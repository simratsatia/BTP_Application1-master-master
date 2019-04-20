package com.example.android.btp_application;

import java.util.ArrayList;

public class SendJSON {

    public ArrayList<String> appusage_name;
    public ArrayList<Float> appusage_duration;
    public int scn_count;
    public Long screen_duration;
    public Long total_calls;
    public Long call_duration;
    public int sms_count;

    public SendJSON() {
        this.appusage_duration = new ArrayList<>();
        this.appusage_name = new ArrayList<>();
    }

}
