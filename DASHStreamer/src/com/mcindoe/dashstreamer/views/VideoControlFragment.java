package com.mcindoe.dashstreamer.views;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.models.VideoControlListener;

public class VideoControlFragment extends Fragment {
	
	private ListView mAdaptationSetListView;
	private List<String> mAdaptationSetTitles;
	private VideoControlListener mVideoControlListener;
	private Button mFullscreenButton;
	private int mCurrAdaptationSetNum;
	
	public VideoControlFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Retain the instance of this video fragment when a config change happens
		setRetainInstance(true);

		mCurrAdaptationSetNum = 0;

		//Inflate our view from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_video_control, container, false);

		mFullscreenButton = (Button)rootView.findViewById(R.id.fullscreen_button);
		mAdaptationSetListView = (ListView)rootView.findViewById(R.id.adaptation_set_list_view);
		
		mFullscreenButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mVideoControlListener != null) {			
					mVideoControlListener.switchToFullscreen();
				}
			}
		});
		
		mAdaptationSetListView.setAdapter(new AdaptationSetArrayAdapter(getActivity(), mAdaptationSetTitles, null));
		mAdaptationSetListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				mCurrAdaptationSetNum = position;
				highlightSelectedAdaptationSet(position);
				mVideoControlListener.setCurrentAdaptationSet(position);
			}
			
		});

		return rootView;
	}
	
	/**
	 * Highlights the video stream option that the user has selected
	 * @param pos - the position in the list view that was selected
	 */
	public void highlightSelectedAdaptationSet(int pos) {
		
		for(int i = 0; i < mAdaptationSetListView.getChildCount(); i++) {
			
			int color;

			if(i == pos) {
				color = R.color.black;
			}
			else {
				color = R.color.gray;
			}

			((TextView)mAdaptationSetListView.getChildAt(i).findViewById(R.id.adaptation_set_name)).setTextColor(getResources().getColor(color));
		}
	}
	
	/**
	 * Standard setter for our list of adaptation sets.
	 * @param titles - list of possible adaptation sets
	 */
	public void setAdaptationSetTitles(List<String> titles) {
		this.mAdaptationSetTitles = titles;
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
	
	private class AdaptationSetArrayAdapter extends ArrayAdapter<String> {
		
		private Context mContext;
		private List<String> mTitles;

		public AdaptationSetArrayAdapter(Context context, List<String> adpNames, View.OnClickListener listener) {
			super(context, R.layout.list_item_adaptation_set, adpNames);
			this.mTitles = adpNames;
			this.mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			//Grab the MPI for this view for quicker calls later.
			String title = mTitles.get(position);

			//Grab our layout inflater
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			//Initialize our return.
			View adaptationSetRow;

			//Inflate the proper layout
			adaptationSetRow = inflater.inflate(R.layout.list_item_adaptation_set, parent, false);
			
			//Set the title
			((TextView)adaptationSetRow.findViewById(R.id.adaptation_set_name)).setText(title);
			
			//Highlight this text to black if it is the currently selected adaptation set.
			if(position == mCurrAdaptationSetNum) {
				((TextView)adaptationSetRow.findViewById(R.id.adaptation_set_name)).setTextColor(getResources().getColor(R.color.black));
			}

			return adaptationSetRow;
		}
	}
}
