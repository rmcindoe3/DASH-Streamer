package com.mcindoe.dashstreamer.controllers;

import android.content.Context;

public class Utils {

	public static final String LOG_TAG = "DASH Streamer";
	
	/**
	 * Converts the given value to the density independent value of pixels for
	 * the given context's screen.
	 * @param val - the number of dp you want returned for this screen size.
	 * @param context - the context of our application.
	 * @return - number of pixels equivalent given dp.
	 */
	public static int getDensityIndependentPixels(int val, Context context) {
		return (int)(val * context.getResources().getDisplayMetrics().density + 0.5f);
	}
}
