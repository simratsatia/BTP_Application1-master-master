package com.example.android.btp_application.DatabaseFiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.btp_application.SendJSON;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db";

    public static final String TABLE_NAME1 = "GenericData";

    public static final String SCREEN_COUNT = "screen_count";

    public static final String SCREEN_DURATION = "screen_duration";

    public static final String TOTAL_CALLS = "total_calls";

    public static final String CALL_DURATION = "call_duration";

    public static final String SMS_COUNT = "sms_count";

    public static final String APP_NAME = "appusage_name";

    public static final String APP_DURATION = "appusage_duration";


    public static final String TABLE_NAME2 = "AppData";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CREATE1 = "create table if not exists "+ TABLE_NAME1 +
            " ( screen_count integer not null ," +

            " screen_duration integer not null , " +

            " total_calls integer not null , " +

            " call_duration integer not null, " +

            " sms_count integer not null);";

    public static final String TABLE_CREATE2 = "create table if not exists "+ TABLE_NAME2 +
            " ( appusage_name text not null ," +

            " appusage_duration integer not null);";


    SQLiteDatabase db;

    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //Log.d("pathInfo",context.getDatabasePath("database2.db").toString());

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;

        db.execSQL(TABLE_CREATE1);
        db.execSQL(TABLE_CREATE2);

        String path = db.getPath().toString();

        Log.d("path0",path);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData_generic(int scn_count,
                           long scn_duration,
                           long total_calls,
                           long call_duration,
                           int sms_count) {

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SCREEN_COUNT, scn_count);
        values.put(SCREEN_DURATION, scn_duration);
        values.put(TOTAL_CALLS, total_calls);
        values.put(CALL_DURATION, call_duration);
        values.put(SMS_COUNT, sms_count);

        db.insert(TABLE_NAME1 ,null ,values);

        db.close();
    }


    public void insertData_app(
            ArrayList<String> appusage_name,
            ArrayList<Float> appusage_duration) {

        for(int i = 0 ;i < appusage_duration.size();i++){

            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(APP_NAME,appusage_name.get(i));
            values.put(APP_DURATION,appusage_duration.get(i));
            db.insert(TABLE_NAME2 ,null ,values);
            db.close();
        }
    }


    public void show_Data_generic() {
        Log.d("TABLE1", getTableAsString(TABLE_NAME1));
    }

    public void show_Data_app() {
        Log.d("TABLE2", getTableAsString(TABLE_NAME2));
    }

    public String getTableAsString(String tableName) {
        db = this.getReadableDatabase();

        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        Log.d("TABLE_DATA", tableString);
        return tableString;
    }

    public SendJSON sendDataObj() {
        SendJSON sendJSON = new SendJSON();
        db = this.getReadableDatabase();
        Cursor allRows1  = db.rawQuery("SELECT * FROM " + "GenericData", null);
        Cursor allRows2  = db.rawQuery("SELECT * FROM " + "AppData", null);
        if (allRows1.moveToFirst() ){
            String[] columnNames = allRows1.getColumnNames();
            do {
                for (String name: columnNames) {
                    switch (name){
                        case SCREEN_COUNT: sendJSON.scn_count = allRows1.getInt(allRows1.getColumnIndex(name));
                            break;
                        case SCREEN_DURATION: sendJSON.screen_duration = allRows1.getLong(allRows1.getColumnIndex(name));
                            break;
                        case TOTAL_CALLS: sendJSON.total_calls = allRows1.getLong(allRows1.getColumnIndex(name));
                            break;
                        case CALL_DURATION: sendJSON.call_duration = allRows1.getLong(allRows1.getColumnIndex(name));
                            break;
                        case SMS_COUNT: sendJSON.sms_count = allRows1.getInt(allRows1.getColumnIndex(name));
                            break;
                    }
                }

            } while (allRows1.moveToNext());
        }

        if (allRows2.moveToFirst()) {
            while (!allRows2.isAfterLast()) {
                String name = allRows2.getString(allRows2.getColumnIndex(APP_NAME));
                sendJSON.appusage_name.add(name);


                Float duration = allRows2.getFloat(allRows2.getColumnIndex(APP_DURATION));
                sendJSON.appusage_duration.add(duration);

                allRows2.moveToNext();
            }
        }

//        if (allRows2.moveToFirst() ){
//            String[] columnNames = allRows2.getColumnNames();
//            do {
//                for (String name: columnNames) {
//                    switch (name){
//                        case APP_NAME:
//                           //sendJSON.appusage_name.add(allRows2.getString(allRows2.getColumnIndex(name)));
//
//                            break;
//                        case APP_DURATION:
//                            sendJSON.appusage_duration.add(allRows2.getFloat(allRows2.getColumnIndex(name)));
//                            break;
//                    }
//                }
//
//            } while (allRows2.moveToNext());
//
//        }

        return sendJSON;
    }

    public void clearDatabase() {
        deleteAll(TABLE_NAME1);
        deleteAll(TABLE_NAME2);
    }

    public void deleteAll(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ tableName);
        db.close();
    }

    public Boolean isTableEmpty(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + tableName;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount == 0;
    }

    public Boolean isDatabaseEmpty() {
        return isTableEmpty(TABLE_NAME1) || isTableEmpty(TABLE_NAME2);
    }
}
