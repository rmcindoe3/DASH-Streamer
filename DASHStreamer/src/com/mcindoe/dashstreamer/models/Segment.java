package com.mcindoe.dashstreamer.models;

public class Segment {
	
	private int id;
	private String url;
	
	public Segment(String url, int id) {
		setUrl(url);
		setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
