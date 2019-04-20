package com.example.android.btp_application;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppUsageDuration extends AppCompatActivity {

    ArrayList<String> name;
    ArrayList<Float> duration;

    public class info{
        String appname;
        float duration;

        public info(String appname, float duration) {
            this.appname = appname;
            this.duration = duration;
        }

        public String getAppname() {
            return appname;
        }

        public float getDuration() {
            return duration;
        }
    }

    public Calendar getendCal(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23); // For 1 PM or 2 PM
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Log.d("cal", cal.getTime().toGMTString());
        return cal;
    }

    public Calendar getbeginCal(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0); // For 1 PM or 2 PM
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        Log.d("cal", cal.getTime().toGMTString());
        return cal;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_usage_duration);
        //recyclerView = (RecyclerView) findViewById(R.id.rv2);

        /*Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);*/

        final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        Calendar beginCal = getbeginCal();
        Calendar endCal = getendCal();

        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        Log.d("TAG", "results for " + beginCal.getTime().toGMTString() + " - " + endCal.getTime().toGMTString());
        ArrayList<info> al = new ArrayList<>();
        for (UsageStats app : queryUsageStats) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String FirstTime = formatter.format(new Date(app.getFirstTimeStamp()));
            String LastTime = formatter.format(new Date(app.getLastTimeStamp()));
            Log.d("app_duration", app.getPackageName() + " | " + (float) (app.getTotalTimeInForeground() / 60000) + " First Time: " + FirstTime+ " Last Time: " + LastTime) ;
            if(app.getTotalTimeInForeground() != 0) {
                al.add(new info(app.getPackageName(), (float) (app.getTotalTimeInForeground() / 1000.0)));
            }

        }

        makenewArrayList(al);
        Log.d("TAG", al.size() + "");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("appusage_name", (Serializable) name);
        returnIntent.putExtra("appusage_duration", (Serializable) duration);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

        //AppUsageAdapter mAdapter = new AppUsageAdapter(this, al);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(mAdapter);
    }

    private void makenewArrayList(ArrayList<info> al) {
        name = new ArrayList<>();
        duration = new ArrayList<>();
        for(info i : al){
            name.add(i.getAppname());
            duration.add(i.getDuration());
        }

    }
}
