package com.mcindoe.dashstreamer.views;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.controllers.DASHStreamerApplication;
import com.mcindoe.dashstreamer.controllers.MPDParser;
import com.mcindoe.dashstreamer.controllers.Utils;
import com.mcindoe.dashstreamer.controllers.MPDParser.MPDLoadedListener;
import com.mcindoe.dashstreamer.controllers.MPIArrayAdapter;
import com.mcindoe.dashstreamer.controllers.MPIParser;
import com.mcindoe.dashstreamer.controllers.MPIParser.MPILoadedListener;
import com.mcindoe.dashstreamer.models.MediaPresentation;
import com.mcindoe.dashstreamer.models.MediaPresentationIndex;

public class MainActivityControlFragment extends Fragment {

	private Fragment mFragment;
	private ListView mVideoListView;
	private MPIArrayAdapter mMPIArrayAdapter;
	private ArrayList<MediaPresentationIndex> mMPIs;

	public MainActivityControlFragment() {
		mFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		mVideoListView = (ListView)rootView.findViewById(R.id.video_list_view);
		
		String ip_addr = getResources().getString(R.string.ip_addr);

		//Starts a request for the MPIs from the server.
		new MPIParser(mFragment.getActivity(), ip_addr + ":4573/mpi.xml", new MPILoadedListener() {

			/**
			 * Called when the download from the server fails.
			 */
			@Override
			public void onFailedDownload() {
				Log.d(Utils.LOG_TAG, "MPI download failed.");
				Toast.makeText(mFragment.getActivity(), "Load from server failed.\nTry again later.", Toast.LENGTH_LONG).show();
			}

			/**
			 * Called when the MPIs have been pulled from the server successfully
			 */
			@Override
			public void onIndicesLoaded(ArrayList<MediaPresentationIndex> mpis) {
				
				mMPIs = mpis;

				//Sets up our video list view with our custom array adapter.
				mMPIArrayAdapter = new MPIArrayAdapter(mFragment.getActivity(), mMPIs, null);
				mVideoListView.setAdapter(mMPIArrayAdapter);

				//Sets the click listener for the list view.
				mVideoListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						//Grabs the MPD for the selected MPI and starts the play activity.
						new MPDParser(mFragment.getActivity(), mMPIs.get(position).getUrl(), new MPDLoadedListener() {

							/**
							 * Called when the download from the server fails.
							 */
							@Override
							public void onFailedDownload() {
								Log.d(Utils.LOG_TAG, "MPI download failed.");
								Toast.makeText(mFragment.getActivity(), "Load from server failed.\nTry again later.", Toast.LENGTH_LONG).show();
							}

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

			}
		});


		return rootView;
	}
}
