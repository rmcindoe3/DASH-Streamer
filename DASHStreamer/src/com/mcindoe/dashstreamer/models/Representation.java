package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

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
