package com.mcindoe.dashstreamer.controllers;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mcindoe.dashstreamer.models.MediaPresentationIndex;

public class MPIParser extends XMLParser {
	
	private MPILoadedListener mMPILoadedListener;

	private final static String ROOT = "root";
	private final static String BASE_URL = "baseUrl";
	
	private final static String MPI = "MPI";
	private final static String NAME = "name";
	private final static String DURATION = "duration";
	private final static String URL = "url";
	private final static String PHOTO_URL = "photo";

	public interface MPILoadedListener {
		public void onIndicesLoaded(ArrayList<MediaPresentationIndex> mpis);
	}
	
	public MPIParser(String url, MPILoadedListener mpiLL) {
		this.mMPILoadedListener = mpiLL;
		(new DownloadXMLTask()).execute(url);
	}

	@Override
	protected void onSuccessfulDownload() {
		mMPILoadedListener.onIndicesLoaded(parseIndices());
	}
	
	/**
	 * Parses the media presentations out of the given URL
	 * @param url - the URL that contains MPDs
	 * @return - an ArrayList of media presentations contained in the given URL
	 */
	public ArrayList<MediaPresentationIndex> parseIndices() {
		
		ArrayList<MediaPresentationIndex> mpis = new ArrayList<MediaPresentationIndex>();

		//Get the root contained in this document.
		NodeList roots = doc.getElementsByTagName(ROOT);
		
		//Gets the base URL from the root node.
		String baseUrl = getAttr(BASE_URL, roots.item(0));
		
		//Then grab the list of MPIs described by this document
		NodeList mpiNodes = ((Element)roots.item(0)).getElementsByTagName(MPI);
		
		//For each MPD, grab its information from the XML and add it to our return list.
		for(int i = 0; i < mpiNodes.getLength(); i++) {

			MediaPresentationIndex index = new MediaPresentationIndex(
					getAttr(NAME, mpiNodes.item(i)),
					baseUrl + getAttr(URL, mpiNodes.item(i)),
					getAttr(PHOTO_URL, mpiNodes.item(i)),
					Integer.parseInt(getAttr(DURATION, mpiNodes.item(i))));

			mpis.add(index);
		}
		
		return mpis;
	}

}
