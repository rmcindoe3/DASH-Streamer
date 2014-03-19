package com.mcindoe.dashstreamer.views;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.ClipRequestListener;
import com.mcindoe.dashstreamer.models.VideoClip;
import com.mcindoe.dashstreamer.models.VideoControlListener;

public class PlayActivity extends ActionBarActivity implements VideoControlListener {
	
	public static final String VIDEO_TITLE = "AE03";
	
	private ArrayList<VideoClip> clips;
	private VideoFragment mVideoFragment;
	private VideoControlFragment mVideoControlFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		setTitle(getIntent().getExtras().getString(VIDEO_TITLE, "Video"));

		if (savedInstanceState == null) {
			
			clips = new ArrayList<VideoClip>();
			clips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_0.mp4", 0));
			clips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_1.mp4", 1));
			clips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_2.mp4", 2));
			clips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_3.mp4", 3));
			clips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_4.mp4", 4));
			
		
			//Create our bundle for our video fragment.
			Bundle args = new Bundle();
			args.putInt(VideoFragment.NUM_CLIPS, 5);
			args.putInt(VideoFragment.CLIP_LENGTH, 10);
			args.putInt(VideoFragment.VIDEO_LENGTH, 50);

			//Create our video fragment and add our bundle to it.
			mVideoFragment = new VideoFragment();
			mVideoFragment.setArguments(args);
			mVideoFragment.setClipRequestListener(new ClipRequestListener() {

				@Override
				public void requestClip(ClipQueue queue, int clipNum) {
					
					Log.d(Utils.LOG_TAG, "Clip has been requested: " + clipNum);
					queue.addClipToQueue(clips.get(clipNum));
				}
			});
			
			for(int i = 0; i < 5; i++) {
				mVideoFragment.addClipToQueue(clips.get(i));
			}
			
			mVideoControlFragment = new VideoControlFragment();
			mVideoControlFragment.setVideoControlListener(this);

			FragmentManager fm = getSupportFragmentManager();

			//Add the video fragment to our container.
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.video_container, mVideoFragment);
			ft.commit();

			//Add the video control fragment to our container.
			ft = fm.beginTransaction();
			ft.setCustomAnimations(R.animator.rising_fade_in, 0);
			ft.add(R.id.control_container, mVideoControlFragment);
			ft.commit();
		}
	}

	@Override
	public void switchToFullscreen() {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//Mirrors our activity entrance animation.
		overridePendingTransition(R.animator.enter_previous_activity, R.animator.exit_next_activity);
	}

}
