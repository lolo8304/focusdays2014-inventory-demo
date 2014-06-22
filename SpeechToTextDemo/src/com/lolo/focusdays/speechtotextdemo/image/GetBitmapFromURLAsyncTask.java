package com.lolo.focusdays.speechtotextdemo.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class GetBitmapFromURLAsyncTask extends AsyncTask<String, Void, Bitmap> {
	@Override
	protected Bitmap doInBackground(String... fileOrURL) {
		return new ImageHelper(fileOrURL[0]).getImage();
	}
}