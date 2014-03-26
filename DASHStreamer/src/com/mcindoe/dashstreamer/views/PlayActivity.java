package com.mcindoe.dashstreamer.views;

import android.app.ActionBar.LayoutParams;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.DASHManager;
import com.mcindoe.dashstreamer.controllers.DASHStreamerApplication;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.ClipRequestListener;
import com.mcindoe.dashstreamer.models.MediaPresentation;
import com.mcindoe.dashstreamer.models.VideoControlListener;

public class PlayActivity extends ActionBarActivity implements VideoControlListener, ClipRequestListener {
	
	public static final String MPD_URL = "NDNK";
	
	private VideoFragment mVideoFragment;
	private VideoControlFragment mVideoControlFragment;
	
	private boolean videoLoaded;
	private boolean playingFullscreen;

	private MediaPresentation mMediaPresentation;
	private DASHManager mDASHManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		mMediaPresentation = ((DASHStreamerApplication)getApplication()).getCurrentMediaPresentation();

		playingFullscreen = false;
		videoLoaded = false;

		//Gets our fragments.
		FragmentManager fm = getSupportFragmentManager();
		mVideoFragment = (VideoFragment)fm.findFragmentByTag("mVideoFragment");
		mVideoControlFragment = (VideoControlFragment)fm.findFragmentByTag("mVideoControlFragment");

		//If our video fragment has not already been created.
		if(mVideoFragment == null) {

			//Create our video fragment and add our bundle to it.
			mVideoFragment = new VideoFragment();
			
			Bundle args = new Bundle();
			args.putInt(VideoFragment.NUM_CLIPS, mMediaPresentation.getNumberOfClips());
			args.putInt(VideoFragment.CLIP_LENGTH, mMediaPresentation.getSegmentLength()/1000);
			args.putInt(VideoFragment.VIDEO_LENGTH, mMediaPresentation.getDuration()/1000);
			
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
		
		mDASHManager = new DASHManager(mMediaPresentation, mVideoFragment);
	}

	@Override
	public void switchToFullscreen() {
		
		//Change the screen orientation.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		//Hide the action bar and status bar.
		getActionBar().hide();
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN);

		//Make the video container layout fill the screen.
		FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		fl.setLayoutParams(params);

		//Tells the activity to re-evaluate all of it's parameters
		findViewById(R.id.play_activity).requestLayout();
		
		playingFullscreen = true;
	}

	@Override
	public void onBackPressed() {
		
		//If we're in fullscreen mode then just switch back to non fullscreen.
		if(playingFullscreen) {
			
			//Change the screen orientation.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
			if(videoLoaded) {
				//Changes the layout of our video container to wrap it's contents.
				FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
				params.height = LayoutParams.WRAP_CONTENT;
				fl.setLayoutParams(params);
			} 
			else {
				//Set the height of the video container to 200dp.
				FrameLayout fl = (FrameLayout)findViewById(R.id.video_container);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
				params.height = Utils.getDensityIndependentPixels(200, this);
				fl.setLayoutParams(params);
			}

			//Tells the activity to re-evaluate all of it's parameters
			findViewById(R.id.play_activity).requestLayout();

			//Hide the action bar and status bar.
			getActionBar().show();
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			
			playingFullscreen = false;
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
