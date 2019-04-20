package com.example.android.btp_application;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.android.btp_application.DatabaseFiles.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NetworkChangeReceiver extends BroadcastReceiver
{
    DBHelper helper;
    Context ctx;
    int ques_score;
    int esteem_score;
   // Context context1;

//    public NetworkChangeReceiver(Context context) {
//        this.context1 = context;
//    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        helper = new DBHelper(context);
        ctx = context;
        try
        {
            if (isOnline(context)) {
                Log.d("internet", "Online Connect Intenet ");
                if(!helper.isDatabaseEmpty()) {
                    SendJSON sendJSON = helper.sendDataObj();
                    work(sendJSON);
                }
            } else {
                Log.d("internet", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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

    public void work(SendJSON sendJSON) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("screen_count", sendJSON.scn_count);
            postData.put("duration_of_screen_on", sendJSON.screen_duration);

            // doa
            JSONArray doa = new JSONArray();
            for (int i = 0; i < sendJSON.appusage_name.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("app_name", sendJSON.appusage_name.get(i));
                obj.put("duration_of_app", sendJSON.appusage_duration.get(i));
                doa.put(obj);
            }
            postData.put("doa", doa);

            // noa
            JSONArray noa = new JSONArray();
            /*for (int i = 0; i < netusage_data.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("app_name", netusage_name.get(i));
                obj.put("duration_of_app", netusage_data.get(i));
                noa.put(obj);
            }*/
            postData.put("noa", noa);

            //calls
            postData.put("no_of_calls", sendJSON.total_calls);

            // messages
            postData.put("no_of_messages", sendJSON.sms_count);

            //To get IMEI number
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String imei_no = telephonyManager.getDeviceId();

            // to get date of data send
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date c = cal.getTime();
//            String newDateStr = c.toGMTString();
            SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
            String newDateStr = postFormater.format(c);
//            Log.d("TODAY", newDateStr);

            //Get Scores of questions
            sharedPref();

            //New elements being added here
            postData.put("call_duration", sendJSON.call_duration);
            postData.put("IMEI", imei_no);
            postData.put("ques_score",ques_score);
            postData.put("esteem_score",esteem_score);
            postData.put("date",newDateStr);

            new SendDeviceDetails().execute("https://phoneusageapp.herokuapp.com/api", postData.toString());

            //Set screen count and duration to 0
            savePref();
            DBHelper helper = new DBHelper(ctx);
            helper.clearDatabase();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sharedPref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        ques_score = sharedPreferences.getInt("score", 0);
        esteem_score = sharedPreferences.getInt("esteem_score",0);
    }

    public void savePref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("screencount", 0);
        edit.putLong("duration", 0);
        edit.commit();
        return;
    }



    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            /*dialog = new ProgressDialog(getApplicationContext());
            dialog.setMessage("Uploading Data. Please Wait..");
            dialog.show();*/
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            DBHelper helper = new DBHelper(ctx);
            helper.clearDatabase();
            super.onPostExecute(result);
            Log.d("send_data", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            /*try {
                dialog.dismiss();
            } catch (final IllegalArgumentException e) {
                // Do nothing.
            } catch (final Exception e) {
                // Do nothing.
            } finally {
                dialog = null;
            }*/
        }
    }
}