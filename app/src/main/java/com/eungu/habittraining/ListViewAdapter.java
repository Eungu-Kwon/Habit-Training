package com.eungu.habittraining;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ramotion.foldingcell.FoldingCell;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

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

class FoldingCellListAdapter extends ArrayAdapter<FoldingCellItem>{
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    Context context;
    private int failCount;
    public FoldingCellListAdapter(Context context, int resource, List<FoldingCellItem> objects) {
        super(context, resource, objects);
        this.context = context;
        failCount = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoldingCellItem item = getItem(position);

        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;

        DBHelper m_helper = new DBHelper(context, "training.db", null, 1);;
        SQLiteDatabase db = m_helper.getReadableDatabase();;
        Cursor c;

        SimpleDateFormat mDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat showDate = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        Date now = showDate.parse(item.getDate(), new ParsePosition(0));
        String sql = String.format("SELECT * FROM training WHERE today LIKE '%s';", mDate.format(now));
        c = db.rawQuery(sql, null);
        c.moveToFirst();

        if(cell == null){
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            viewHolder.dateInTitle = (TextView)cell.findViewById(R.id.dateInTitle);
            viewHolder.dateInContent = (TextView)cell.findViewById(R.id.dateContent);
            viewHolder.contentList = (ListView)cell.findViewById(R.id.list_in_content);
            viewHolder.doIcon = (ImageView)cell.findViewById(R.id.imageInTitle);
            cell.setTag(viewHolder);
        }
        else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (null == item)
            return cell;

        viewHolder.dateInTitle.setText(item.getDate());
        viewHolder.dateInContent.setText(item.getDate());
        boolean checkDone = true;
        do{
            if(c.getInt(2) == -1) checkDone = false;
        }while(c.moveToNext());
        if(checkDone) viewHolder.doIcon.setImageResource(R.drawable.checkbox_checked);
        else {
            failCount += 0;
            viewHolder.doIcon.setImageResource(R.drawable.checkbox_blank);
        }

        final ArrayList<ListViewItem> items = new ArrayList<>();

        c.moveToFirst();

        do{
            ListViewItem item1 = new ListViewItem(R.drawable.checkbox_blank, c.getString(1));
            if(c.getInt(2) == 1) item1.setIcon(R.drawable.checkbox_checked);
            else item1.setIcon(R.drawable.checkbox_blank);
            items.add(item1);
        }while(c.moveToNext());

        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) viewHolder.contentList.getLayoutParams();
        int h = ((c.getCount() * 64) + 37);
        if(h < 160) h = 180;
        lp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, h, context.getResources().getDisplayMetrics());

        viewHolder.contentList.setLayoutParams(lp);
        viewHolder.contentList.requestLayout();

        final ListViewAdapter adapter = new ListViewAdapter(context, R.layout.cell_title_linear, items);
        viewHolder.contentList.setAdapter(adapter);

        return cell;
    }

    public int getFailCount(){return failCount;}
    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    private static class ViewHolder{
        TextView dateInTitle;
        TextView dateInContent;
        ListView contentList;
        ImageView doIcon;
    }
}
