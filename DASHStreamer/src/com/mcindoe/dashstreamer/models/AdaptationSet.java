package com.mcindoe.dashstreamer.models;

import java.util.ArrayList;

public class AdaptationSet {

	private String contentDescriptor;
	private ArrayList<Representation> representations;
	
	public AdaptationSet(String contentDescriptor) {
		setContentDescriptor(contentDescriptor);
		setRepresentations(new ArrayList<Representation>());
	}
	
	public void addRepresentation(Representation rep) {
		representations.add(rep);
	}

	public String getContentDescriptor() {
		return contentDescriptor;
	}

	public void setContentDescriptor(String contentDescriptor) {
		this.contentDescriptor = contentDescriptor;
	}

	public ArrayList<Representation> getRepresentations() {
		return representations;
	}

	public void setRepresentations(ArrayList<Representation> representations) {
		this.representations = representations;
	}
}
