package com.lolo.focusdays.speechtotextdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.robobinding.binder.Binders;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventoryappbase.core.AsyncResponse;
import com.example.inventoryappbase.core.ean.EANPresentationModel;
import com.example.inventoryappbase.core.ean.EANSearchAnalyzeAsyncTask;
import com.example.inventoryappbase.core.ean.IntentIntegrator;
import com.example.inventoryappbase.core.ean.IntentResult;
import com.example.inventoryappbase.core.image.Image2TextAsyncTask;
import com.example.inventoryappbase.core.image.Image2TextPresentationModel;
import com.example.inventoryappbase.core.location.SimpleAddress;
import com.example.inventoryappbase.core.profile.ListProfileAsyncTask;
import com.example.inventoryappbase.core.speech.Speech2TextOnClickListener;
import com.example.inventoryappbase.core.speech.Text2SpeechListener;
import com.example.inventoryappbase.core.speech.Text2SpeechOnClickListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;
import com.lolo.focusdays.speechtotextdemo.location.MyLocationDemoActivity;

/*
 * 
 * speech2text
 * 		http://viralpatel.net/blogs/android-speech-to-text-api/ 
 * 
 * check if online
 * 		http://viralpatel.net/blogs/android-internet-connection-status-network-change/
 * 
 * how to pick from photo-gallery
 * 		http://viralpatel.net/blogs/pick-image-from-galary-android-app/
 * 
 * how to do properties
 * 		http://viralpatel.net/blogs/android-preferences-activity-example/
 * 
 * how to trigger media scanner api
 * 		http://viralpatel.net/blogs/android-trigger-media-scanner-api/
 * 
 * text2speech
 * 		http://www.androidhive.info/2012/01/android-text-to-speech-tutorial 
 * 
 * image search google
 * 		by URL: http://images.google.com/searchbyimage?image_url=http%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fa%2Fa1%2FKonrad_Lorenz.JPG
 * 		upload to raspnberry pi and use raspi URL to search
 * 
 * barcode reader
 * 		https://github.com/zxing/zxing
 * 		set android:minSdkVersion="11" 
 * 
 * jasdb
 * 		http://www.oberasoftware.com/
 *		copy androidlib
 *		missing class nl.renarj.jasdb.core.exceptions.JasDBStorageException
 *		copy jasdb_core.jar from lib 
 * 
 * MVC events - MVVM
 * 		https://code.google.com/p/roboguice
 * 		http://robobinding.github.io/RoboBinding/home.html
 * 		example
 * 			https://code.google.com/p/roboguice/wiki/SimpleExample
 * 		http://www.ymc.ch/wie-macht-man-mvc-in-android-die-architektur-von-hannibal-mobile
 * 
 * location services
 * 			https://developer.android.com/google/play-services/location.html
 * 		IMPORTANT: install google play services
 *			https://developer.android.com/google/play-services/setup.html#Install
 *		IMPORTANT: register OAUTH2 token
 *			https://developer.android.com/google/auth/http-auth.html
 *			https://developers.google.com/maps/documentation/android/start?hl=de-de
 *				select Google Maps Geolocation API + Google Play Android Developer API
 *				SHA1 from eclipse / prefs / Android / Build
 *				package name must be the same as java + AndroidManifest.xml packagename
 *
 *		https://console.developers.google.com/project
			Key for Android apps (with certificates)
			API key:	
			AIzaSyC6TqhPeNRa1cNFHh8GpXx-PAPuHs8qpu0
			Android apps:	
			B2:9D:F9:90:24:79:9C:0D:88:46:A4:A1:EE:84:CF:42:94:C3:30:B5;com.lolo.focusdays.speechtotextdemo
			Activated on:	May 27, 2014 6:50 AM
			Activated by:	 lolo8304@gmail.com – you

 * 
 * 
 */

public class MainActivity extends RoboActivity   {
	
	protected static final int RESULT_SPEECH_PRICE = 1;
	protected static final int RESULT_SPEECH_DESCRIPTION = 2;
	protected static final int CAMERA_PIC_REQUEST = 3;
	protected static final int LOAD_PIC_REQUEST = 4;
	protected static final int CURRENT_LOCATION = 5;

	/* needed codes for notification */
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	
	
	private TextToSpeech ttsDescription;
	private TextToSpeech ttsPrice;

