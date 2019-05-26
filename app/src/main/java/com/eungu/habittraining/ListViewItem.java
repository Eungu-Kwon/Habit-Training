package com.eungu.habittraining;

import android.view.View;

public class ListViewItem {
    private int icon;
    private String name;

    public int getIcon(){return icon;}
    public void setIcon(int icon){this.icon = icon;}
    public String getName(){return name;}

    public ListViewItem(int icon,String name){
        this.icon=icon;
        this.name=name;
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