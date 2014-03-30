package com.mcindoe.dashstreamer.controllers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.mcindoe.dashstreamer.models.MediaPresentationIndex;

public class MPIParser extends XMLParser {
	
	private MPILoadedListener mMPILoadedListener;

	private final static String ROOT = "root";
	private final static String BASE_URL = "baseUrl";
	
	private final static String MPI = "MPI";
	private final static String NAME = "name";
	private final static String SEASON = "season";
	private final static String EPISODE = "episode";
	private final static String DURATION = "duration";
	private final static String URL = "url";
	private final static String PHOTO_URL = "photo";
	
	private int downloadedPhotoCount;

	public interface MPILoadedListener {
		public void onIndicesLoaded(ArrayList<MediaPresentationIndex> mpis);
		public void onFailedDownload();
	}
	
	public MPIParser(String url, MPILoadedListener mpiLL) {
		this.downloadedPhotoCount = 0;
		this.mMPILoadedListener = mpiLL;
		(new DownloadXMLTask()).execute(url);
	}

	@Override
	protected void onSuccessfulDownload() {
		mMPILoadedListener.onIndicesLoaded(parseIndices());
	}

	@Override
	protected void onFailedDownload() {
		mMPILoadedListener.onFailedDownload();
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
					Integer.parseInt(getAttr(SEASON, mpiNodes.item(i))),
					Integer.parseInt(getAttr(EPISODE, mpiNodes.item(i))),
					baseUrl + getAttr(URL, mpiNodes.item(i)),
					getAttr(PHOTO_URL, mpiNodes.item(i)),
					Integer.parseInt(getAttr(DURATION, mpiNodes.item(i))));
			
			(new DownloadPhotoTask()).execute(index);

			mpis.add(index);
		}
		
		//Wait for all the MPIs to get their photos downloaded.
		while(downloadedPhotoCount < mpis.size());
		
		return mpis;
	}

	/**
	 * AsyncTask that grabs the photo for the given MPI and saves it.
	 */
	private class DownloadPhotoTask extends AsyncTask<MediaPresentationIndex, Integer, Void> {

		@Override
		protected Void doInBackground(MediaPresentationIndex... params) {
			
			MediaPresentationIndex mpi = params[0];

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(mpi.getPhotoUrl());

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				
				mpi.setPhoto(BitmapFactory.decodeStream(httpEntity.getContent()));
				downloadedPhotoCount++;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

}
