package com.eungu.habittraining;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import com.unity3d.player.*;

public class MainActivity extends Activity {
    private DBHelper m_helper;
    private SQLiteDatabase db;
    private Cursor c;
    private UnityPlayer m_UnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);

        Button b = (Button)findViewById(R.id.button);
        Button b1 = (Button)findViewById(R.id.button1);
        Button b2 = (Button)findViewById(R.id.button2);
        Button b3 = (Button)findViewById(R.id.button3);

        FrameLayout frame = (FrameLayout)findViewById(R.id.frameLayout);
        m_UnityPlayer = new UnityPlayer(this);
        //m_UnityPlayer.init(m_UnityPlayer.getSettings().getInt("gles_mode", 1), false);
        //LayoutParams lp = new LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        frame.addView(m_UnityPlayer.getView(), 0);
        //m_UnityPlayer.requestFocus();

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getWritableDatabase();
                db.execSQL("CREATE TABLE IF NOT EXISTS training (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, done INTEGER);");
                Toast.makeText(MainActivity.this, "Add Table", Toast.LENGTH_LONG).show();
                db.close();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getWritableDatabase();
                db.execSQL("INSERT INTO training VALUES (NULL, 'hello', 0);");
                Toast.makeText(MainActivity.this, "Add Record", Toast.LENGTH_LONG).show();
                db.close();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getReadableDatabase();
                c = db.rawQuery("SELECT * FROM training;", null);
                c.moveToFirst();
                db.close();
                Toast.makeText(MainActivity.this, c.getString(0), Toast.LENGTH_LONG).show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getReadableDatabase();
                db.execSQL("DROP TABLE IF EXISTS training;");
                db.close();
            }
        });
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