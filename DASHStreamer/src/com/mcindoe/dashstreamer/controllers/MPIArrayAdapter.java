package com.mcindoe.dashstreamer.controllers;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.models.MediaPresentationIndex;

public class MPIArrayAdapter extends ArrayAdapter<MediaPresentationIndex> {
	
	private Context mContext;
	private List<MediaPresentationIndex> mMPIs;

    public MPIArrayAdapter(Context context, List<MediaPresentationIndex> mpis, View.OnClickListener listener) {

        super(context, R.layout.list_item_video, mpis);

        this.mContext = context;
        this.mMPIs = mpis;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

    	//Grab the MPI for this view for quicker calls later.
        MediaPresentationIndex mpi = mMPIs.get(position);

        //Grab our layout inflater
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Initialize our return.
        View videoRow;

        //Inflate the proper layout
        videoRow = inflater.inflate(R.layout.list_item_video, parent, false);

        //Fill in the view with information from the MPI.
        ((TextView)videoRow.findViewById(R.id.video_title_text_view)).setText(mpi.getName());
        ((TextView)videoRow.findViewById(R.id.video_duration_text_view)).setText(parseDuration(mpi.getDuration()));
        ((ImageView)videoRow.findViewById(R.id.video_list_item_picture)).setImageBitmap(mpi.getPhoto());

        //If the video has a season/episode, then display those values on the screen.
        if(mpi.getSeason() != -1) {
        	((TextView)videoRow.findViewById(R.id.video_season_text_view)).setText("Season " + mpi.getSeason());
        }

        if(mpi.getEpisode() != -1) {
        	((TextView)videoRow.findViewById(R.id.video_episode_text_view)).setText("Episode " + mpi.getEpisode());
        }

        return videoRow;
    }
    
    /**
     * Formats the duration of the video into HH:MM:SS
     * @param duration - duration of the video in milliseconds
     * @return - HH:MM:SS representation of "duration" milliseconds
     */
    private String parseDuration(int duration) {
    	
    	String ret = "";
    	
    	duration /= 1000;
    	ret += ":" + String.format("%02d", duration%60);
    	
    	duration /= 60;
    	ret = String.format("%02d", duration%60) + ret;
    	
    	duration /= 60;
    	if(duration != 0) {
    		ret = duration + ":" + ret;
    	}
    	
    	return ret;
    }
}
