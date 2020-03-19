package com.example.carbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class mySpeechRecogniser extends Activity{

    /*private String message = "" ;
    private ArrayList<String> matches  = new ArrayList<>();
    private int position;
    private String voiceCommand="";
    private boolean isListening = true;
    private Context mContext;

    public mySpeechRecogniser (Context c){
        this.mContext=c;
    }
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

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches.contains("hello")){
            position = (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).
                    indexOf("hello"));
            voiceCommand=matches.get(position);
        }
        else if (matches.contains("hi")){
            position = (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).
                    indexOf("hi"));
            voiceCommand=matches.get(position);
        }
        else {
            //main.startRecognising();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public ArrayList<String> getMatches() {
        return this.matches;
    }
    public String myVoiceCommand(){
        return voiceCommand;
    }
    public boolean isListening(){
        return isListening;
    }*/
}
