package com.mcindoe.dashstreamer.views;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.DeleteFileTask;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.ClipRequestListener;
import com.mcindoe.dashstreamer.models.VideoClip;
import com.mcindoe.dashstreamer.models.VideoControlListener;


/**
 * A placeholder fragment containing a simple view.
 */
public class VideoFragment extends Fragment implements ClipQueue {
	
	private final static int MAX_CLIPS_BUFFERED = 6;

	//UI Elements from our layout xml
	private VideoView mVideoView;
	private ImageButton mPlayPauseButton;
	private SeekBar mVideoSeekBar;
	private TextView mVideoTimeTextView;
	private LinearLayout mControllerLayout;
	
	//Timers for video events
	private Timer mControllerTimer;
	private Timer mSeekBarTimer;

	//Variables to keep track of current video progress
	private static final int WANTS_TO_PLAY = 1, PLAYING = 2, PAUSED = 3, NOT_PLAYING = 4;
	private int mVideoState;
	private int currClipNum, numClips, clipLength, videoLength;
	private Queue<VideoClip> clipsToPlay;
	
	//The source activity for this fragment
	private Activity mSourceActivity;
	
	//Our listener for clip requests.
	private ClipRequestListener mClipRequestListener;
	
	//Allows us to let to source activity know when the video has been loaded.
	private VideoControlListener mVideoControlListener;
	
	//Some variables for managing the fragment lifecycle.
	private boolean videoInterrupted, startVideoOnResume;
	private int interruptPosition;
	
	//Some variables for seeking to the middle of a clip.
	private int seekPosition;
	private boolean seekOnStart;
	
	//Variables for our video controller.
	private static final int SHOWN = 1, HIDING = 2, HIDDEN = 3, SHOWING = 4;
	private int mControllerState;
	private boolean keepControllerShown;
	private ObjectAnimator mShowControllerAnimator;
	private ObjectAnimator mHideControllerAnimator;
	
	//Strings for getting values from args bundles
	public static final String NUM_CLIPS = "AE01";
	public static final String VIDEO_LENGTH = "AF02";
	public static final String CLIP_LENGTH = "BF02";
	
