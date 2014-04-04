package com.mcindoe.dashstreamer.models;

public interface VideoControlListener {

	public abstract void switchToFullscreen();
	public abstract void videoLoaded();
	public abstract void setCurrentAdaptationSet(int id);

}
