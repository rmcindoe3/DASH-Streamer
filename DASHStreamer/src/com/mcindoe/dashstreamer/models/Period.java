package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class Period {
	
	private int startTime;
	private ArrayList<AdaptationSet> adaptationSets;
	
	public Period(int startTime) {
		
		setStartTime(startTime);
		setAdaptationSets(new ArrayList<AdaptationSet>());
	}
	
	public void printInfoToLog() {

		Log.d(Utils.LOG_TAG, "Period");
		Log.d(Utils.LOG_TAG, "Start: " + startTime);
		
		for(int i = 0; i < adaptationSets.size(); i++) {
			adaptationSets.get(i).printInfoToLog();
		}
	}
	
	public void addAdaptationSet(AdaptationSet adpSet) {
		adaptationSets.add(adpSet);
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public ArrayList<AdaptationSet> getAdaptationSets() {
		return adaptationSets;
	}

	public void setAdaptationSets(ArrayList<AdaptationSet> adaptationSets) {
		this.adaptationSets = adaptationSets;
	}

}
