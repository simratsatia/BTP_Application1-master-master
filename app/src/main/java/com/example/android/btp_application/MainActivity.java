package com.example.android.btp_application;

import android.Manifest;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.btp_application.DatabaseFiles.DBHelper;
import com.example.android.btp_application.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    ArrayList<String> al;
    Button btn, app_info_btn, data_usage_btn, calllogs_btn, smscount_btn, setalarm_btn, send_data;

    long total_calls;
    long duration;
    private BroadcastReceiver mNetworkReceiver;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = sharedPreferences.getBoolean("pref_previously_started", false);

        btn = findViewById(R.id.screen_time);
        app_info_btn = (Button) findViewById(R.id.app_usage_info);
        data_usage_btn = (Button) findViewById(R.id.data_usage);
        calllogs_btn = (Button) findViewById(R.id.call_logs);
        smscount_btn = (Button) findViewById(R.id.sms_count);
        setalarm_btn = (Button) findViewById(R.id.set_alarm);
        send_data = (Button) findViewById(R.id.send_data);

        btn.setOnClickListener(this);
        app_info_btn.setOnClickListener(this);
        data_usage_btn.setOnClickListener(this);
        calllogs_btn.setOnClickListener(this);
        smscount_btn.setOnClickListener(this);
        setalarm_btn.setOnClickListener(this);
        send_data.setOnClickListener(this);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        /*if(!previouslyStarted) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("pref_previously_started", Boolean.TRUE);
            edit.commit();

            Intent i = new Intent(this, QuestionsActivity.class);
            startActivity(i);
        }
        else{
            //Intent i = new Intent(this, QuestionsActivity.class);
            //startActivity(i);
        }*/
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.screen_time:  Intent i = new Intent(this, ScreenTime.class);
                startActivity(i);
                break;
            case R.id.app_usage_info:  Intent i1 = new Intent(this, AppUsageDuration.class);
                startActivity(i1);
                break;

            case R.id.data_usage: //Intent i2 = new Intent(this, ListActivity.class);
                //startActivity(i2);
                DBHelper helper = new DBHelper(this);
                helper.clearDatabase();
                helper.show_Data_app();
                helper.show_Data_generic();
                break;
            case R.id.call_logs:  Intent i3 = new Intent(this, CallLogs.class);
                startActivityForResult(i3, 1);
                break;
            case R.id.sms_count:  Intent i4 = new Intent(this, SMSCount.class);
                startActivity(i4);
                break;
            case R.id.set_alarm:
                //setAlarmManager();
                break;

            case R.id.send_data:
                Intent i5 = new Intent(this, BackEndActivity.class);
                startActivity(i5);
                break;

        }
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}
