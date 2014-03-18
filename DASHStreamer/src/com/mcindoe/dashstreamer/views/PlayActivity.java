package com.mcindoe.dashstreamer.views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mcindoe.dashstreamer.R;

public class PlayActivity extends ActionBarActivity {
	
	public static final String VIDEO_TITLE = "AE03";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		setTitle(getIntent().getExtras().getString(VIDEO_TITLE, "Video"));

		if (savedInstanceState == null) {
		
			//Create our bundle for our video fragment.
			Bundle args = new Bundle();
			args.putInt(VideoFragment.NUM_CLIPS, 5);
			args.putInt(VideoFragment.CLIP_LENGTH, 10);
			args.putInt(VideoFragment.VIDEO_LENGTH, 50);
			args.putString(VideoFragment.FILE_PATH, "/DASHStreamer/bb_10s_");

			//Create our video fragment and add our bundle to it.
			VideoFragment vidFrag = new VideoFragment();
			vidFrag.setArguments(args);

			//Add the video fragment to our container.
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, vidFrag).commit();
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
