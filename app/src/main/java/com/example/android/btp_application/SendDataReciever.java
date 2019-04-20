package com.example.android.btp_application;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.btp_application.DatabaseFiles.DBHelper;

public class SendDataReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            Intent i5 = new Intent(context, BackEndActivity.class);
            context.startActivity(i5);
        }

        try
        {
            if (isOnline(context)) {
                Log.d("internet", "Online Connect Intenet ");
            } else {
                Log.d("internet", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /*if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                Log.d("app", "Network " + ni.getTypeName() + " connected");
                DBHelper helper = new DBHelper(context);
                if(!helper.isDatabaseEmpty()) {
                    Toast.makeText(context, "Sending data from database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Nothing to send!!", Toast.LENGTH_SHORT).show();
                }
            } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
                Log.d("app","There's no network connectivity");
            }
        }*/
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
