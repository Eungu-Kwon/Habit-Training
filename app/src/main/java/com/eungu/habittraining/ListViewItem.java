package com.eungu.habittraining;

public class ListViewItem {
    private int icon;
    private String name;

    public int getIcon(){return icon;}
    public String getName(){return name;}

    public ListViewItem(int icon,String name){
        this.icon=icon;
        this.name=name;
    }
}
