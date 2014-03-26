package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class MediaPresentation {
	
	private String name, baseUrl;
	private int duration, segmentLength;
	private ArrayList<Period> periods;
	
	public MediaPresentation(String name, String baseUrl, int segmentLength, int duration) {
		setName(name);
		setBaseUrl(baseUrl);
		setDuration(duration);
		setSegmentLength(segmentLength);
		setPeriods(new ArrayList<Period>());
	}
	
	/**
	 * Returns the number of clips that will be played in this presentation.
	 * @return - the number of clips in the presentation.
	 */
	public int getNumberOfClips() {
		int ret = 0;
		for(int i = 0; i < periods.size(); i++) {
			
			//Each subnode of a period will have the same number of segments in 
			// it so we only have to add one segment list size per period.
			ret += periods.get(i)
					.getAdaptationSets().get(0)
					.getRepresentations().get(0)
					.getSegments().size();
		}
		return ret;
	}
	
	public void printInfoToLog() {

		Log.d(Utils.LOG_TAG, "MPD");
		Log.d(Utils.LOG_TAG, "Name: " + name);
		Log.d(Utils.LOG_TAG, "Base URL: " + baseUrl);
		Log.d(Utils.LOG_TAG, "Segment Length: " + segmentLength);
		Log.d(Utils.LOG_TAG, "Duration: " + duration);
		
		for(int i = 0; i < periods.size(); i++) {
			periods.get(i).printInfoToLog();
		}
	}
	
	public void addPeriod(Period per) {
		periods.add(per);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(ArrayList<Period> periods) {
		this.periods = periods;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}
}
