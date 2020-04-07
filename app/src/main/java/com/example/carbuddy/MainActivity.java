package com.example.carbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AsyncResponse {


    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private final int REQ_CODE = 100;
    private String numberToCall;
    private String myVoiceCommand = null;
    private String message;
    private int positionOfSpokenWord;
    private ArrayList<String> myList = new ArrayList<>();

    @BindView(R.id.textView4)
    TextView myTextview;
    @BindView(R.id.myconst)
    ConstraintLayout constraint;
    AnimationDrawable animationDrawable;
    mySpeechRecogniser listener = new mySpeechRecogniser();
    SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private GetContacts getContacts ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        myTextview.setText(null);
        getContacts = new GetContacts(this);
        getContacts.delegate=this;
        startAnimation();
        dontLock();
        muteAudio();
        requestAudioPermissions();
    }


    // Requesting permissions
    private void requestAudioPermissions() {
        if(ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
        else {
                startRecognising();
             }
        }

    private void requestPhoneCallPermission() {
        if(ContextCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2);
        }
        else {
            startPhoneCall();
        }
    }
    private void requestContactsPermission() {
        if(ContextCompat.checkSelfPermission( this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    3);
        }
    }

    // Getting result of permissions granted by user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted !", Toast.LENGTH_LONG).show();
                startRecognising();
            } else {
                Toast.makeText(this, "You must allow permissions to run the app ! ", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        else if (requestCode==2) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted !", Toast.LENGTH_LONG).show();
                startPhoneCall();
            }
            else {
                Toast.makeText(this, "You must allow permissions to run the app ! ", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        else if (requestCode==3) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted !", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "You must allow permissions to run the app ! ", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    // Initiate background animation
    public void startAnimation() {
        animationDrawable = (AnimationDrawable) constraint.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();
    }

    // Keep application from locking
    public void dontLock() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
    }

    // Start speech Recogniser
    public void startRecognising() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "el");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        //TODO : After second time getting contacts speechListener does not hear anymore.. !
    }

    // Restart speech recogniser
    public void restartSpeechListener() {
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.destroy();
        startRecognising();
    }

    // Listening state of phone call & initiate phone call
    public void startPhoneCall() throws SecurityException{
        PhoneCallListener phoneListener = new PhoneCallListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse(numberToCall));
        startActivity(phoneIntent);
    }


    //Todo : check this because it mutes, but user can choose to unmute ,
    // which will be very annoying hearing that beep all the time !
    public void muteAudio(){
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
    }

    // executing asyncTask to get list of contacts
    public void getContacts(){
        getContacts.execute();
    }

    @Override
    public void myAsyncResponseGetContacts(HashMap<String, String> output) {
        //Todo :  now that we have contacts , import logic to our project !
        StringBuilder str1  = new StringBuilder();
        Map.Entry<String,String> entry = output.entrySet().iterator().next();
        String name;
        String phone;
        for (int i = 0 ; i < output.size() ; i++){
            name = entry.getKey();
            phone = entry.getValue();
            str1.append(name).append(" ").append(phone).append(" \n");
        }
        int size = output.size();
        myTextview.setText( String.valueOf(size) + str1);
        getContacts= new GetContacts(this);
        getContacts.delegate=this;
    }

    // Declaring inner java class for Speech Recognition
    // Todo : Create 3 different speech listeners (one for voice commands, one for contacts matchup and one for dialing numbers ! ) // or find a better way to import logic to our initial one
    //  we will need to make our existing listener into another class to be used in the other areas of the apps as well !

    @SuppressLint("Registered")
    public class mySpeechRecogniser extends Activity implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            try {
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
                }
            }
            finally {
                restartSpeechListener();
            }
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            try {

                if(matches.contains("Contacts") ||matches.contains("contacts")) {
                    requestContactsPermission();
                    MainActivity.this.getContacts();
                    myTextview.setText("Contacts ! ");
                }
            else if (matches.contains("call home") || matches.contains("call Home")) {
                myTextview.setText("Calling Home...");
                numberToCall="tel:2109655279";
                // Todo : Get list of contacts
                // Todo : Later date -->  Implement model for speech recognition with TfLite.
                requestPhoneCallPermission();
            }
            else if (matches.contains("close")) {
                MainActivity.this.finish();
            }
            else {
                myTextview.setText("Nothing");
                }
            }
            catch (Exception ignored) {Toast.makeText(MainActivity.this,ignored.toString(),Toast.LENGTH_SHORT).show();}
            finally {
                startRecognising();
            }

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }
}