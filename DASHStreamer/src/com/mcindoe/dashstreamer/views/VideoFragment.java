package com.mcindoe.dashstreamer.views;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.Utils;


/**
 * A placeholder fragment containing a simple view.
 */
public class VideoFragment extends Fragment {

	private VideoView mVideoView;
	private ImageButton mPlayPauseButton;
	private int currVideoNum, numVideos;
	private String filePath;
	
	public static final String NUM_VIDEOS = "AE01";
	public static final String FILE_PATH = "AE02";
	
	public VideoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_video, container, false);

		//Initialize the video player variables.
		this.currVideoNum = 0;
		this.numVideos = getArguments().getInt(NUM_VIDEOS, 0);
		this.filePath = getArguments().getString(FILE_PATH, "");
		
		//Grab out video view.
		mVideoView = (VideoView)rootView.findViewById(R.id.my_video_view);
		
		//When the current video finishes, start the next one if available.
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				if(updateVideoPath()) {
					mVideoView.start();
				}
				else {
					mPlayPauseButton.setImageResource(R.drawable.play_icon);
				}
			}
		});

		mPlayPauseButton = (ImageButton)rootView.findViewById(R.id.play_pause_button);
		
		mPlayPauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//If the video is playing, pause it.
				if(mVideoView.isPlaying()) {
					mVideoView.pause();
					mPlayPauseButton.setImageResource(R.drawable.play_icon);
				}
				//If the video is not playing, play it...
				else {
					mVideoView.start();
					mPlayPauseButton.setImageResource(R.drawable.pause_icon);
				}
			}
		});
		
		//Setup the video path to the start of the first video.
		updateVideoPath();
		
		return rootView;
	}

	@Override
	public void onPause() {
		
		//TODO: Deal with activity pausing...

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * Updates the video path of our Video View to the next video clip.
	 * @return - true if the video is ready to be started
	 * 		   - false if there are no more videos to be played.
	 */
	private boolean updateVideoPath() {

		//If this is the last video clip for this video...
		if(currVideoNum == numVideos) {
			
			//reset the video number counter and return false to not play
			currVideoNum = 0;
			mVideoView.setVideoPath(getCurrentVideoFilePath());
			return false;
		}

		Log.d(Utils.LOG_TAG, "Setting video path: " + getCurrentVideoFilePath());
		mVideoView.setVideoPath(getCurrentVideoFilePath());

		currVideoNum++;

		return true;
	}

	/**
	 * Gets the file path for the current video we want to play.
	 * @return - String describing the file location of the next video clip to play.
	 */
	private String getCurrentVideoFilePath() {
		return Environment.getExternalStorageDirectory() + filePath + currVideoNum + ".mp4";
	}
}
