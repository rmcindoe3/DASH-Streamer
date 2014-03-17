package com.mcindoe.dashstreamer.views;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.Utils;


/**
 * A placeholder fragment containing a simple view.
 */
public class VideoFragment extends Fragment {

	private VideoView mVideoView;
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

		this.currVideoNum = 0;
		this.numVideos = getArguments().getInt(NUM_VIDEOS, 0);
		this.filePath = getArguments().getString(FILE_PATH, "");
		
		mVideoView = (VideoView)rootView.findViewById(R.id.my_video_view);
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				if(updateVideoPath()) {
					mVideoView.start();
				}
			}
		});
		
		if(updateVideoPath()) {
			mVideoView.start();
		}
		
		return rootView;
	}

	private boolean updateVideoPath() {

		if(currVideoNum == numVideos) {
			return false;
		}

		Log.d(Utils.LOG_TAG, "Setting video path: " + getCurrentVideoFilePath());
		mVideoView.setVideoPath(getCurrentVideoFilePath());

		currVideoNum++;

		return true;
	}

	private String getCurrentVideoFilePath() {
		return Environment.getExternalStorageDirectory() + filePath + String.format("%02d", currVideoNum) + ".mp4";
	}
}
