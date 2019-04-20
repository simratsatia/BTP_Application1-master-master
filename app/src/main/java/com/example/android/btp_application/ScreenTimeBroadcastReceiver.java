package com.example.android.btp_application;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class ScreenTimeBroadcastReceiver extends BroadcastReceiver {

    private long startTimer = System.currentTimeMillis();
    private long endTimer;
    private long screenOnTimeSingle;
    private long screenOnTime;
    private final long TIME_ERROR = 1000;
    SharedPreferences sharedPreferences;
    Context context;
    int scn_count;
    long duration;

    public ScreenTimeBroadcastReceiver (Context c){
        this.context = c;
        sharedPref();
    }


    public void onReceive(Context context, Intent intent) {

        Log.d("LOG", "ScreenTimeService onReceive");

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startTimer = System.currentTimeMillis();
            Log.d("LOG", "startTimer: " + Long.toString(((startTimer))/1000));
            scn_count++;
            Log.d("LOG", "count: " + Long.toString((scn_count)));
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            endTimer = System.currentTimeMillis();
            Log.d("LOG", "startTimer: " + Long.toString(((startTimer))/1000));
            Log.d("LOG", "endTimer: " + Long.toString(((endTimer))/1000));
            screenOnTimeSingle = endTimer - startTimer;

            duration += screenOnTimeSingle;
            Log.d("LOG", "screenOnTime: " + Long.toString((duration)/1000));

            //Change shared preferences
            savePref();

        }

    }

    public void savePref(){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("screencount", scn_count);
        edit.putLong("duration", duration);
        edit.commit();
        return;
    }

    public void sharedPref(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        scn_count = sharedPreferences.getInt("screencount",0);
        duration = sharedPreferences.getLong("duration",0);
    }

}
