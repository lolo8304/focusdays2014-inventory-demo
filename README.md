focusdays2014-inventory-demo-app
============================

this demo shows with 2 Android Activites all possible sensors that could be integrated into a innovative houshold / collection inventory app

implemented sensors
----------------
- speech 2 text (for textfields and integerfields)
- text 2 speech
- image 2 text (via Google Website using camera or gallery intent)
- barcode2text (via zxing barcode app and codecheck.info for EAN code explanations)
- Google maps and localization
- android accounts information
- android profile information


important notice
----------------
- the Google API key is bound to the eclipse / SHA1 key (under preferences / android / ) and the name of the package in android.manifest
- to build the app and run the app you need to copy the file debug.keystore in the root directory of the project into you $HOME/.android directory

precondition to build the app
----------------
- Google SDK installed
- eclipse with Android development tools installed
- project-dependencies: google-play-services_lib project (in same repo)
- speech2text: install needed language packs for speech recognition on your android
- USB debugging enabled on phone (see link focusdays sharepoint how to do it)

the following online services are used to run some sensors
----------------
speech2text: 
- none, android native

text2speech: 
- none, android native, speech recognitions installed

image2text: 
 
barcode2text
- barcode app (free): https://play.google.com/store/apps/details?id=com.google.zxing.client.android&hl=de 
- (unfortuntaly the intent integration to other apps does not support code2text only image2code detection :-(
- eansearch service: http://raspi-lolo.dyndns.org/upload/  using codecheck.info via HTTP integration