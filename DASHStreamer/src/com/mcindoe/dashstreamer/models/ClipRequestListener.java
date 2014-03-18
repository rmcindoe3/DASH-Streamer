package com.mcindoe.dashstreamer.models;

public interface ClipRequestListener {
	
	public abstract void requestClip(ClipQueue queue, int clipNum);

}
