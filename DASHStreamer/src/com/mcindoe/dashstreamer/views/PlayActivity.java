package com.mcindoe.dashstreamer.views;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.ClipRequestListener;
import com.mcindoe.dashstreamer.models.VideoClip;
import com.mcindoe.dashstreamer.models.VideoControlListener;

public class PlayActivity extends ActionBarActivity implements VideoControlListener, ClipRequestListener {
	
	public static final String VIDEO_TITLE = "AE03";
	public static final String FULLSCREEN = "932J";
	
	private VideoFragment mVideoFragment;
	private VideoControlFragment mVideoControlFragment;
	
	private boolean videoLoaded;
	private boolean playingFullscreen;

	private ArrayList<VideoClip> testingClips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		setTitle(getIntent().getExtras().getString(VIDEO_TITLE, "Video"));
		playingFullscreen = getIntent().getExtras().getBoolean(FULLSCREEN, false);
		
		videoLoaded = false;

		testingClips = new ArrayList<VideoClip>();
		testingClips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_0.mp4", 0));
		testingClips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_1.mp4", 1));
		testingClips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_2.mp4", 2));
		testingClips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_3.mp4", 3));
		testingClips.add(new VideoClip(Environment.getExternalStorageDirectory() + "/DASHStreamer/bb_10s_4.mp4", 4));

		//Gets our fragments.
		FragmentManager fm = getSupportFragmentManager();
		mVideoFragment = (VideoFragment)fm.findFragmentByTag("mVideoFragment");
		mVideoControlFragment = (VideoControlFragment)fm.findFragmentByTag("mVideoControlFragment");

		//If our video fragment has not already been created.
		if(mVideoFragment == null) {

			//Create our bundle for our video fragment.
			Bundle args = new Bundle();
			args.putInt(VideoFragment.NUM_CLIPS, 5);
			args.putInt(VideoFragment.CLIP_LENGTH, 10);
			args.putInt(VideoFragment.VIDEO_LENGTH, 50);

			//Create our video fragment and add our bundle to it.
			mVideoFragment = new VideoFragment();
			mVideoFragment.setArguments(args);

			//Add the video fragment to our container.
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.video_container, mVideoFragment, "mVideoFragment");
			ft.commit();
		}
		
		//If our video control fragment has not been created...
		if(mVideoControlFragment == null) {
			
			//Create it.
			mVideoControlFragment = new VideoControlFragment();

			//Add the video control fragment to our container.
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(R.animator.rising_fade_in, 0);
			ft.add(R.id.control_container, mVideoControlFragment, "mVideoControlFragment");
			ft.commit();
		}
		
		//If we're in fullscreen mode...
		if(playingFullscreen) {
			
			//Hide the action bar and status bar.
			getActionBar().hide();
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);

			//Make the video container layout fill the screen.
			FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
			params.height = LayoutParams.MATCH_PARENT;
			fl.setLayoutParams(params);
		}
		//If we're not in fullscreen mode...
		else {
			
			//Set the height of the video container to 200dp.
			FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
			params.height = Utils.getDensityIndependentPixels(200, this);
			fl.setLayoutParams(params);
		}

		//Tells the activity to re-evaluate all of it's parameters
		findViewById(R.id.play_activity).requestLayout();
	}

	@Override
	public void switchToFullscreen() {
		
		//Tells us that we want to be in full screen mode when the activity recreates.
		getIntent().putExtra(FULLSCREEN, true);
		
		//Change the screen orientation.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public void onBackPressed() {
		
		//If we're in fullscreen mode then just switch back to non fullscreen.
		if(playingFullscreen) {
			
			//Tells us that we don't want to be in fullscreen mode when the activity recreates.
			getIntent().putExtra(FULLSCREEN, false);
			
			//Change the screen orientation.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		//Otherwise go back to the previous activity.
		else {
			super.onBackPressed();
			//Mirrors our activity entrance animation.
			overridePendingTransition(R.animator.enter_previous_activity, R.animator.exit_next_activity);
		}
	}

	/**
	 * This function is called when our video fragment is 
	 * waiting on a clip to be added to it's queue.
	 */
	@Override
	public void requestClip(ClipQueue queue, int clipNum) {

		queue.addClipToQueue(testingClips.get(clipNum));
	}

	/**
	 * Called when the video is loaded and about to start playing in our VideoFragment.
	 * The purpose of this method is to allocate the correct video dimensions to the 
	 * video fragment container once the video height is loaded in the player.
	 */
	@Override
	public void videoLoaded() {

		//Once the video has been loaded once, we don't want to to spend time reassigning
		// this layout's height because it automatically handles it from here.
		if(!videoLoaded) {
			
			videoLoaded = true;
			
			//Changes the layout of our video container to wrap it's contents.
			FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			fl.setLayoutParams(params);
		}
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
}
