package com.mcindoe.dashstreamer.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcindoe.dashstreamer.R;

public class VideoControlFragment extends Fragment {
	
	public VideoControlFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//Inflate our view from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_video_control, container, false);


		return rootView;
	}
	

}
