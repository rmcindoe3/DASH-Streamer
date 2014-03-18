package com.mcindoe.dashstreamer.views;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
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
import com.mcindoe.dashstreamer.controllers.Utils;


/**
 * A placeholder fragment containing a simple view.
 */
public class VideoFragment extends Fragment {

	private VideoView mVideoView;
	private ImageButton mPlayPauseButton;
	private SeekBar mVideoSeekBar;
	private TextView mVideoTimeTextView;

	private int currClipNum, numClips, clipLength, videoLength;
	private String filePath;
	
	private LinearLayout mControllerLayout;
	private Timer mControllerTimer;
	
	private Timer mSeekBarTimer;
	
	private Activity mSourceActivity;
	
	private static final int SHOWN = 1, HIDING = 2, HIDDEN = 3, SHOWING = 4;
	private int mControllerState;
	private boolean keepControllerShown;
	
	private ObjectAnimator mShowControllerAnimator;
	private ObjectAnimator mHideControllerAnimator;
	
	public static final String NUM_CLIPS = "AE01";
	public static final String FILE_PATH = "AE02";
	public static final String VIDEO_LENGTH = "AF02";
	public static final String CLIP_LENGTH = "BF02";
	
	public VideoFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//Inflate our view from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_video, container, false);
		
		//Grabs the source activity, used to run hideController on the UI thread in timertask runnables
		mSourceActivity = getActivity();

		//Initialize the video player variables.
		this.currClipNum = 0;
		this.numClips = getArguments().getInt(NUM_CLIPS, 0);
		this.filePath = getArguments().getString(FILE_PATH, "");
		this.videoLength = getArguments().getInt(VIDEO_LENGTH, 0);
		this.clipLength = getArguments().getInt(CLIP_LENGTH, 10);
		keepControllerShown = true;
		
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

				//If there is still more video clips to be played, start them...
				currClipNum++;
				if(updateVideoPath()) {
					mVideoView.start();
				}
				//Otherwise set the play/pause button to the play icon and 
				// wait for the user to press play.
				else {
					keepControllerShown = true;
					showController();
					mPlayPauseButton.setImageResource(R.drawable.play_icon);
				}
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
					startVideo();
				}
					
			}
		});
		
		//Set the max value of the seek bar to the length of the video in seconds.
		mVideoSeekBar.setMax(videoLength);
		mVideoSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
				
				//pauses the video while the user seeks
				pauseVideo();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				//updates the video path with the new clip number and starts the video.
				updateVideoPath();
				
				//Starts the video where you stopped seeking.
				startVideo();
			}
		});
		
		//Setup the video path to the start of the first video.
		updateVideoPath();
		
		//Inits our timers
		mControllerTimer = new Timer();
		mSeekBarTimer = new Timer();
		
		//our controller starts in the shown state.
		mControllerState = SHOWN;
		
		//Sets up our controller animators
		setupControllerAnimators();

		return rootView;
	}
	
	/**
	 * Pauses the our currently playing video.
	 */
	public void pauseVideo() {
		
		//Pauses the video.
		mVideoView.pause();
		
		//Cancels the timer that updates the seek bar
		mSeekBarTimer.cancel();

		//Changes the play/pause button icon to the play symbol.
		mPlayPauseButton.setImageResource(R.drawable.play_icon);

		//Shows the controller permanently.
		keepControllerShown = true;
		showController();
	}
	
	/**
	 * Starts our video
	 */
	public void startVideo() {

		//Cancels any timer that happens to be running on the our
		// seek bar timer and then restarts it.
		mSeekBarTimer.cancel();
		mSeekBarTimer = new Timer();
		mSeekBarTimer.scheduleAtFixedRate(new UpdateVideoUITimerTask(), 0, 1000);

		//Starts the video.
		mVideoView.start();

		//Changes the play/pause button icon to the pause symbol.
		mPlayPauseButton.setImageResource(R.drawable.pause_icon);

		//Shows the controller, but has it fade away after 2 seconds.
		keepControllerShown = false;
		showController();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		pauseVideo();
	}

	/**
	 * Updates the video path of our Video View to the next video clip.
	 * @return - true if the video is ready to be started
	 * 		   - false if there are no more videos to be played.
	 */
	private boolean updateVideoPath() {

		//If this is the last video clip for this video...
		if(currClipNum == numClips) {
			
			//reset the video number counter and return false to not play
			currClipNum = 0;
			mVideoView.setVideoPath(getCurrentVideoFilePath());
			return false;
		}

		Log.d(Utils.LOG_TAG, "Setting video path: " + getCurrentVideoFilePath());
		mVideoView.setVideoPath(getCurrentVideoFilePath());

		return true;
	}

	/**
	 * Gets the file path for the current video we want to play.
	 * @return - String describing the file location of the next video clip to play.
	 */
	private String getCurrentVideoFilePath() {
		return Environment.getExternalStorageDirectory() + filePath + currClipNum + ".mp4";
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
	
}
