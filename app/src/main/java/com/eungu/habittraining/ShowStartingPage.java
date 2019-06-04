package com.eungu.habittraining;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStartingPage extends Activity {
    private Animation fadein, fadeout, startPhase;
    private DBHelper m_helper;
    private SQLiteDatabase db;
    private Cursor c;
    private int phase;
    private final int DELAYTIME = 100;
    private boolean canPress;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getReadableDatabase();
        c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='grown';", null);
        Cursor c1 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='todolist';", null);
        Cursor c2 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='training';", null);
        Cursor c3 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='debug';", null);
        if(c.getCount() > 0 && c1.getCount() > 0 && c2.getCount() > 0 && c3.getCount() > 0){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            db.close();
            finish();
            return;
        }

        db.close();
        setContentView(R.layout.starting_page);
        final TextView textView = (TextView)findViewById(R.id.newtext);

        Animation.AnimationListener listener1 = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switch (phase){
                    case 2:
                        textView.startAnimation(fadein);
                        textView.setText("본 어플은\n심리학의 이해수업을 듣고\n강의 내용을 기초로 제작되었습니다.");
                        textView.setTextSize(20);
                        phase+=1;
                        break;
                    case 4:
                        textView.startAnimation(fadein);
                        textView.setText("목표를 설정하고 달성하면,\n이차 강화물을 얻어 습관이 강화되고\n특정 행동이 조성됩니다.\n(조작적 조건형성)");
                        textView.setTextSize(20);
                        phase+=1;
                        break;
                    case 6:
                        textView.startAnimation(fadein);
                        textView.setText("이제\n목표를 설정하세요.");
                        textView.setTextSize(40);
                        phase+=1;
                        break;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation.AnimationListener listener2 = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                canPress = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        phase = 1;
        canPress = true;
        startPhase = new AlphaAnimation(1.0f, 0.001f);
        startPhase.setDuration(0);
        startPhase.setFillAfter(true);

        fadein = new AlphaAnimation(0.001f, 1.0f);
        fadein.setDuration(DELAYTIME);
        fadein.setFillAfter(true);
        fadein.setAnimationListener(listener2);

        fadeout = new AlphaAnimation(1.0f, 0.001f);
        fadeout.setDuration(DELAYTIME);
        fadeout.setFillAfter(true);
        fadeout.setAnimationListener(listener1);

        textView.startAnimation(startPhase);
        textView.startAnimation(fadein);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canPress){
                    switch (phase){
                        case 1:
                        case 3:
                        case 5:
                            textView.startAnimation(fadeout);
                            canPress = false;
                            phase += 1;
                            break;
                        case 7:
                            Intent intent = new Intent(getApplicationContext(), SettingNewGoal.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            finish();
                            break;
                    }
                }
            }
        });
    }
}
