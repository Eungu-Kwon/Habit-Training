package com.eungu.habittraining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import com.unity3d.player.*;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private DBHelper m_helper;
    private SQLiteDatabase db;
    private Cursor c;
    private UnityPlayer m_UnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
                db = m_helper.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS training;");
                db.execSQL("DROP TABLE IF EXISTS grown;");
            }
        });

        loadUnityView();
        MakeGoalList();
    }

    private void MakeGoalList(){
        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getReadableDatabase();
        c = db.rawQuery("SELECT * FROM training;", null);
        c.moveToFirst();
        final ListView listview = (ListView)findViewById(R.id.list);
        final ArrayList<ListViewItem> items = new ArrayList<>();
        do{
            ListViewItem i = new ListViewItem(R.drawable.checkbox_blank, c.getString(1));
            i.setDone(c.getInt(2));
            LoadIcon(i);
            items.add(i);
        }while (c.moveToNext());
        db.close();
        final ListViewAdapter adapter = new ListViewAdapter(this, R.layout.cell_title_linear, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.get(position).reverseDone();
                db = m_helper.getWritableDatabase();
                db.execSQL(String.format("UPDATE training SET done = %d WHERE title = '%s'", items.get(position).isDone(), items.get(position).getName()));
                db.close();
                LoadIcon(items.get(position));
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void LoadIcon(ListViewItem i){
        if(i != null){
            if(i.isDone() == 1){
                i.setIcon(R.drawable.checkbox_checked);
            }
            else {
                i.setIcon(R.drawable.checkbox_blank);
            }
        }
    }

    private void loadUnityView(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);

        FrameLayout frame = (FrameLayout)findViewById(R.id.unityLayout);
        m_UnityPlayer = new UnityPlayer(this);
        m_UnityPlayer.init(m_UnityPlayer.getSettings().getInt("gles_mode", 1), false);
        LayoutParams lp = new LayoutParams (LayoutParams.MATCH_PARENT, dm.heightPixels * 2 / 5);
        frame.addView(m_UnityPlayer.getView(), 0, lp);
        m_UnityPlayer.requestFocus();
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        m_UnityPlayer.destroy();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        m_UnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        m_UnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        super.onStart();
        m_UnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        m_UnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        m_UnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            m_UnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        m_UnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        m_UnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return m_UnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return m_UnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return m_UnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return m_UnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return m_UnityPlayer.injectEvent(event); }
}