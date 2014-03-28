package com.mcindoe.dashstreamer.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class MediaPresentationIndex {

	private String name, url, photoUrl;
	private int duration, season, episode;
	private Bitmap photo;
	
	public MediaPresentationIndex(String name, int season, int episode, String url, String photoUrl, int duration) {
		setName(name);
		setSeason(season);
		setEpisode(episode);
		setUrl(url);
		setPhotoUrl(photoUrl);
		setDuration(duration);
	}
	
	public void printInfoToLog() {
		Log.d(Utils.LOG_TAG, "MPI");
		Log.d(Utils.LOG_TAG, "Name: " + name);
		Log.d(Utils.LOG_TAG, "Season: " + season);
		Log.d(Utils.LOG_TAG, "Episode: " + episode);
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

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}
}