	@InjectView(R.id.btnSpeakDescription) private ImageView btnSpeakDescription;
	@InjectView(R.id.descriptionField) private EditText descriptionField;
	@InjectView(R.id.btnListenDescription) private View btnListenDescription;
	@InjectView(R.id.textKeywords) private TextView textKeywords;
	@InjectView(R.id.similarKeywords) private TextView similarKeywords;
	@InjectView(R.id.textBarcode) private TextView textBarcode;
	@InjectView(R.id.textBarcodeText) private TextView textBarcodeText;

	@InjectView(R.id.btnSpeakPrice) private ImageView btnSpeakPrice;
	@InjectView(R.id.priceField) private EditText priceField;
	@InjectView(R.id.btnListenPrice) private View btnListenPrice;

	@InjectView(R.id.imageView1) private ImageView image;

	
	@InjectView(R.id.textCurrentLocation_address) private TextView textCurrentLocationAddress;
	@InjectView(R.id.textCurrentLocation_zip) private TextView textCurrentLocationZip;
	@InjectView(R.id.textCurrentLocation_city) private TextView textCurrentLocationCity;
	@InjectView(R.id.textCurrentLocation_country) private TextView textCurrentLocationCountry;
	@InjectView(R.id.textCurrentLocation_Longitude) private TextView textCurrentLocationLongitude;
	@InjectView(R.id.textCurrentLocation_Latitude) private TextView textCurrentLocationLatitude;
	
	@InjectView(R.id.textAccounts) private TextView textAccounts;
	@InjectView(R.id.textProfile) private TextView textProfile;
	
	private Locale defaultLocale = new Locale("de", "DE");
	
	private Image2TextPresentationModel image2TextModel = new Image2TextPresentationModel(this);
	private EANPresentationModel eanModel = new EANPresentationModel();
	private SimpleAddress currentLocation;

    GoogleCloudMessaging gcm;
    String regid;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Speech2TextPresentationModel model = new Speech2TextPresentationModel();
		Binders.bind(this, R.layout.activity_main, model);
		
		
		
		btnSpeakDescription.setOnClickListener(new Speech2TextOnClickListener(this, defaultLocale, RESULT_SPEECH_DESCRIPTION));

		Text2SpeechListener text2SpeechListenerDescription = new Text2SpeechListener(btnListenDescription, defaultLocale);
		ttsDescription = new TextToSpeech(this, text2SpeechListenerDescription);
		text2SpeechListenerDescription.setTTS(ttsDescription);
		btnListenDescription.setOnClickListener(new Text2SpeechOnClickListener(ttsDescription, descriptionField));

		btnSpeakPrice.setOnClickListener(new Speech2TextOnClickListener(this, defaultLocale, RESULT_SPEECH_PRICE));

		Text2SpeechListener text2SpeechListenerPrice = new Text2SpeechListener(btnListenPrice, defaultLocale);
		ttsPrice = new TextToSpeech(this, text2SpeechListenerPrice);
		text2SpeechListenerPrice.setTTS(ttsPrice);
		btnListenPrice.setOnClickListener(new Text2SpeechOnClickListener(ttsPrice, priceField));

		displayAccounts();
		

		
	    /**
	     * restore all needed settings if a savedInstance is available, needed for rotation
	     */
		if (savedInstanceState != null) {
			this.image2TextModel = savedInstanceState.getParcelable("image2TextModel");
			
			if (this.image2TextModel != null && this.image2TextModel.hasValidImage()) {
				this.image.setImageURI(this.image2TextModel.getImageSmallUri());
				textKeywords.setText(this.image2TextModel.getKeywords());
				similarKeywords.setText(this.image2TextModel.getSimilarKeywords());
			}

			this.eanModel = savedInstanceState.getParcelable("eanModel");
			if (this.eanModel != null) {
				textBarcode.setText(this.eanModel.getFormat()+":"+this.eanModel.getBarcode());
				textBarcodeText.setText(this.eanModel.getTitle());
			}
			
			this.currentLocation = savedInstanceState.getParcelable("currentLocation");
			
			if (savedInstanceState.getDouble("marker.longitude", 0.0d) != 0.0d &&
					savedInstanceState.getDouble("marker.latitude", 0.0d) != 0.0d) {
				this.setLocation(
						new SimpleAddress(
							new LatLng(
									savedInstanceState.getDouble("marker.longitude", 0.0d),
									savedInstanceState.getDouble("marker.latitude", 0.0d)),
							savedInstanceState.getString("currentLocationAddress"),
							savedInstanceState.getString("currentLocationZip"),
							savedInstanceState.getString("currentLocationCity"),
							savedInstanceState.getString("currentLocationCountry")));
			}
			
		}
		
