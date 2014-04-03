package com.mcindoe.dashstreamer.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mcindoe.dashstreamer.models.AdaptationSet;
import com.mcindoe.dashstreamer.models.ClipQueue;
import com.mcindoe.dashstreamer.models.MediaPresentation;
import com.mcindoe.dashstreamer.models.Period;
import com.mcindoe.dashstreamer.models.Representation;
import com.mcindoe.dashstreamer.models.VideoClip;

public class DASHManager {
	
	private static final String DOWNLOAD_FOLDER = Environment.getExternalStorageDirectory() + "/DASHStreamer/";
	
	private int mCurrSegmentNum;
	private int mCurrRepresentationNum;

	private Queue<Long> mThroughputHistory;

	private MediaPresentation mMediaPresentation;
	private Period mPeriod;
	private AdaptationSet mAdaptationSet;
	private Representation mRepresentation;
	
	private ArrayList<Integer> mAvailableBitrates;
	
	private DownloadClipTask mDownloadClipTask;
	
	private ClipQueue mClipQueue;
	
	public DASHManager(MediaPresentation mediaPresentation, ClipQueue clipQueue) {

		this.mMediaPresentation = mediaPresentation;
		this.mClipQueue = clipQueue;

		//Make our download folder if it doesn't already exist.
		File videoClipFolder = new File(DOWNLOAD_FOLDER);
		if(!videoClipFolder.exists()) {
			videoClipFolder.mkdirs();
		}

		//Start at the beginning of the video.
		mCurrSegmentNum = 0;
		
		//Default to the lowest bitrate possible
		mCurrRepresentationNum = 0;

		mThroughputHistory = new LinkedList<Long>();

		//Start with the first period.
		mPeriod = mMediaPresentation.getPeriods().get(0);

		//Start with the default adaptation set.
		mAdaptationSet = mPeriod.getAdaptationSets().get(0);

		//Start out at the lowest bitrate.
		mRepresentation = mAdaptationSet.getRepresentations().get(mCurrRepresentationNum);
		
		calculateAvailableBitrates();
		
		requestCurrentSegment();
	}
	
	/**
	 * Called when the video was seeked to a different part of the video
	 * that was not currently queued.
	 * @param clipNum - the clip number that was seeked to.
	 */
	public void setCurrentClipNum(int clipNum) {
		
		//If we're not already trying to get the selected clip number
		if(clipNum != mCurrSegmentNum) {

			//Cancel anything currently downloading and clear
			// what we have in the queue already.
			cancelCurrentDownload();
			mClipQueue.clear();

			//Updates the current segment number and then requests it.
			mCurrSegmentNum = clipNum;
			requestCurrentSegment();
		}
	}
	
	/**
	 * Tells the DASH manager that there is now space in the clip queue.
	 */
	public void clipCompleted() {
		
		//If we're not already downloading something, then request the current segment
		if(mDownloadClipTask.getStatus() != AsyncTask.Status.RUNNING) {
			requestCurrentSegment();
		}
	}
	
	/**
	 * Cancels the current clip download and deletes anythign that may have
	 * been saved to the sdcard.
	 */
	public void cancelCurrentDownload() {
		
		mDownloadClipTask.cancel(true);
		String tempFile = mDownloadClipTask.getFilepath();
		
		if(tempFile != null) {
			(new DeleteFileTask()).execute(tempFile);
		}
	}
	
	/**
	 * Requests the next segment from the server.
	 */
	public void requestCurrentSegment() {
		mDownloadClipTask = (DownloadClipTask) (new DownloadClipTask()).execute(getCurrentSegmentURL(), getCurrentSegmentFilename());
	}
	
	public String getCurrentSegmentURL() {
		return mMediaPresentation.getBaseUrl() + 
				mRepresentation.getFolder() + 
				mRepresentation.getSegments().get(mCurrSegmentNum).getUrl();
	}
	
	public String getCurrentSegmentFilename() {
		return mRepresentation.getSegments().get(mCurrSegmentNum).getUrl();
	}
	
