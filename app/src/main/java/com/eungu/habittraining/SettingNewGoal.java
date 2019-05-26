package com.eungu.habittraining;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingNewGoal extends Activity {
    private DBHelper m_helper;
    private SQLiteDatabase db;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_goal);

        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);
        db = m_helper.getWritableDatabase();

        Button addButton = (Button)findViewById(R.id.add_goal_button);
        Button startButton = (Button)findViewById(R.id.start_button);
        final EditText inputField = (EditText)findViewById(R.id.text_input);

        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        ListView listView = (ListView)findViewById(R.id.list_in_setting);
        final ArrayList<ListViewItemInSetting> list = new ArrayList<>();
        final ListViewInSettingAdapter adapter = new ListViewInSettingAdapter(getApplicationContext(), R.layout.cell_setting_goal, list);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(inputField.getWindowToken(), 0);
                list.add(new ListViewItemInSetting(inputField.getText().toString()));
                inputField.setText("");
                adapter.notifyDataSetChanged();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ListViewItemInSetting item : list){
                    
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });
    }
}