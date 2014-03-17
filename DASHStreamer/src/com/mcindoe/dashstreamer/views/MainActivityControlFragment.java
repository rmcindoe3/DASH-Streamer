package com.mcindoe.dashstreamer.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcindoe.dashstreamer.R;

public class MainActivityControlFragment extends Fragment {
	
	private Fragment mFragment;

	public MainActivityControlFragment() {
		
		mFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		Button startVideoButton = (Button)rootView.findViewById(R.id.start_video_button);

		startVideoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mFragment.getActivity(), PlayActivity.class);
				intent.putExtra(PlayActivity.VIDEO_TITLE, "Breaking Bad S05E13");
				startActivityForResult(intent, 0);
			}
		});

		return rootView;
	}
}
