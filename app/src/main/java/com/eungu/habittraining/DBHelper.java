package com.eungu.habittraining;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

//todolist (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT), startdate TEXT
//training (today TEXT, title TEXT, done INTEGER) done 1, not done -1
//grown (_id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, days INTEGER, rested INTEGER) , phase INTEGER
//debug (day INTEGER)
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS training;");
        db.execSQL("DROP TABLE IF EXISTS todolist;");
        db.execSQL("DROP TABLE IF EXISTS grown;");
        db.execSQL("DROP TABLE IF EXISTS debug;");
        onCreate(db);
    }
}
