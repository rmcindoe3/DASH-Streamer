package com.mcindoe.dashstreamer.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.models.VideoControlListener;

public class VideoControlFragment extends Fragment {
	
	private VideoControlListener mVideoControlListener;
	private Button mFullscreenButton;
	
	public VideoControlFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Retain the instance of this video fragment when a config change happens
		setRetainInstance(true);

		//Inflate our view from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_video_control, container, false);

		mFullscreenButton = (Button)rootView.findViewById(R.id.fullscreen_button);
		
		mFullscreenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mVideoControlListener != null) {			
					mVideoControlListener.switchToFullscreen();
				}
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		//Sets our video control listener to the attached activity.
		mVideoControlListener = (PlayActivity)activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		//removes reference to previously attached activity.
		mVideoControlListener = null;
	}
}
