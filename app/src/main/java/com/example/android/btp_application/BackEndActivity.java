package com.example.android.btp_application;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.telephony.TelephonyManager;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BackEndActivity extends AppCompatActivity {

    Button send_data;
    SharedPreferences sharedPreferences;

    //Data members to be used
    int scn_count;
    Long screen_duration;
    long total_calls;
    long call_duration;
    int sms_count;
    int ques_score;
    int esteem_score;
    ArrayList<String> netusage_name;
    ArrayList<Long> netusage_data;
    ArrayList<String> appusage_name;
    ArrayList<Float> appusage_duration;
    public static final int ia_code = 1212;
    DBHelper helper;
    ProgressDialog dialog;
    private BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend);
        //Call intent activity for all information
        Intent intent_activity = new Intent(getApplicationContext(), IntentActivity.class);
        startActivityForResult(intent_activity, ia_code);
        registerNetworkBroadcastForNougat();
        //Activity called

    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ia_code) {
            if (resultCode == Activity.RESULT_OK) {
                //netusage_name = (ArrayList<String>) data.getSerializableExtra("netusage_name");
                //netusage_data = (ArrayList<Long>) data.getSerializableExtra("netusage_data");
                netusage_name = new ArrayList<>();
                netusage_name.add("T1");
                netusage_data = new ArrayList<>();
                netusage_data.add((long) 1);
                appusage_name = (ArrayList<String>) data.getSerializableExtra("appusage_name");
                appusage_duration = (ArrayList<Float>) data.getSerializableExtra("appusage_duration");
                total_calls = (long) data.getLongExtra("total_calls", 0);
                call_duration = (long) data.getLongExtra("duration", 0);
                sms_count = (int) data.getIntExtra("sms_count", 0);
                sharedPref();
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        work();
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        work();
                    }
                } else {
                    Toast.makeText(this, "No Internet ", Toast.LENGTH_SHORT).show();
                    //Make Database
                    helper = new DBHelper(this);
                    helper.insertData_generic(scn_count, screen_duration, total_calls, call_duration, sms_count);
                    helper.insertData_app(appusage_name, appusage_duration);

                    // see Database
                    helper.show_Data_app();
                    helper.show_Data_generic();

                    finish();
                }
            }
        }
    }

    public void work() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("screen_count", scn_count);
            postData.put("duration_of_screen_on", screen_duration);

            // doa
            JSONArray doa = new JSONArray();
            for (int i = 0; i < appusage_name.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("app_name", appusage_name.get(i));
                obj.put("duration_of_app", appusage_duration.get(i));
                doa.put(obj);
            }
            postData.put("doa", doa);

            // noa
            JSONArray noa = new JSONArray();
            for (int i = 0; i < netusage_data.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("app_name", netusage_name.get(i));
                obj.put("duration_of_app", netusage_data.get(i));
                noa.put(obj);
            }
            postData.put("noa", noa);

            //calls
            postData.put("no_of_calls", total_calls);

            // messages
            postData.put("no_of_messages", sms_count);

            //Code to get IMEI number
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

            //Code to get date
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date c = cal.getTime();
//            String newDateStr = c.toGMTString();
            SimpleDateFormat postFormater = new SimpleDateFormat("MMMM dd, yyyy");
            String newDateStr = postFormater.format(c);
//            Log.d("TODAY", newDateStr);

            //New elements being added here
            postData.put("call_duration", call_duration);
            postData.put("IMEI", imei_no);
            postData.put("ques_score",ques_score);
            postData.put("esteem_score",esteem_score);
            postData.put("date",newDateStr);

            new SendDeviceDetails().execute("https://phoneusageapp.herokuapp.com/api", postData.toString());
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sharedPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        scn_count = sharedPreferences.getInt("screencount", 0);
        screen_duration = sharedPreferences.getLong("duration", 0);
        ques_score = sharedPreferences.getInt("score", 0);
        esteem_score = sharedPreferences.getInt("esteem_score",0);

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
            finish();
        }
    }
}
