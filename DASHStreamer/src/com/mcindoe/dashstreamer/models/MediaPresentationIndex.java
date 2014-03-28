package com.mcindoe.dashstreamer.models;

import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class MediaPresentationIndex {

	private String name, url, photoUrl;
	private int duration;
	
	public MediaPresentationIndex(String name, String url, String photoUrl, int duration) {
		setName(name);
		setUrl(url);
		setPhotoUrl(photoUrl);
		setDuration(duration);
	}
	
	public void printInfoToLog() {
		Log.d(Utils.LOG_TAG, "MPI");
		Log.d(Utils.LOG_TAG, "Name: " + name);
		Log.d(Utils.LOG_TAG, "Url: " + url);
		Log.d(Utils.LOG_TAG, "Photo Url: " + photoUrl);
		Log.d(Utils.LOG_TAG, "Duration: " + duration);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
