package com.eungu.habittraining;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ListViewItem> data;
    private int layout;

    public ListViewAdapter(Context context, int layout, ArrayList<ListViewItem> data) {
        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        ListViewItem listviewitem=data.get(position);
        ImageView icon=(ImageView)convertView.findViewById(R.id.imageView);
        icon.setImageResource(listviewitem.getIcon());
        TextView name=(TextView)convertView.findViewById(R.id.textView);
        name.setText(listviewitem.getName());
        return convertView;
    }
}
class ListViewInSettingAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ListViewItemInSetting> data;
    private int layout;
    Button b;
    private View.OnClickListener listener;

    public ListViewInSettingAdapter(Context context, int layout, ArrayList<ListViewItemInSetting> data) {

        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        ListViewItemInSetting listviewitem=data.get(position);
        b = (Button)convertView.findViewById(R.id.erase_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                refreashList();
            }
        });
        data.indexOf(listviewitem);
        TextView name=(TextView)convertView.findViewById(R.id.goal_text_in_setting);
        name.setText(listviewitem.getName());
        return convertView;
    }

    public void setButtonClickListener(View.OnClickListener defaultRequestBtnClickListener){
        this.listener = defaultRequestBtnClickListener;
    }

    public void refreashList(){
        this.notifyDataSetChanged();
    }
}

