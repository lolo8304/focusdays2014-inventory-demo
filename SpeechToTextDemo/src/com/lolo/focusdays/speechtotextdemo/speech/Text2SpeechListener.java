package com.lolo.focusdays.speechtotextdemo.speech;

import java.util.Locale;

import com.lolo.focusdays.speechtotextdemo.MainActivity;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

public class Text2SpeechListener implements
		TextToSpeech.OnInitListener {

	/**
	 * 
	 */
	private final MainActivity Text2SpeechListener;
	private Locale defaultLocale;
	private View ViewSpeak;
	private TextToSpeech tts;

	public Text2SpeechListener(MainActivity mainActivity, View ViewSpeak, Locale defaultLocale) {
		Text2SpeechListener = mainActivity;
		this.ViewSpeak = ViewSpeak;
		this.defaultLocale = defaultLocale;
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(defaultLocale);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				ViewSpeak.setEnabled(true);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	public void setTTS(TextToSpeech tts) {
		this.tts = tts;
	}

}