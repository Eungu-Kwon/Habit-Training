package com.eungu.habittraining;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SettingNewGoal extends Activity {
    private DBHelper m_helper;
    private SQLiteDatabase db;

    View.OnClickListener listener;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_goal);

        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);

        addButton = (Button)findViewById(R.id.add_goal_button);
        final TextView text_info = (TextView)findViewById(R.id.text_in_goal);
        Button startButton = (Button)findViewById(R.id.start_button);
        final EditText inputField = (EditText)findViewById(R.id.text_input);

        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        ListView listView = (ListView)findViewById(R.id.list_in_setting);
        final ArrayList<ListViewItemInSetting> list = new ArrayList<>();
        final ListViewInSettingAdapter adapter = new ListViewInSettingAdapter(getApplicationContext(), R.layout.cell_setting_goal, list);
        listView.setAdapter(adapter);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputField.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "목표를 입력해주세요!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(list.size() == 0) text_info.setVisibility(View.GONE);
                if(v.getId() == R.id.add_goal_button){
                    imm.hideSoftInputFromWindow(inputField.getWindowToken(), 0);
                }
                list.add(new ListViewItemInSetting(inputField.getText().toString()));
                inputField.setText("");

                adapter.notifyDataSetChanged();
            }
        };

        addButton.setOnClickListener(listener);

        inputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        if(event.getAction() == KeyEvent.ACTION_DOWN) addButton.performClick();
                        break;
                }
                return false;
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size() == 0) {
                    Toast.makeText(getApplicationContext(), "목표를 설정해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat mDate = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                Date today = new Date();
                String now = mDate.format(today);
                db = m_helper.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS training;");
                db.execSQL("DROP TABLE IF EXISTS todolist;");
                db.execSQL("CREATE TABLE training (today TEXT, title TEXT, done INTEGER);");
                db.execSQL("CREATE TABLE todolist (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, startdate TEXT);");
                for(int i = 0; i < list.size(); i++){
                    String sql = String.format("INSERT INTO training VALUES ('%s', '%s', -1);", now, list.get(i).getName());
                    db.execSQL(sql);
                    sql = String.format("INSERT INTO todolist VALUES (NULL, '%s', '%s');", list.get(i).getName(), now);
                    db.execSQL(sql);
                }
                db.close();
                Intent intent = new Intent(getApplicationContext(), SettingLevel.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_ENTER){
//            addButton.performClick();
//        }
//        return super.onKeyUp(keyCode, event);
//    }
}
