package com.mcindoe.dashstreamer.models;

public interface ClipQueue {

	public abstract void addClipToQueue(VideoClip videoClip);
	public abstract boolean hasRoom();
}
