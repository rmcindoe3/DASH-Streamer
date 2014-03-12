package com.mcindoe.dashstreamer.models;

public class Segment {
	
	private int length;
	private String videoURL;
	
	public Segment(String videoURL, int length) {
		
		setLength(length);
		setVideoURL(videoURL);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getVideoURL() {
		return videoURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

}
