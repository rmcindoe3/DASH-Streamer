package com.mcindoe.dashstreamer.models;

public class VideoClip {
	
	private String filePath;
	private int clipNum;

	public VideoClip(String filePath, int clipNum) {
		setFilePath(filePath);
		setClipNum(clipNum);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getClipNum() {
		return clipNum;
	}

	public void setClipNum(int clipNum) {
		this.clipNum = clipNum;
	}

}
