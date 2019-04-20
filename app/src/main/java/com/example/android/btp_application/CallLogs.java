package com.example.android.btp_application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallLogs extends AppCompatActivity {

    TextView call;
    private static final int PERMISSIONS_REQUEST_READ_LOGS = 101;
    long total_duration = 0;
    long total_calls = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCallDetails();
        finish();
    }



    private void getCallDetails() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_LOGS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            StringBuffer sb = new StringBuffer();
            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");


            managedCursor.moveToLast();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date c = cal.getTime();
            SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
            String newDateStr = postFormater.format(c);
            Log.d("TODAY", newDateStr);

            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat postFormater1 = new SimpleDateFormat("MMMM dd, yyyy");
            String newDateStr1 = postFormater1.format(callDayTime);
            Log.d("DATE", newDateStr1);

            if (newDateStr.equals(newDateStr1)) {
                total_calls ++;
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                total_duration += managedCursor.getLong(duration);
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");

            }

            while (managedCursor.moveToPrevious()) {

                phNumber = managedCursor.getString(number);
                callType = managedCursor.getString(type);
                callDate = managedCursor.getString(date);
                callDayTime = new Date(Long.valueOf(callDate));
                postFormater1 = new SimpleDateFormat("MMMM dd, yyyy");
                newDateStr1 = postFormater1.format(callDayTime);
                Log.d("DATE", newDateStr1);

                if (newDateStr.equals(newDateStr1)) {
                    total_calls ++;
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    total_duration += managedCursor.getLong(duration);
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");

                } else {
                    break;
                }
            }
            managedCursor.close();
        }

        Log.d("call_log", "Duration: " + Long.toString(total_duration) + " Call Count: " + Long.toString(total_calls));
        Intent returnIntent = new Intent();
        returnIntent.putExtra("total_calls",total_calls);
        returnIntent.putExtra("duration",total_duration);
        setResult(Activity.RESULT_OK,returnIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_LOGS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getCallDetails();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
