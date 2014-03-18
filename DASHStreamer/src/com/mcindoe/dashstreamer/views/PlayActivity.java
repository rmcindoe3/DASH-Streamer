package com.mcindoe.dashstreamer.views;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.ClipRequestListener;
import com.mcindoe.dashstreamer.models.VideoClip;

public class PlayActivity extends ActionBarActivity {
	
	public static final String VIDEO_TITLE = "AE03";
	
	private ArrayList<VideoClip> clips;

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
			VideoFragment vidFrag = new VideoFragment();
			vidFrag.setArguments(args);
			vidFrag.setClipRequestListener(new ClipRequestListener() {

				@Override
				public void requestClip(ClipQueue queue, int clipNum) {
					
					Log.d(Utils.LOG_TAG, "Clip has been requested: " + clipNum);
					queue.addClipToQueue(clips.get(clipNum));
				}
			});
			
			for(int i = 0; i < 5; i++) {
				vidFrag.addClipToQueue(clips.get(i));
			}

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
