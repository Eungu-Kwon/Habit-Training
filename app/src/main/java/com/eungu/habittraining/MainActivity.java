package com.eungu.habittraining;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends Activity {

    private DBHelper m_helper;
    private SQLiteDatabase db;
    private Cursor c;
    private int doneThis = 0;
    private UnityPlayer m_UnityPlayer;

    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeToday();
        loadUnityView();
        MakeTabs();
        MakeGoalList();
    }

    private void InitializeToday(){
        SimpleDateFormat mDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Date today = new Date();
        String now = mDate.format(today);
        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getReadableDatabase();
        String sql = String.format("SELECT * FROM training WHERE today LIKE '%s';", now);
        c = db.rawQuery(sql, null);

        if(c.getCount() == 0){
            sql = String.format("SELECT * FROM todolist");
            Cursor listC = db.rawQuery(sql, null);
            c.moveToFirst();
            listC.moveToFirst();
            for(int i = 0; i < c.getCount(); i++){
                String itemName = listC.getString(1);
                sql = String.format("INSERT INTO training VALUES ('%s', '%s', -1);", now, itemName);
                db.execSQL(sql);
                listC.moveToNext();
            }

            Toast.makeText(getApplicationContext(), "초기화는 하루에 한번!", Toast.LENGTH_LONG).show();
        }
    }

    public void MakeAlert(int menu, final ArrayList<ListViewItem> items, final int idx, final ListViewAdapter adapter){
        alertDialogBuilder = new AlertDialog.Builder(this);
        String title = null, message = null, pb = null, nb = null;
        DialogInterface.OnClickListener listenerPositive = null, listenerNegative = null;
        switch (menu){
            case 1:
                title = "확인";
                message = "데이터를 삭제하시겠습니까?\n어플이 자동으로 종료됩니다.";
                pb = "삭제";
                nb = "취소";
                listenerPositive = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
                        db = m_helper.getWritableDatabase();
                        db.execSQL("DROP TABLE IF EXISTS training;");
                        db.execSQL("DROP TABLE IF EXISTS grown;");
                        MainActivity.this.finish();
                    }
                };
                listenerNegative = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                };
                break;
            case 2:
                title = "확인";
                message = "이 목표를 달성했나요?\n양심에 따라 정직하게 확인해주세요!\n한번 확인하면 취소할 수 없습니다.";
                pb = "확인";
                nb = "취소";
                listenerPositive = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items.get(idx).reverseDone();
                        db = m_helper.getWritableDatabase();
                        db.execSQL(String.format("UPDATE training SET done = %d WHERE title = '%s'", items.get(idx).isDone(), items.get(idx).getName()));
                        db.close();
                        LoadIcon(items.get(idx));
                        adapter.notifyDataSetChanged();

                        for(ListViewItem item : items){if(item.isDone() == -1) return; }

                        IWAE();
                    }
                };
                listenerNegative = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                };
                break;
        }
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(pb, listenerPositive);
        alertDialogBuilder.setNegativeButton(nb, listenerNegative);
    }

    public void IWAE(){
        KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(10f, 15f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 2000L);
        Toast.makeText(getApplicationContext(), "목표를 달성하였습니다.\n수고하셨습니다!", Toast.LENGTH_LONG).show();
        return;
    }

    private void MakeTabs(){
        TabHost tabhost = (TabHost)findViewById(R.id.tabhost);
        tabhost.setup();

        TabHost.TabSpec tab1 = tabhost.newTabSpec("Tab Spac 1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("일반");
        tabhost.addTab(tab1);

        TabHost.TabSpec tab2 = tabhost.newTabSpec("Tab Spac 2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("설정");
        tabhost.addTab(tab2);

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeAlert(1, null, 0, null);
                AlertDialog ad = alertDialogBuilder.create();
                ad.show();
            }
        });
    }

    private void MakeGoalList(){
        SimpleDateFormat mDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Date today = new Date();
        String now = mDate.format(today);
        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getReadableDatabase();
        String sql = String.format("SELECT * FROM training WHERE today LIKE '%s';", now);
        c = db.rawQuery(sql, null);
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
                if(items.get(position).isDone() == 1) return;
                MakeAlert(2, items, position, adapter);
                AlertDialog ad = alertDialogBuilder.create();
                ad.show();
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