	    // Get the intent that started this activity
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	    	if (type.startsWith("image/")) {
	            handleSendImage(intent); // Handle single image being sent
	        }
	    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	            handleSendMultipleImages(intent); // Handle multiple images being sent
	        }
	    }
	    
	    
        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(this.getApplicationContext());
		
	}

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(this.getClass().getName(), "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(this.getClass().getName(), "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

	private void displayAccounts() {
		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccounts();
		StringBuffer buffer = new StringBuffer();

		
		for (Account account : accounts) {
			AuthenticatorDescription authenticatorDescriptionByType = this.getAuthenticatorDescriptionByType(accountManager, account.type);
			buffer
			.append(authenticatorDescriptionByType.labelId).append("\n ").append(account.name)
			.append("\n");
		}
		textAccounts.setText(buffer.toString());
		
		try {
			buffer = new StringBuffer();
			Map<String, String> list = new ListProfileAsyncTask(this).execute().get();
			for (Entry<String, String> pair : list.entrySet()) {
				buffer
				.append(pair.getKey())
				.append("=")
				.append(pair.getValue())
				.append("\n");
			}
			textProfile.setText(buffer.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private AuthenticatorDescription getAuthenticatorDescriptionByType(AccountManager mgr, String type) {
		AuthenticatorDescription[] authenticatorTypes = mgr.getAuthenticatorTypes();
		for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
			if (authenticatorDescription.type.equals(type)) return authenticatorDescription;
		}
		return null;
	}

	/** capture camera start **/

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putParcelable("image2TextModel", this.image2TextModel);
		bundle.putParcelable("eanModel", this.eanModel);
		
		if (this.currentLocation != null) {
			bundle.putString("currentLocationAddress", this.currentLocation.getAddress());
			bundle.putString("currentLocationZip", this.currentLocation.getZip());
			bundle.putString("currentLocationCity", this.currentLocation.getCity());
			bundle.putString("currentLocationCountry", this.currentLocation.getCountry());
			bundle.putDouble("marker.longitude", this.currentLocation.getPosition().longitude);
			bundle.putDouble("marker.latitude", this.currentLocation.getPosition().latitude);
		}
		
		
	}

	public void onClick_buttonCapture(View view) {
    	Image2TextPresentationModel model = new Image2TextPresentationModel(this);
    	model.setImageOriginalFullName(model.generateNewFileName());
    	
    	this.image2TextModel = model;
    	
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, model.getImageOriginalUri());
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}

	public void onClick_buttonLoad(View view) {
    	Image2TextPresentationModel model = new Image2TextPresentationModel(this);
    	this.image2TextModel = model;

		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, LOAD_PIC_REQUEST);
	}
	
	public void onClick_buttonScan(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	public void onClick_CurrentLocation(View view) {
		startActivityForResult(
				new SimpleAddress(
						this, 
						MyLocationDemoActivity.class, 
						this.getCurrentLocation()), CURRENT_LOCATION);
	}
	
	
	/**
	 * onConfigurationChanged must be overwritten to be able to roate the phone and 
	 * the app is correctly redrawn 
	 * see also method #savedInstanceState
	 * 
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int ot = getResources().getConfiguration().orientation;
		switch (ot) {
			case Configuration.ORIENTATION_LANDSCAPE: {
				if (this.image2TextModel != null && this.image2TextModel.hasValidImage()) {
					image.setImageURI(this.image2TextModel.getImageSmallUri());
				}
			}
			case Configuration.ORIENTATION_PORTRAIT: {
				if (this.image2TextModel != null && this.image2TextModel.hasValidImage()) {
					image.setImageURI(this.image2TextModel.getImageSmallUri());
				}
			}
		}
	}



	/** capture camera end **/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (ttsDescription != null) {
			ttsDescription.stop();
			ttsDescription.shutdown();
		}
		if (ttsPrice != null) {
			ttsPrice.stop();
			ttsPrice.shutdown();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		this.scanResultat(requestCode, resultCode, data);
		
		switch (requestCode) {
			case RESULT_SPEECH_PRICE: {
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					priceField.setText(this.extractNumbers(text.get(0)));
				}
				break;
			}
			case RESULT_SPEECH_DESCRIPTION: {
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					String string = text.get(0);
					if (string != null && string.length() > 0) {
						descriptionField.setText(string);
					}
				}
				break;
			}
			case CAMERA_PIC_REQUEST: {
				if (resultCode == RESULT_OK) {
					image2Text(this.image2TextModel, true);
				} else {
					new File(this.image2TextModel.getImageOriginalFullName()).delete();
				}
			}
			case LOAD_PIC_REQUEST: {
				if (resultCode == RESULT_OK && null != data) {
					this.image2TextModel.setImageOriginalFullName(this.getPath(data.getData()));
					image2Text(this.image2TextModel, false);
				}
			}
			case CURRENT_LOCATION : {
				if (resultCode == RESULT_OK && null != data) {
					this.setLocation(new SimpleAddress(data));
				}

			}
		}
	}

	private void scanResultat(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null && scanResult.isValidResult()) {
				this.eanModel.setBarcode(scanResult.getContents());
				this.eanModel.setFormat(scanResult.getFormatName());
				textBarcode.setText(eanModel.getFormatAndCode());
				
				new EANSearchAnalyzeAsyncTask(new AsyncResponse<String, Integer>() {
					@Override
					public void processTime(long timeInMs) {
					}
					
					@Override
					public void processFinish(String title) {
						eanModel.setTitle(title);
						textBarcodeText.setText(eanModel.getTitle());
					}

					@Override
					public void processProgress(Integer level) {
						
					}
				}).execute(scanResult.getContents());
		}
	}

	private void image2Text(Image2TextPresentationModel model, boolean deleteIfProcessed) {
		AsyncResponse<Image2TextPresentationModel, Image2TextPresentationModel> response = new AsyncResponse<Image2TextPresentationModel, Image2TextPresentationModel>() {
			@Override
			public void processFinish(Image2TextPresentationModel model) {
				image.setImageURI(model.getImageSmallUri());
				textKeywords.setText(model.getKeywords());
				similarKeywords.setText(model.getSimilarKeywords());
				
			}
			@Override
			public void processTime(long timeInMs) {
			}
			@Override
			public void processProgress(Image2TextPresentationModel model) {
				image.setImageURI(model.getImageSmallUri());
				textKeywords.setText("detecting keywords ...");
				similarKeywords.setText("");
			}
		};
		new Image2TextAsyncTask(this, response, false).execute(model);
	}

	private void handleSendImage(Intent intent) {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {
	    	Image2TextPresentationModel model = new Image2TextPresentationModel(this);
	    	model.setImageOriginalFullName(this.getPath(imageUri));
	    	this.image2TextModel = model;
	    	image2Text(model, false);
	    }
	}

	private void handleSendMultipleImages(Intent intent) {
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
	    if (imageUris != null) {
	    	for (Uri imageUri : imageUris) {
		    	Image2TextPresentationModel model = new Image2TextPresentationModel(this);
		    	model.setImageOriginalFullName(this.getPath(imageUri));
		    	this.image2TextModel = model;
		    	image2Text(model, false);
			}
	    }
	}	
	
	private String getPath(Uri uri) {
		if (uri != null) {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
	
			Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
			if (cursor != null) {
				try {
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
					return cursor.getString(columnIndex);
				} finally {
					cursor.close();
				}
			} else {
				return uri.getPath();
			}
		} else {
			return null;
		}
	}


	private CharSequence extractNumbers(String string) {
		if (string != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < string.length(); i++) {
				if (Character.isDigit(string.charAt(i))) {
					buffer.append(string.charAt(i));
				}
			}
			string = buffer.toString();
		}
		return string;
	}

	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void setLocation(SimpleAddress simpleAddress) {
		this.currentLocation = simpleAddress;
		this.textCurrentLocationAddress.setText(simpleAddress.getAddress());
		this.textCurrentLocationZip.setText(simpleAddress.getZip());
		this.textCurrentLocationCity.setText(simpleAddress.getCity());
		this.textCurrentLocationCountry.setText(simpleAddress.getCountry());
		this.textCurrentLocationLongitude.setText(Double.toString(
			simpleAddress.getPosition().longitude));
		this.textCurrentLocationLatitude.setText(Double.toString(
			simpleAddress.getPosition().latitude));
	}
	public SimpleAddress getCurrentLocation() {
		return this.currentLocation;
	}

}
