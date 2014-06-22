package com.lolo.focusdays.speechtotextdemo.speech;

import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import com.lolo.focusdays.speechtotextdemo.MainActivity;

public class Speech2TextOnClickListener implements
		View.OnClickListener {

	/**
	 * 
	 */
	private final MainActivity mainActivity;
	private int requestCode;
	private Locale locale;

	public Speech2TextOnClickListener(MainActivity mainActivity, Locale locale, int requestCode) {
		this.mainActivity = mainActivity;
		this.locale = locale;
		this.requestCode = requestCode;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				locale.toString());
		try {
			this.mainActivity.startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(this.mainActivity.getApplicationContext(),
					"Ops! Your device doesn't support Speech to Text",
					Toast.LENGTH_SHORT);
			t.show();
		}
	}
}