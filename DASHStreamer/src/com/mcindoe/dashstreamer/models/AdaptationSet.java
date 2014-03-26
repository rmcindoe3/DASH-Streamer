package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

import android.util.Log;

import com.mcindoe.dashstreamer.controllers.Utils;

public class AdaptationSet {

	private String name;
	private ArrayList<Representation> representations;
	
	public AdaptationSet(String name) {
		setName(name);
		setRepresentations(new ArrayList<Representation>());
	}
	
	public void printInfoToLog() {

		Log.d(Utils.LOG_TAG, "Adaptation Set");
		Log.d(Utils.LOG_TAG, "Name: " + name);
		
		for(int i = 0; i < representations.size(); i++) {
			representations.get(i).printInfoToLog();
		}
	}
	
	public void addRepresentation(Representation rep) {
		representations.add(rep);
	}

	public ArrayList<Representation> getRepresentations() {
		return representations;
	}

	public void setRepresentations(ArrayList<Representation> representations) {
		this.representations = representations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
