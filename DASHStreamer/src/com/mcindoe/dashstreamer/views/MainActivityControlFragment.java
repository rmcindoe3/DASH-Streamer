package com.mcindoe.dashstreamer.views;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.DASHStreamerApplication;
import com.mcindoe.dashstreamer.controllers.MPDParser;
import com.mcindoe.dashstreamer.controllers.MPDParser.MPDLoadedListener;
import com.mcindoe.dashstreamer.models.MediaPresentation;

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

				new MPDParser("http://10.0.0.3:4573/MPDs/iasip_s09_e01_mpd.xml", new MPDLoadedListener() {

					@Override
					public void onMediaPresentationsLoaded(ArrayList<MediaPresentation> mpds) {

						((DASHStreamerApplication)mFragment.getActivity().getApplication()).setCurrentMediaPresentation(mpds.get(0));

						Intent intent = new Intent(mFragment.getActivity(), PlayActivity.class);
						startActivityForResult(intent, 0);
						mFragment.getActivity().overridePendingTransition(R.animator.enter_next_activity, R.animator.exit_current_activity);
					}
				});
			}
		});

		return rootView;
	}
}
