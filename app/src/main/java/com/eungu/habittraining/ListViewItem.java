package com.eungu.habittraining;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListViewItem {
    private int icon;
    private String name;
    private int done;

    public int isDone(){return done;}
    public int getIcon(){return icon;}
    public void setIcon(int icon){this.icon = icon;}
    public void setDone(int i){this.done = i;}
    public void reverseDone(){this.done *= -1;}
    public String getName(){return name;}

    public ListViewItem(int icon,String name){
        this.icon=icon;
        this.name=name;
        this.done=-1;
    }
}

class ListViewItemInSetting {
    private String name;
    private View.OnClickListener listener;
    public String getName(){return name;}
    public ListViewItemInSetting(String name){
        this.name=name;
    }
}

class FoldingCellItem {
    private String date;
    private String listName;
    private boolean done;

    public FoldingCellItem(String date, String listName, boolean done) {
        this.date = date;
        this.listName = listName;
        this.done = done;
    }

    public boolean isDone(SQLiteDatabase db){
        Cursor c;

        SimpleDateFormat mDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat showDate = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        Date now = showDate.parse(date, new ParsePosition(0));

        String sql = String.format("SELECT * FROM training WHERE today LIKE '%s';", mDate.format(now));
        c = db.rawQuery(sql, null);
        c.moveToFirst();

        boolean checkDone = true;
        do{
            if(c.getInt(2) == -1) checkDone = false;
        }while(c.moveToNext());
        setDone(checkDone);
        return checkDone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}