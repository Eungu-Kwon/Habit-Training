package com.eungu.habittraining;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DBHelper m_helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_helper = new DBHelper(getApplicationContext(), "training.db", null, 1);

        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getWritableDatabase();
                db.execSQL("INSERT INTO training VALUES (NULL, 'whattodo', 0)");
                Toast.makeText(MainActivity.this, "Add Record", Toast.LENGTH_LONG).show();
                db.close();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = m_helper.getWritableDatabase();
                db.execSQL("DROP TABLE training");
                db.close();
                Toast.makeText(MainActivity.this, "Delete Table", Toast.LENGTH_LONG).show();
            }
        });
    }
}
