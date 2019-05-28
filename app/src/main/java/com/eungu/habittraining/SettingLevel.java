package com.eungu.habittraining;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingLevel extends Activity {
    private DBHelper m_helper;
    private SQLiteDatabase db;
    private int level;

    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_plants);
        level = -1;



        Button startButton = (Button)findViewById(R.id.start_button_in_level);
        final TextView textView = (TextView)findViewById(R.id.info_text);

        ImageView level0 = (ImageView)findViewById(R.id.level_0);
        ImageView level1 = (ImageView)findViewById(R.id.level_1);
        ImageView level5 = (ImageView)findViewById(R.id.level_5);

        View.OnClickListener clickImage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.level_0:
                        level = 0;
                        textView.setText("일주일 동안 목표를 실천합니다.\n(7일)");
                        break;
                    case R.id.level_1:
                        level = 1;
                        textView.setText("이주일 동안 목표를 실천합니다.\n(14일)");
                        break;
                    case R.id.level_5:
                        level = 5;
                        textView.setText("한달간 목표를 실천합니다.\n(30일)");
                        break;
                }
            }
        };

        level0.setOnClickListener(clickImage);
        level0.setImageResource(R.drawable.num1);
        level1.setOnClickListener(clickImage);
        level1.setImageResource(R.drawable.num2);
        level5.setOnClickListener(clickImage);
        level5.setImageResource(R.drawable.num3);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(level == -1){
                    Toast.makeText(getApplicationContext(), "난이도를 선택해 주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                MakeAlert();
                AlertDialog ad = alertDialogBuilder.create();
                ad.show();
            }
        });
    }

    public void StartProgram(){
        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS grown;");
        db.execSQL("CREATE TABLE grown (_id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, days INTEGER, rested INTEGER);");
        db.execSQL(String.format("INSERT INTO grown VALUES (NULL, %d, 0, 0);", level));
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    public void MakeAlert(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("확인");
        alertDialogBuilder.setMessage("시작하시겠습니까?\n한번 시작하면 변경할 수 없습니다!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("시작", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StartProgram();
                    }
                });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }
}
