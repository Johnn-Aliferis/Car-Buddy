package com.example.carbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.TaskInfo;
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private final int REQ_CODE = 100;
    private String numberToCall;
    private String myVoiceCommand = null;
    private String message;
    private int positionOfSpokenWord;

    @BindView(R.id.textView4)
    TextView myTextview;
    @BindView(R.id.myconst)
    ConstraintLayout constraint;
    AnimationDrawable animationDrawable;
    mySpeechRecogniser listener = new mySpeechRecogniser();
    SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        myTextview.setText(null);
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
        startActivity(mSpeechRecognizerIntent);
    }

    // Restart speech recogniser
    public void restartSpeechListener() {
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.destroy();
        startRecognising();
    }

    // Listening state of phone call & initiate phone call
    public void startPhoneCall() throws SecurityException{
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse(numberToCall));
        startActivity(phoneIntent);
    }

    public void muteAudio(){
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
    }

    // Declaring inner java class for Speech Recognition

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

                if (matches.contains("Hi" ) ||matches.contains("hi" ) ) {
                myTextview.setText("Hi");
                }
            else if (matches.contains("call home") || matches.contains("call Home")) {
                myTextview.setText("Calling Home...");
                numberToCall="tel:2109655279";
                // Todo : Get list of contacts on user's phone and not parse the numbers hard-coded !
                // Todo : Maybe translate the speech input to users
                // Todo : Later date -->  implement model for speech recognition with TfLite.
                requestPhoneCallPermission();
            }
            else if (matches.contains("close")) {
                MainActivity.this.finish();
            }
            else {
                myTextview.setText("Nothing");
                }
            startRecognising();
            }
            catch (Exception ignored) {}
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    // Declare inner class declaration for phone listener

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended, need detect flag
                if (isPhoneCalling) {
                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    isPhoneCalling = false;
                }
            }
        }
    }
}