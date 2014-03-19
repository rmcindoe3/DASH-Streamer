package com.mcindoe.dashstreamer.views;

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
	
	public void setVideoControlListener(VideoControlListener vcl) {
		this.mVideoControlListener = vcl;
	}
	

}
