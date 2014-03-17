package com.mcindoe.dashstreamer.views;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.VideoView;

import com.mcindoe.dashstreamer.R;

public class MainActivity extends Activity {
	
	private VideoView mVideoView;
	private int currVideoNum, numVideos;
	private final static String LOG_TAG = "DASH Streamer";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		currVideoNum = 0;
		numVideos = 5;
		
		mVideoView = (VideoView)findViewById(R.id.my_video_view);

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				//Updates the video path 
				if(updateVideoPath()) {
					mVideoView.start();
				}
			}
		});

		if(updateVideoPath()) {
			mVideoView.start();
		}
	}
	
	private boolean updateVideoPath() {

		if(currVideoNum == numVideos) {
			return false;
		}

		Log.d(LOG_TAG, "Setting video path: " + getCurrentVideoFilePath());
		mVideoView.setVideoPath(getCurrentVideoFilePath());
		
		currVideoNum++;

		return true;
	}
	
	private String getCurrentVideoFilePath() {
		return Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_0" + currVideoNum + ".mp4";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
