package com.mcindoe.dashstreamer.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.mcindoe.dashstreamer.models.AdaptationSet;
import com.mcindoe.dashstreamer.models.MediaPresentation;
import com.mcindoe.dashstreamer.models.Period;
import com.mcindoe.dashstreamer.models.Representation;
import com.mcindoe.dashstreamer.models.Segment;

public class MPDParser {
	
	private Document mpdDoc;
	private MPDLoadedListener mMPDLoadedListener;
	
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
	}
	
	public MPDParser(String url, MPDLoadedListener mpdLL) {
		this.mMPDLoadedListener = mpdLL;
		(new DownloadMPDTask()).execute(url);
	}
	
	/**
	 * Parses the media presentations out of the given URL
	 * @param url - the URL that contains MPDs
	 * @return - an ArrayList of media presentations contained in the given URL
	 */
	public ArrayList<MediaPresentation> parseMediaPresentation() {
		
		ArrayList<MediaPresentation> mpds = new ArrayList<MediaPresentation>();

		//Get the list of mpds contained in this document.
		NodeList mpdList = mpdDoc.getElementsByTagName(MPD);
		
		//For each MPD, grab its information from the XML and add it to our return list.
		for(int i = 0; i < mpdList.getLength(); i++) {
			
			MediaPresentation mediaPresentation = new MediaPresentation(
					getAttr(NAME, mpdList.item(i)),
					getAttr(BASE_URL, mpdList.item(i)),
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
					Integer.parseInt(getAttr(BITRATE, representationList.item(i))),
					Integer.parseInt(getAttr(SEGMENT_LENGTH, representationList.item(i))));
			
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
	
	/**
	 * Grabs the value of the given attribute from the given node
	 * @param attrName - Name of the attribute you want.
	 * @param node - the node you want the attribute from.
	 * @return - the value of the requested attribute of the given node.
	 */
	private String getAttr(String attrName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		for(int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			if(attr.getNodeName().equals(attrName)) {
				return attr.getNodeValue();
			}
		}
		return "";
	}
	
	/**
	 * Gets the DOM element of the given MPD file.
	 * @param mpd - the MPD file we want to get the DOM of.
	 * @return - the DOM of the given MPD file.
	 */
	private Document getDomElement(String mpd) {

		Document doc = null;
		
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(mpd));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	private class DownloadMPDTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String mpd = null;

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				mpd = EntityUtils.toString(httpEntity);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return mpd;
		}

		@Override
		protected void onPostExecute(String result) {

			//Grabs the MPD from the URL and puts it into a Document.
			mpdDoc = getDomElement(result);
			mMPDLoadedListener.onMediaPresentationsLoaded(parseMediaPresentation());
		}
		
	}

}
