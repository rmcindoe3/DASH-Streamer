package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

public class MediaPresentation {
	
	private String name, baseUrl;
	private int duration;
	private ArrayList<Period> periods;
	
	public MediaPresentation(String name, String baseUrl, int duration) {
		setName(name);
		setBaseUrl(baseUrl);
		setDuration(duration);
		setPeriods(new ArrayList<Period>());
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
}