	public VideoFragment() {

		//Inits various video playback variables
		currClipNum = 0;
		keepControllerShown = true;
		
		//Inits our timers
		mControllerTimer = new Timer();
		mSeekBarTimer = new Timer();
		
		//our controller starts in the shown state.
		mControllerState = SHOWN;
		
		//Our video starts in the not playing state.
		mVideoState = NOT_PLAYING;
		
		//Initialize our clips to play queue as a linked list.
		clipsToPlay = new LinkedList<VideoClip>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Retain the instance of this video fragment when a config change happens
		setRetainInstance(true);

		//Inflate our view from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_video, container, false);

		//Initialize the video player variables.
		this.numClips = getArguments().getInt(NUM_CLIPS, 0);
		this.videoLength = getArguments().getInt(VIDEO_LENGTH, 0);
		this.clipLength = getArguments().getInt(CLIP_LENGTH, 10);
		
		//Grab some views.
		mVideoView = (VideoView)rootView.findViewById(R.id.my_video_view);
		mControllerLayout = (LinearLayout)rootView.findViewById(R.id.controller_layout);
		mPlayPauseButton = (ImageButton)rootView.findViewById(R.id.play_pause_button);
		mVideoSeekBar = (SeekBar)rootView.findViewById(R.id.video_seek_bar);
		mVideoTimeTextView = (TextView)rootView.findViewById(R.id.time_text_view);
		
		//When the current video finishes, start the next one if available.
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				//Poll the queue of clips.  We want to delete this clip later.
				VideoClip clipToDelete = clipsToPlay.poll();
				
				//If this was the last clip in our video.
				if(clipToDelete.getClipNum() == (numClips-1)) {
					
					//Set the state of the video to not playing.
					mVideoState = NOT_PLAYING;

					//Show the video controller and don't hide it.
					keepControllerShown = true;
					showController();

					//Set the play/pause button icon to the play image.
					mPlayPauseButton.setImageResource(R.drawable.play_icon);
					
					//Reset the current clip number back to zero.
					currClipNum = 0;
					
					//cancel our seekbar update timer.
					mSeekBarTimer.cancel();
				}
				//If we weren't at the end of the video, but there are no clips ready to play.
				else if(clipsToPlay.isEmpty()) {
					
					//Set the state of the video to show we want to play, but can't.
					mVideoState = WANTS_TO_PLAY;
					
					//Request the next clip.
					if(mClipRequestListener != null) {
						mClipRequestListener.requestClip(clipToDelete.getClipNum() + 1);
					}
				}
				//If we weren't at the end of the video and there are clips ready to play.
				else {
					
					//Set the state of the video to show we are playing now.
					mVideoState = PLAYING;
					
					//Update the video path and start the new video.
					updateVideoPath();
					startVideo(false);

					//Let our clip request listener know that a clip has been completed
					if(mClipRequestListener != null) {
						mClipRequestListener.clipCompleted();
					}
				}

				deleteClip(clipToDelete);
			}
		});
		
		//Sets an on touch listener to our video view so we can make the
		// controller reappear.
		mVideoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				//If the video is playing right now, then show the controller
				// and hide it again after 2 seconds
				if(mVideoView.isPlaying()) {
					keepControllerShown = false;
					showController();
				}
				return true;
			}
			
		});
		
		//Sets up our play/pause button with a click listener
		mPlayPauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//If the video is playing, pause it.
				if(mVideoView.isPlaying()) {
					pauseVideo();
				}
				//If the video is not playing, play it...
				else {
					playVideo();
				}
					
			}
		});
		
		//Set the max value of the seek bar to the length of the video in seconds.
		mVideoSeekBar.setMax(videoLength - 1);
		mVideoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			private int origClipNum;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				//Make sure we're reacting to changes caused by the user.
				if(fromUser) {
					
					//Updates the current clip number as well as the text view for time.
					currClipNum = progress/clipLength;
					updateVideoTimeTextView(progress*1000);
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				//Store the clip number we were originall on
				// this is to optimize content loading and reduce unecessary loads
				origClipNum = currClipNum;
				
				//pauses the video while the user seeks
				pauseVideo();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				//Tells our video where to seek to when it starts.
				prepareSeek((seekBar.getProgress()%clipLength)*1000);
				
				//If we changed to a clip we already have loaded then
				// we don't need to make a new request, we can just move ahead that
				// amount of clips.
				if(currClipNum >= origClipNum && currClipNum < origClipNum + clipsToPlay.size()) {
					
					//pops and deletes the right number of clips from our queue.
					for(int i = 0; i < (currClipNum-origClipNum); i++) {
						deleteClip(clipsToPlay.poll());
					}

					//updates the video path with the new clip number and starts the video.
					updateVideoPath();

					//Starts the video where you stopped seeking.
					playVideo();
				}
				//If the clip selected is not one of the clips we have already.
				else {
					
					//We're changing to a want to play state.
					mVideoState = WANTS_TO_PLAY;
					
					//Request the selected clip.
					if(mClipRequestListener != null) {
						mClipRequestListener.requestClip(currClipNum);
					}
				}
			}
		});
		
		//Sets up our controller animators
		setupControllerAnimators();

		return rootView;
	}
	
	/**
	 * Indicates there was a request to pause the video from
	 * the user.  Manages state of video.
	 */
	public void pauseVideo() {
		
		//State machine for playing our video.
		switch(mVideoState) {

		//If the video wants to play.
		case WANTS_TO_PLAY:
			
			//Change the video state to say we don't want to play right now.
			mVideoState = NOT_PLAYING;
			break;

		//If the video is playing.
		case PLAYING:
			
			//Change the state of the video to the paused state.
			mVideoState = PAUSED;

			//Pauses the video.
			mVideoView.pause();

			//Cancels the timer that updates the seek bar
			mSeekBarTimer.cancel();

			//Changes the play/pause button icon to the play symbol.
			mPlayPauseButton.setImageResource(R.drawable.play_icon);

			//Shows the controller permanently.
			keepControllerShown = true;
			showController();
			break;

		//If the video is already not playing
		case NOT_PLAYING:
			//Do nothing.
			break;

		//If the video is already paused.
		case PAUSED:
			//Do nothing.
			break;
		}
	}
	
	/**
	 * Indicates there was a request to play the video from
	 * the user.  Manages state of video.
	 */
	public void playVideo() {
		
		//State machine for playing our video.
		switch(mVideoState) {
		
		//If the video is currently not playing
		case NOT_PLAYING:
			
			//If we don't have clips to play.
			if(clipsToPlay.isEmpty()) {
				
				//Switch to the wants to play state.
				mVideoState = WANTS_TO_PLAY;

				//Make a request to our clip request listener.
				if(mClipRequestListener != null) {
					mClipRequestListener.requestClip(currClipNum);
				}
			}
			//If we have clips to play.
			else {
				
				//Switch to the playing state.
				mVideoState = PLAYING;
				
				//Sets up the video path for our video.
				updateVideoPath();
				
				//Starts playing the video.
				startVideo(true);
			}
			break;
			
		//If the video is currently paused.
		case PAUSED:
			
			//Set the video state to playing.
			mVideoState = PLAYING;
			
			//Start the video.
			startVideo(true);

			break;
			
		//If the video wants to play already.
		case WANTS_TO_PLAY:
			//Do nothing
			break;
		
		//If the video is already playing.
		case PLAYING:
			//Do nothing
			break;
		}
	}
	
	/**
	 * Override the default onResume function to include resuming our video
	 */
	@Override
	public void onResume() {
		super.onResume();

		//If we're resuming from an interrupted video
		if(videoInterrupted) {
			
			//Make sure we reload the media player in case it was used elsewhere.
			updateVideoPath();

			//Prepares the video to seek to the given position when starting.
			prepareSeek(interruptPosition);
			
			//Start the video if we exited in a state that desires that.
			if(startVideoOnResume) {
				mVideoState = PLAYING;
				startVideo(true);
			}
			//Else pause the video and seek it to the position it was interrupted at.
			else {
				mVideoState = PAUSED;
			}

			//Calculates the current time of the video according to cliplength, current
			// clip number and the current position in the clip that is playing...
			int currTime = clipLength*1000*currClipNum + interruptPosition;

			//Update the progress bar and video time text view with the new time.
			mVideoSeekBar.setProgress(currTime/1000);
			updateVideoTimeTextView(currTime);
		}

		//Reset our interrupted boolean to false.
		videoInterrupted = false;
	}
	
	/**
	 * Override the default onPause function to include pausing our video
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		switch(mVideoState) {

		//If we were in the middle of playing the video when the fragment pauses...
		case PLAYING:
			
			//Set our video state variables and pause the video.
			videoInterrupted = true;
			startVideoOnResume = true;
			interruptPosition = mVideoView.getCurrentPosition();
			pauseVideo();
			break;
			
		//If we were in the middle of the video, but paused when the fragment pauses...
		case PAUSED:
			
			//Set our video state variables and pause the video.
			videoInterrupted = true;
			startVideoOnResume = false;
			interruptPosition = mVideoView.getCurrentPosition();
			pauseVideo();
			break;
			
		//If we're currently waiting on clips, just pause the video.
		case WANTS_TO_PLAY:
			pauseVideo();
			break;
		}
	}
	
	/**
	 * Actually starts our video view to play the video.
	 * @param position - the position we want to start the video at.
	 * @param showController - true if we want to show the controller
	 * 						  false if we don't want to show the controller
	 */
	private void startVideo(boolean showController) {


		//Cancels any timer that happens to be running on our
		// seek bar timer and then restarts it.
		mSeekBarTimer.cancel();
		mSeekBarTimer = new Timer();
		
		if(seekOnStart) {

			//Seek to the requested position.
			mVideoView.seekTo(seekPosition);
			seekOnStart = false;

			//If we start this instantly it will load the wrong time to the UI for a half
			// a second because of the fact that we seeked on this start.  So we want a 
			// one second delay before the timer starts to give the videoview time to update.
			mSeekBarTimer.scheduleAtFixedRate(new UpdateVideoUITimerTask(), 1000, 1000);
		}
		else {
			//If we didn't seek, just start the UI update timer instantly.
			mSeekBarTimer.scheduleAtFixedRate(new UpdateVideoUITimerTask(), 0, 1000);
		}

		//Starts the video.
		mVideoView.start();

		//Changes the play/pause button icon to the pause symbol.
		mPlayPauseButton.setImageResource(R.drawable.pause_icon);

		//If we specified we want the controller to be shown as this
		// video starts, then show the controller for 2 seconds.
		if(showController) {
			//Shows the controller, but has it fade away after 2 seconds.
			keepControllerShown = false;
			showController();
		}
	}
	
	/**
	 * Sets up the video to seek to the given position the next time it's played
	 * @param pos - the position we want to seek to.
	 */
	public void prepareSeek(int pos) {
		seekOnStart = true;
		seekPosition = pos;
	}

	/**
	 * Updates the video path of our Video View to the next video clip.
	 */
	private void updateVideoPath() {

		//Peeks at the clip at the top of our play list.
		VideoClip clip = clipsToPlay.peek();

		//Sets the current clip number accordingly.
		currClipNum = clip.getClipNum();

		//Sets the filepath of our videoview accordingly.
		Log.d(Utils.LOG_TAG, "Setting video path: " + clip.getFilePath());
		mVideoView.setVideoPath(clip.getFilePath());

		//Tell our listener that the video has been loaded.
		if(mVideoControlListener != null) {
			mVideoControlListener.videoLoaded();
		}
	}

	/**
	 * Adds a clip to our clipsToPlay queue - this will be called 
	 * from our clip request listener once a clip is available.
	 */
	@Override
	public void addClipToQueue(VideoClip videoClip) {

		//Add the given clip to our queue.
		clipsToPlay.add(videoClip);
		
		//If the video is currently waiting to play, then start the video.
		if(mVideoState == WANTS_TO_PLAY) {
			mVideoState = PLAYING;
			updateVideoPath();
			startVideo(true);
		}
	}
	
	/**
	 * Custom timer task that simply hides our video controller when run.
	 */
	private class UpdateVideoUITimerTask extends TimerTask {

		@Override
		public void run() {
			
			//We have to run this on our UI thread according to Android OS.
			mSourceActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					//Calculates the current time of the video according to cliplength, current
					// clip number and the current position in the clip that is playing...
					int currTime = clipLength*1000*currClipNum + mVideoView.getCurrentPosition();
					
					//Update the progress bar and video time text view with the new time.
					mVideoSeekBar.setProgress(currTime/1000);
					updateVideoTimeTextView(currTime);
				}
			});
		}
	}
	
	/**
	 * Updates the video time text view with the appropriate numbers
	 * according to the given time.
	 * @param currTime - the current time of the video.
	 */
	public void updateVideoTimeTextView(int currTime) {

		//If the video is past one hour, show the hour mark
		if(currTime > 3600000) {
			mVideoTimeTextView.setText(String.format("%d:%02d:%02d", currTime/3600000, (currTime/60000)%60, (currTime/1000)%60));
		}
		//Otherwise save space and don't show hours.
		else {
			mVideoTimeTextView.setText(String.format("%d:%02d", currTime/60000, (currTime/1000)%60));
		}
	}
	
	/**
	 * Shows the controller at the bottom of our video view.
	 */
	public void showController() {

		switch(mControllerState) {

		//If the controller is in a showing or shown state,
		// reset the hide controller timer.
		case SHOWING:
		case SHOWN:
			startHideControllerTimer();
			break;

		//If the controller is currently in the process of hiding,
		// then cancel the animation.
		case HIDING:
			mHideControllerAnimator.cancel();
			break;

		//If the controller is already fully hidden, just start the 
		// show controller animation.
		case HIDDEN:
			mShowControllerAnimator.start();
			break;
		}
	}
	
	/**
	 * Starts our TimerTask that hides the video controller.
	 */
	public void startHideControllerTimer() {

		//Cancel any current timer if there was one, reset the timer,
		// and then schedule a new task to hide the controller.
		mControllerTimer.cancel();
		mControllerTimer = new Timer();
		if(!keepControllerShown) {
			mControllerTimer.schedule(new HideControllerTimerTask(), 2000);
		}
	}
	
	/**
	 * Custom timer task that simply hides our video controller when run.
	 */
	private class HideControllerTimerTask extends TimerTask {

		@Override
		public void run() {
			
			//We have to run this on our UI thread according to Android OS.
			mSourceActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//Hide the controller if this timer actually executes.
					mHideControllerAnimator.start();
				}
			});
		}
	}
	
	/**
	 * Initializes our video controller animators.
	 */
	public void setupControllerAnimators() {

		mShowControllerAnimator = ObjectAnimator.ofFloat(mControllerLayout, "alpha", 0, 1);
		mShowControllerAnimator.setDuration(500);
		
		//Adds a listener for this animator so we keep track of our video controller state.
		mShowControllerAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				//When animation starts, set controller state properly
				// and set the visibility of the layout to visible.
				mControllerState = SHOWING;
				mControllerLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				//When the animation ends, set the controller state properly
				// and start the hide controller timer.
				mControllerState = SHOWN;
				startHideControllerTimer();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});

		mHideControllerAnimator = ObjectAnimator.ofFloat(mControllerLayout, "alpha", 1, 0);
		mHideControllerAnimator.setDuration(500);

		//Adds a listener for this animator so we keep track of our video controller state.
		mHideControllerAnimator.addListener(new AnimatorListener() {
			
			private boolean cancelled = false;

			@Override
			public void onAnimationStart(Animator animation) {
				//Change the controller state properly.
				mControllerState = HIDING;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				//If this wasn't called after the animation was 
				// cancelled, then set the state properly and 
				// make the controller not visible.
				if(!cancelled) {
					mControllerState = HIDDEN;
					mControllerLayout.setVisibility(View.GONE);
				}
				else {
					cancelled = false;
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				//If the animation is cancelled, put the controller
				// in the shown state with the proper visibility settings.
				mControllerState = SHOWN;
				mControllerLayout.setVisibility(View.VISIBLE);
				mControllerLayout.setAlpha(1);
				startHideControllerTimer();
				cancelled = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//Creates our listeners to the attached activity.
		mVideoControlListener = (PlayActivity)activity;
		mClipRequestListener = (PlayActivity)activity;
		
		//Grabs the source activity, used to run hideController() on the UI thread in timertask runnables
		mSourceActivity = activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		//Cancels our controller timer on the chance it was running when this fragment detaches.
		mControllerTimer.cancel();
		
		//Removes all references to the previously attached activity.
		mVideoControlListener = null;
		mClipRequestListener = null;
		mSourceActivity = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		//Clears any remaining video clips off the sdcard.
		clear();
	}

	/**
	 * Checks to see if there is still room in our clipstoplay buffer
	 */
	@Override
	public boolean hasRoom() {
		return (clipsToPlay.size() < MAX_CLIPS_BUFFERED);
	}

	/**
	 * Empties the Clip Queue and frees up the space from the hard drive.
	 */
	@Override
	public void clear() {
		
		while(!clipsToPlay.isEmpty()) {
			(new DeleteFileTask()).execute(clipsToPlay.poll().getFilePath());
		}
	}
	
	/**
	 * Deletes the given clip off the sdcard.
	 * @param clip - the clip to delete.
	 */
	public void deleteClip(VideoClip clip) {
		(new DeleteFileTask()).execute(clip.getFilePath());
	}
}
