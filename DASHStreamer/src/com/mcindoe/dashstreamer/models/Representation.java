package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class Representation {
	
	private String name, folder;
	private int bitrate, segmentLength;

	private ArrayList<Segment> segments;
	
	public Representation(String name, String folder, int bitrate, int segmentLength) {
		setName(name);
		setFolder(folder);
		setBitrate(bitrate);
		setSegmentLength(segmentLength);
		setSegments(new ArrayList<Segment>());
	}
	
	public void printInfoToLog() {

		Log.d(Utils.LOG_TAG, "Representation");
		Log.d(Utils.LOG_TAG, "Name: " + name);
		Log.d(Utils.LOG_TAG, "Folder: " + folder);
		Log.d(Utils.LOG_TAG, "Bitrate: " + bitrate);
		Log.d(Utils.LOG_TAG, "Segment Length: " + segmentLength);
		
		for(int i = 0; i < segments.size(); i++) {
			segments.get(i).printInfoToLog();
		}
	}
	
	public void addSegment(Segment seg) {
		segments.add(seg);
	}
	
	public ArrayList<Segment> getSegments() {
		return segments;
	}
	
	public void setSegments(ArrayList<Segment> segs) {
		this.segments = segs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}
}
