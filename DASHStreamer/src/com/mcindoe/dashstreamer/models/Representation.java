package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

public class Representation {
	
	private int bitRate;
	private int videoHeight;
	private int videoWidth;
	private ArrayList<Segment> segments;
	
	public Representation(int bitRate, int videoHeight, int videoWidth) {
		
		setBitRate(bitRate);
		setVideoHeight(videoHeight);
		setVideoWidth(videoWidth);
		setSegments(new ArrayList<Segment>());
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

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

}
