package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

public class Period {
	
	private int startTime;
	private int duration;
	private ArrayList<AdaptationSet> adaptationSets;
	
	public Period(int startTime, int duration) {
		
		setStartTime(startTime);
		setDuration(duration);
		setAdaptationSets(new ArrayList<AdaptationSet>());
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ArrayList<AdaptationSet> getAdaptationSets() {
		return adaptationSets;
	}

	public void setAdaptationSets(ArrayList<AdaptationSet> adaptationSets) {
		this.adaptationSets = adaptationSets;
	}

}
