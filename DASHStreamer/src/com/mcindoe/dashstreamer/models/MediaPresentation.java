package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

public class MediaPresentation {
	
	private String presentationName;
	private ArrayList<Period> periods;
	
	public MediaPresentation(String presentationName) {
		setPresentationName(presentationName);
		setPeriods(new ArrayList<Period>());
	}
	
	public void addPeriod(Period per) {
		periods.add(per);
	}

	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}

	public ArrayList<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(ArrayList<Period> periods) {
		this.periods = periods;
	}
}
