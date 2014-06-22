package com.lolo.focusdays.speechtotextdemo.ean;

import android.os.AsyncTask;

import com.lolo.focusdays.speechtotextdemo.AsyncResponse;

public class EANSearchAnalyzeAsyncTask extends AsyncTask<String, Void, String> {
	
	private AsyncResponse<String> response;
	public EANSearchAnalyzeAsyncTask(AsyncResponse<String> response) {
		this.response = response;
	}
	@Override
	protected String doInBackground(String... string) {
		return new EANSerchHelper(string[0]).getTitle();
	}
	@Override
	protected void onPostExecute(String result) {
		response.processFinish(result);
	}
}