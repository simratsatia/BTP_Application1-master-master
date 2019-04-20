package com.example.android.btp_application;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SMSCount extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllSms(this);
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAllSms(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            int cinbox = 0, coutbox = 0, csent = 0;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        Date current = cal.getTime();
                        SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
                        String newDateStr = postFormater.format(current);
                        Log.d("TODAY", newDateStr);

                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        Date dateFormat = new Date(Long.valueOf(smsDate));
                        SimpleDateFormat postFormater1 = new SimpleDateFormat("MMMM dd, yyyy");
                        String newDateStr1 = postFormater1.format(dateFormat);
                        Log.d("DATE", newDateStr1);

                        String type;
                        if (newDateStr.equals(newDateStr1)) {
                            switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                                case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                    type = "inbox";
                                    cinbox++;
                                    break;
                                case Telephony.Sms.MESSAGE_TYPE_SENT:
                                    type = "sent";
                                    csent++;
                                    break;
                                case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                                    type = "outbox";
                                    coutbox++;
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            break;
                        }

                        c.moveToNext();
                    }
                }

                c.close();

            } else {
                Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
            }
            Log.d("sms_count", "INBOX: " + cinbox + " , OUTBOX: " + coutbox + " , SENT: " + csent);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("sms_count",cinbox+coutbox+csent);
            setResult(Activity.RESULT_OK,returnIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getAllSms(this);
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
