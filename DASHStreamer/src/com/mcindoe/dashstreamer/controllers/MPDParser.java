package com.mcindoe.dashstreamer.controllers;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.mcindoe.dashstreamer.R;
import com.mcindoe.dashstreamer.models.AdaptationSet;
import com.mcindoe.dashstreamer.models.MediaPresentation;
import com.mcindoe.dashstreamer.models.Period;
import com.mcindoe.dashstreamer.models.Representation;
import com.mcindoe.dashstreamer.models.Segment;

public class MPDParser extends XMLParser {
	
	private MPDLoadedListener mMPDLoadedListener;
	private Context mContext;
	
	/*** All of the MPD XML tags and attributes ***/
	private static final String MPD = "MPD";
	private static final String NAME = "name";
	private static final String DURATION = "duration";
	private static final String BASE_URL = "baseUrl";

	private static final String PERIOD = "Period";
	private static final String START = "start";

	private static final String ADAPTATION_SET = "AdaptationSet";
	private static final String BITRATE = "bitrate";
	private static final String SEGMENT_LENGTH = "segmentLength";
	private static final String FOLDER = "folder";

	private static final String REPRESENTATION = "Representation";

	private static final String SEGMENT = "Segment";
	private static final String ID = "id";
	private static final String URL = "url";

	public interface MPDLoadedListener {
		public void onMediaPresentationsLoaded(ArrayList<MediaPresentation> mpds);
		public void onFailedDownload();
	}
	
	public MPDParser(Context context, String url, MPDLoadedListener mpdLL) {
		this.mContext = context;
		this.mMPDLoadedListener = mpdLL;
		(new DownloadXMLTask()).execute(url);
	}

	/**
	 * Called by our DownloadXMLTask when the document is successfully downloaded.
	 */
	@Override
	protected void onSuccessfulDownload() {
		mMPDLoadedListener.onMediaPresentationsLoaded(parseMediaPresentation());
	}

	@Override
	protected void onFailedDownload() {
		mMPDLoadedListener.onFailedDownload();
	}
	
	/**
	 * Parses the media presentations out of the given URL
	 * @param url - the URL that contains MPDs
	 * @return - an ArrayList of media presentations contained in the given URL
	 */
	public ArrayList<MediaPresentation> parseMediaPresentation() {
		
		ArrayList<MediaPresentation> mpds = new ArrayList<MediaPresentation>();
		
		String ip_addr = mContext.getResources().getString(R.string.ip_addr);

		//Get the list of mpds contained in this document.
		NodeList mpdList = doc.getElementsByTagName(MPD);
		
		//For each MPD, grab its information from the XML and add it to our return list.
		for(int i = 0; i < mpdList.getLength(); i++) {
			
			MediaPresentation mediaPresentation = new MediaPresentation(
					getAttr(NAME, mpdList.item(i)),
					ip_addr + getAttr(BASE_URL, mpdList.item(i)),
					Integer.parseInt(getAttr(SEGMENT_LENGTH, mpdList.item(i))),
					Integer.parseInt(getAttr(DURATION, mpdList.item(i))));
			
			mediaPresentation.setPeriods(parsePeriods(mpdList.item(i)));

			mpds.add(mediaPresentation);
		}
		
		return mpds;
	}
	
	/**
	 * Grabs all the periods associated with the given MPD.
	 * @param mpdNode - the DOM node for the MPD.
	 * @return - List of periods for this MPD.
	 */
	private ArrayList<Period> parsePeriods(Node mpdNode) {
		
		ArrayList<Period> periods = new ArrayList<Period>();
		
		//Create a list of child nodes for this MPD.
		NodeList periodList = ((Element)mpdNode).getElementsByTagName(PERIOD);

		//Loop through the child nodes adding each period to the return list.
		for(int i = 0; i < periodList.getLength(); i++) {
			
			Period period = new Period(Integer.parseInt(getAttr(START, periodList.item(i))));
			
			period.setAdaptationSets(parseAdaptationSets(periodList.item(i)));
			
			periods.add(period);
		}
		
		return periods;
	}
	
	/**
	 * Grabs all the adaptation sets associated with the given period.
	 * @param periodNode - the DOM node for the period.
	 * @return - list of adaptation sets for this period.
	 */
	private ArrayList<AdaptationSet> parseAdaptationSets(Node periodNode) {
		
		ArrayList<AdaptationSet> adaptationSets = new ArrayList<AdaptationSet>();
		
		//Create a list of child nodes for this period.
		NodeList adaptationSetList = ((Element)periodNode).getElementsByTagName(ADAPTATION_SET);
		
		//Loop thorugh the child nodes adding each adaptation set to the return list.
		for(int i = 0; i < adaptationSetList.getLength(); i++) {
			
			AdaptationSet adaptationSet = new AdaptationSet(getAttr(NAME, adaptationSetList.item(i)));
			
			adaptationSet.setRepresentations(parseRepresentations(adaptationSetList.item(i)));
			
			adaptationSets.add(adaptationSet);
		}
		
		return adaptationSets;
	}
	
	/**
	 * Grabs all the representations associated with the given adaptation set
	 * @param adaptationSetNode- the DOM node for the adaptation set.
	 * @return - list of representations belonging to this adaptation set.
	 */
	private ArrayList<Representation> parseRepresentations(Node adaptationSetNode) {
		
		ArrayList<Representation> representations = new ArrayList<Representation>();
		
		//Creat a list of child nodes for this adaptation set.
		NodeList representationList = ((Element)adaptationSetNode).getElementsByTagName(REPRESENTATION);
		
		//Loop through the child nodes adding each representation to the return list.
		for(int i = 0; i < representationList.getLength(); i++) {
			
			Representation representation = new Representation(
					getAttr(NAME, representationList.item(i)),
					getAttr(FOLDER, representationList.item(i)),
					Integer.parseInt(getAttr(BITRATE, representationList.item(i))));
			
			representation.setSegments(parseSegments(representationList.item(i)));
			
			representations.add(representation);
		}
		
		return representations;
	}
	
	/**
	 * Grabs all the segments associated with the given representation.
	 * @param representationNode - the DOM node for the representation.
	 * @return - list of segments belonging to this representation.
	 */
	private ArrayList<Segment> parseSegments(Node representationNode) {
		
		ArrayList<Segment> segments = new ArrayList<Segment>();
		
		//Create a list of child nodes for this representation
		NodeList segmentList = ((Element)representationNode).getElementsByTagName(SEGMENT);
		
		//Loop through the child nodes adding each segment to the return list.
		for(int i = 0; i < segmentList.getLength(); i++) {
			
			Segment segment = new Segment(
					getAttr(URL, segmentList.item(i)),
					Integer.parseInt(getAttr(ID, segmentList.item(i))));
			
			segments.add(segment);
		}

		return segments;
	}

}
