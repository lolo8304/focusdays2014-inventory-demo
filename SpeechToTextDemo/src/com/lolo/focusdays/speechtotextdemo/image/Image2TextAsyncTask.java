package com.lolo.focusdays.speechtotextdemo.image;

import java.io.File;

import com.lolo.focusdays.speechtotextdemo.AsyncResponse;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Image2TextAsyncTask extends AsyncTask<Image2TextPresentationModel, Void, Image2TextPresentationModel[]> {
	
	private boolean deleteAfterDetection;
	private Context context;
	private AsyncResponse<Image2TextPresentationModel> response;

	public Image2TextAsyncTask(Context context, AsyncResponse<Image2TextPresentationModel> response, boolean deleteAfterDetection) {
		super();
		this.context = context;
		this.response = response;
		this.deleteAfterDetection = deleteAfterDetection;
	}

	@Override
	protected Image2TextPresentationModel[] doInBackground(Image2TextPresentationModel... models) {
		for (Image2TextPresentationModel model : models) {
			try {
				ImageHelper image = model.createImage();
				ImageHelper smallImage = model.createSmallImage(image);
				model.setKeywords(smallImage.detectKeywords());
//				Toast.makeText(this.context, "tagged picture <" + model.getKeywords() +"> ("+model.getImageSmallUri()+")", Toast.LENGTH_SHORT).show();
				if (this.deleteAfterDetection) {
					new File(model.getImageSmallFullName()).delete();
				}
			} catch (Exception e) {
//				Toast.makeText(this.context, "error tagging picture: "+e.getMessage(), Toast.LENGTH_LONG).show();
				model.setKeywords("none");
				if (this.deleteAfterDetection) {
					new File(model.getImageSmallFullName()).delete();
				}
			}
		}
		return models;
	}

	@Override
	protected void onPostExecute(Image2TextPresentationModel[] models) {
		for (Image2TextPresentationModel model : models) {
			Toast.makeText(this.context, "tagged picture <" + model.getKeywords() +"> ("+model.getImageSmallUri()+")", Toast.LENGTH_SHORT).show();
			response.processFinish(model);
		}
	}
	
}