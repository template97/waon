package com.example.yuju.waon02;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context, int version) {
        super(context, "WAonData0.db", null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table SETTING ( _id integer primary key autoincrement, LOCATION integer, RAIN integer, SNOW integer);");
        db.execSQL("insert into SETTING values (null, 8, 0, 0)");
        db.execSQL("create table ALARM ( _id integer primary key autoincrement, NAME text, LABEL text, HOUR integer, MIN integer, RAIN integer, SNOW integer, VIBRATE integer, VALID integer);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*if(oldVersion <= 1){
            db.execSQL("alter table SETTING add column HELP integer;");
            db.execSQL("update SETTING set HELP = 1;");
        }
        if(oldVersion <=2 ){
            db.execSQL("alter table SETTING add column ROMAN integer;");
            db.execSQL("update SETTING set ROMAN = 0;");
        }
        if(oldVersion <= 3){
            db.execSQL("create table FUNCTION ( _id integer primary key autoincrement, " +
                    "NAME text, NUMBER integer, CONTEXT text);");

        }*/
    }

}
