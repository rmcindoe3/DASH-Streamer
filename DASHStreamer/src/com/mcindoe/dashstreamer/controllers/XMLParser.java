package com.mcindoe.dashstreamer.controllers;

import java.io.IOException;
import java.io.StringReader;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

public abstract class XMLParser {

	protected Document doc;
	
	protected abstract void onSuccessfulDownload();
	
	/**
	 * Grabs the value of the given attribute from the given node
	 * @param attrName - Name of the attribute you want.
	 * @param node - the node you want the attribute from.
	 * @return - the value of the requested attribute of the given node.
	 */
	protected String getAttr(String attrName, Node node) {
		NamedNodeMap attrs = node.getAttributes();
		for(int i = 0; i < attrs.getLength(); i++) {
			Node attr = attrs.item(i);
			if(attr.getNodeName().equals(attrName)) {
				return attr.getNodeValue();
			}
		}
		return "";
	}
	
	protected class DownloadXMLTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String xml = null;

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				xml = EntityUtils.toString(httpEntity);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return xml;
		}

		@Override
		protected void onPostExecute(String result) {

			//Grabs the MPD from the URL and puts it into a Document.
			doc = getDomElement(result);
			onSuccessfulDownload();
		}
		
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
}
