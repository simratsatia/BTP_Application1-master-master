package com.example.android.btp_application;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class IntentActivity extends AppCompatActivity {

    long total_calls;
    long call_duration;
    int sms_count;
    ArrayList<String> netusage_name;
    ArrayList<Long> netusage_data;
    ArrayList<String> appusage_name;
    ArrayList<Float> appusage_duration;
    Intent returnIntent = new Intent();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
//        Intent i = new Intent(this, ScreenTime.class);
//        startActivity(i);

        Toast.makeText(this, "All activites about to be executed!!", Toast.LENGTH_SHORT).show();
//
//        Intent i1 = new Intent(this, ListActivity.class);
//        startActivityForResult(i1,1);

        Intent i2= new Intent(this, AppUsageDuration.class);
        startActivityForResult(i2,2);

        Intent i3 = new Intent(this, CallLogs.class);
        startActivityForResult(i3,3);

        Intent i4 = new Intent(this, SMSCount.class);
        startActivityForResult(i4,4);
//
        Intent i5 = new Intent(this, ScreenTime.class);
        startActivity(i5);


       // new SeparateThread1().execute();


        Toast.makeText(this, "All activites executed!!", Toast.LENGTH_SHORT).show();



    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("total_calls",total_calls);
//        returnIntent.putExtra("duration",call_duration);
//        returnIntent.putExtra("sms_count",sms_count);
//        returnIntent.putExtra("netusage_name",(Serializable)netusage_name);
//        returnIntent.putExtra("netusage_data",(Serializable)netusage_data);
//        returnIntent.putExtra("appusage_name",(Serializable)appusage_name);
//        returnIntent.putExtra("appusage_duration",(Serializable)appusage_duration);
//        setResult(Activity.RESULT_OK,returnIntent);
//
//    }
//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (requestCode == 1) {
//            if(resultCode == Activity.RESULT_OK){
//                netusage_name = (ArrayList<String>) data.getSerializableExtra("netusage_name");
//                netusage_data = (ArrayList<Long>) data.getSerializableExtra("netusage_data");
//                returnIntent.putExtra("netusage_name",(Serializable)netusage_name);
//                returnIntent.putExtra("netusage_data",(Serializable)netusage_data);
//
//                if(returnIntent.getExtras().size() == 7) {
//                    setResult(Activity.RESULT_OK, returnIntent);
//                   // finish();
//                }
//
//                for(String item : netusage_name) {
//                    Log.d("netusage_return",item );
//                }
//                for(Long item : netusage_data) {
//                    Log.d("netusage_return",item+"" );
//                }
//            }
//        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                appusage_name = (ArrayList<String>) data.getSerializableExtra("appusage_name");
                appusage_duration = (ArrayList<Float>) data.getSerializableExtra("appusage_duration");
                returnIntent.putExtra("appusage_name",(Serializable)appusage_name);
                returnIntent.putExtra("appusage_duration",(Serializable)appusage_duration);

                if(returnIntent.getExtras().size() == 5) {
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                for(String item : appusage_name) {
                    Log.d("appusage_return",item );
                }
                for(Float item : appusage_duration) {
                    Log.d("appusage_return",item+"" );
                }
            }
        }

        if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                total_calls= (long) data.getLongExtra("total_calls",0);
                call_duration= (long) data.getLongExtra("duration",0);
                returnIntent.putExtra("total_calls",total_calls);
                returnIntent.putExtra("duration",call_duration);

                if(returnIntent.getExtras().size() == 5) {
                    setResult(Activity.RESULT_OK, returnIntent);
                   finish();
                }


                Log.d("call_return", total_calls + "+" + call_duration);
            }
        }

        if (requestCode == 4) {
            if(resultCode == Activity.RESULT_OK){
                sms_count = (int) data.getIntExtra("sms_count",0);
                returnIntent.putExtra("sms_count",sms_count);

                if(returnIntent.getExtras().size() == 5) {
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                Log.d("sms_return", sms_count+"");
            }
        }
    }

}
