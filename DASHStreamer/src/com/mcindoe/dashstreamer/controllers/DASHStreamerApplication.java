package com.mcindoe.dashstreamer.controllers;

import android.app.Application;

import com.mcindoe.dashstreamer.models.MediaPresentation;

public class DASHStreamerApplication extends Application {
	
	private MediaPresentation currentMediaPresentation;

	public MediaPresentation getCurrentMediaPresentation() {
		return currentMediaPresentation;
	}

	public void setCurrentMediaPresentation(MediaPresentation currentMediaPresentation) {
		this.currentMediaPresentation = currentMediaPresentation;
	}
}
