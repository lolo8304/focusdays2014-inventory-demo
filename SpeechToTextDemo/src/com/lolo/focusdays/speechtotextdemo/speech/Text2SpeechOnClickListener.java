package com.lolo.focusdays.speechtotextdemo.speech;

import com.lolo.focusdays.speechtotextdemo.MainActivity;

import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

public class Text2SpeechOnClickListener implements
		View.OnClickListener {
	/**
	 * 
	 */
	private final MainActivity Text2SpeechOnClickListener;
	private TextView view;
	private TextToSpeech tts;

	public Text2SpeechOnClickListener(MainActivity mainActivity, TextToSpeech tts, TextView view) {
		Text2SpeechOnClickListener = mainActivity;
		this.tts = tts;
		this.view = view;

	}

	@Override
	public void onClick(View v) {
		tts.speak(view.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
	}
}