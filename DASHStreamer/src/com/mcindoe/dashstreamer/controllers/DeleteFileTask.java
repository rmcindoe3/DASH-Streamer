package com.mcindoe.dashstreamer.controllers;

import java.io.File;

import android.os.AsyncTask;

public class DeleteFileTask extends AsyncTask<String, Integer, Void> {

	@Override
	protected Void doInBackground(String... params) {

		File clip = new File(params[0]);
		clip.delete();

		return null;
	}
}
