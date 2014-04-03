package com.mcindoe.dashstreamer.models;

public interface ClipRequestListener {
	
	public abstract void requestClip(int clipNum);
	public abstract void clipCompleted();

}
