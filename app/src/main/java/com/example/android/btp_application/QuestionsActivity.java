package com.example.android.btp_application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class QuestionsActivity extends AppCompatActivity {

    int score = 0;
    int esteem_score = 0;
    ArrayList<String> al;
    ArrayList<RadioGroup> rglist;
    Button submit;
    SharedPreferences sharedPreferences;
    private static final int PERMISSIONS_REQUEST_READ_LOGS = 101;
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;
    public static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 102;
    public String emni_no = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean previouslyStarted = sharedPreferences.getBoolean("pref_previously_started", false);

        if(previouslyStarted) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }


        int permissions_code = 42;
        String[] permissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE};

        if(!hasPermissions(permissions)){
            ActivityCompat.requestPermissions(this, permissions, permissions_code);
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


       /* while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_LOGS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }


        while (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
            return;
        }*/ /*else {
            emni_no = telephonyManager.getDeviceId();
            Log.d("IMEI", "IMEI: " + emni_no);
        }*/
        setContentView(R.layout.question_scrollview);
        submit = (Button) findViewById(R.id.submit) ;
        rglist = new ArrayList<>();
        rglist.add((RadioGroup) findViewById(R.id.rg1));
        rglist.add((RadioGroup) findViewById(R.id.rg2));
        rglist.add((RadioGroup) findViewById(R.id.rg3));
        rglist.add((RadioGroup) findViewById(R.id.rg4));
        rglist.add((RadioGroup) findViewById(R.id.rg5));
        rglist.add((RadioGroup) findViewById(R.id.rg6));
        rglist.add((RadioGroup) findViewById(R.id.rg7));
        rglist.add((RadioGroup) findViewById(R.id.rg8));
        rglist.add((RadioGroup) findViewById(R.id.rg9));
        rglist.add((RadioGroup) findViewById(R.id.rg10));

//        for(final RadioGroup rg: rglist){
//            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    if(rg.getCheckedRadioButtonId()!=-1){
//                        int id= rg.getCheckedRadioButtonId();
//                        View radioButton = rg.findViewById(id);
//                        int radioId = rg.indexOfChild(radioButton);
//                        RadioButton btn = (RadioButton) rg.getChildAt(radioId);
//                        String selection = (String) btn.getText();
//                        score+= returnscore(selection);
//                    }
//                }
//            });
//
//        }


        final RadioGroup esteemrg = (RadioGroup) findViewById(R.id.rg11);
        esteemrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(esteemrg.getCheckedRadioButtonId()!=-1){
                    int id= esteemrg.getCheckedRadioButtonId();
                    View radioButton = esteemrg.findViewById(id);
                    int radioId = esteemrg.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) esteemrg.getChildAt(radioId);
                    String selection = (String) btn.getText();

                    switch (selection) {
                        case "1": esteem_score = 1;
                            break;
                        case "2": esteem_score = 2;
                            break;
                        case "3": esteem_score = 3;
                            break;
                        case "4": esteem_score = 4;
                            break;
                        case "5": esteem_score = 5;
                            break;
                        case "6": esteem_score = 6;
                            break;
                        case "7": esteem_score = 7;
                            break;
                    }
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
           @Override
          public void onClick(View v) {
               //Calculate the score
                for (RadioGroup rg: rglist){
                    int id= rg.getCheckedRadioButtonId();
                    View radioButton = rg.findViewById(id);
                    int radioId = rg.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) rg.getChildAt(radioId);
                    String selection = (String) btn.getText();
                    score+= returnscore(selection);
                }
                Log.d("SCORE", score + "");
                Log.d("ESTEEM_SCORE", esteem_score + "");
                if(checkAllQuestionsAnswered()) {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putBoolean("pref_previously_started", Boolean.TRUE);
                    edit.commit();
                    savePref();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        });

//        submit = (Button) findViewById(R.id.submit) ;
//        rv = (RecyclerView) findViewById(R.id.rv1);
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("SCORE", sharedPreferences.getInt("score",0) + "");
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(i);
//            }
//        });

    }

    Boolean checkAllQuestionsAnswered() {
        for(int i = 0; i < rglist.size(); i++) {
            if(rglist.get(i).getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please fill the form! ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }


    public void savePref(){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("score",score );
        edit.putInt("esteem_score", esteem_score);
        edit.commit();
        return;
    }

    private boolean hasPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    private int returnscore(String selection){
        int retval = 0;

        switch (selection) {
            case "Strongly agree": return 6;

            case "Agree": return 5;

            case "Weakly agree": return 4;

            case "Weakly disagree": return 3;

            case "Disagree": return 2;

            case "Strongly disagree": return 1;

        }
        return retval;
    }


}