	/**
	 * Choses the best quality video source based on the current
	 * estimated throughput of the system.
	 */
	public void updateVideoBitrate() {
		
		//grabs our current average throughput.
		long aveThroughput = getAverageThroughput();
		
		//Default to the lowest bitrate.
		mCurrRepresentationNum = 0;
		
		//For all available bitrates, find the highest bitrate where
		// the bitrate is less than or equal to 70% of our available throughput.
		for(int i = mAvailableBitrates.size() - 1; i >= 0; i--) {
			if(aveThroughput >= (mAvailableBitrates.get(i)*10/7)) {
				mCurrRepresentationNum = i;
				break;
			}
		}

		//Set the current representation based on this.
		mRepresentation = mAdaptationSet.getRepresentations().get(mCurrRepresentationNum);

	}
	
	/**
	 * Fills out the available bitrates for the current adaptation set.
	 */
	public void calculateAvailableBitrates() {
		
		//Fill out the available bitrates for the stream.
		mAvailableBitrates = new ArrayList<Integer>();
		for(int i = 0; i < mAdaptationSet.getRepresentations().size(); i++) {
			mAvailableBitrates.add(mAdaptationSet.getRepresentations().get(i).getBitrate());
		}
	}
	
	/**
	 * Calculates the average throughput of the previously received segments.
	 * @return - the average current throughput.
	 */
	public long getAverageThroughput() {

		long ret = 0;
		Iterator<Long> iter = mThroughputHistory.iterator();

		while(iter.hasNext()) {
			ret += iter.next();
		}

		return ret;
	}

	/**
	 * Asynchronous task that takes in a string URL and string filename and
	 * grabs the video clip from the given URL and saves it to the downloaded
	 * video clip folder as the given filename.
	 */
	private class DownloadClipTask extends AsyncTask<String, Integer, Long> {
		
		private String filepath;

		/**
		 * params[0] : Should be the URL of the video clip file.
		 * params[1] : Should be the name of the video clip file that we're saving.
		 * return : Estimated average throughput for the file transfer.
		 */
		@Override
		protected Long doInBackground(String... params) {
			
			Long ret = (long)-1;
			long startTime = 0, stopTime = -1;
			long fileSize = 0;

			OutputStream outputStream = null;

			try {

				//Create our HTTP client and post request.
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(params[0]);

				startTime = System.currentTimeMillis();

				//Execute the post request.
				HttpResponse httpResponse = httpClient.execute(httpPost);

				//Create an input stream for the http file response.
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = httpEntity.getContent();

				//Grabs the size of the file downloaded.
				fileSize = httpEntity.getContentLength();
				
				//Create our file output stream to the downloads folder with the given name.
				outputStream = new FileOutputStream(new File(DOWNLOAD_FOLDER + params[1]));
				
				filepath = DOWNLOAD_FOLDER + params[1];
				
				//Set up some buffers and variables for writing the file.
				int read = 0;
				byte[] bytes = new byte[1024];
				
				//Write the file from the http response to the file on the sdcard.
				while((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				
				stopTime = System.currentTimeMillis();

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				//If we successfully received the http response then
				// update the return to be the throughput during that time.
				if(stopTime != -1) {
					
					//This gets the throughput in Kbps
					ret = 8*fileSize / (stopTime-startTime);
				}
				
				//Make sure we close the file output stream if it was opened.
				if(outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return ret;
		}

		@Override
		protected void onPostExecute(Long result) {
			if(result != -1) {

				Log.d(Utils.LOG_TAG, "Downloaded " + getCurrentSegmentFilename() + " at " + result + " Kbps");

				mClipQueue.addClipToQueue(new VideoClip(DOWNLOAD_FOLDER + getCurrentSegmentFilename(), mCurrSegmentNum));

				//Adds the resulting throughput of the last video to our history.
				mThroughputHistory.add(result);
				
				//Clears our throughput history that is no longer relevant to
				// the current network state.
				while(mThroughputHistory.size() > 3) {
					mThroughputHistory.poll();
				}

				//Updates the selected video bitrate based on the new throughput.
				updateVideoBitrate();

				//if this wasn't the last segment in the video.
				if(++mCurrSegmentNum < mRepresentation.getSegments().size()) {

					//If our clip queue has room in it still.
					if(mClipQueue.hasRoom()) {
						requestCurrentSegment();
					}
				}

			}
			else {
				Log.d(Utils.LOG_TAG, "Download of media segment failed");
			}
		}
		
		public String getFilepath() {
			return filepath;
		}
		
	}

}
