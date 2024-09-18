package com.example.oxgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistoryRecord extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextView simpleScoreTextView, mediumScoreTextView, difficultyScoreTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.historical_score);

        // 获取DBHelper实例
        dbHelper = DBHelper.getInstance(this);

        // 初始化视图组件
        initView();

        // 显示当前的分数
        displayTimes();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historical_score), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtonListeners();
    }

    private void initView() {
        simpleScoreTextView = findViewById(R.id.simple_time);
        mediumScoreTextView = findViewById(R.id.medium_time);
        difficultyScoreTextView = findViewById(R.id.difficulty_time);
    }

    private void displayTimes() {
        Cursor cursor = dbHelper.getTimes();
        if (cursor.moveToFirst()) {
            do {
                String difficulty = cursor.getString(cursor.getColumnIndexOrThrow("difficulty"));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow("time"));

                switch (difficulty) {
                    case "简单":
                        simpleScoreTextView.setText(processTimer(time));
                        break;
                    case "中等":
                        mediumScoreTextView.setText(processTimer(time));
                        break;
                    case "困难":
                        difficultyScoreTextView.setText(processTimer(time));
                        break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setupButtonListeners() {
        ImageView returnpre = findViewById(R.id.returnpre_image);
        returnpre.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dbHelper.close();
    }

    private String processTimer(int currentSeconds) {
        int minute = (currentSeconds / 60) % 60;
        int second = currentSeconds % 60;

        String minuteString;
        if (minute<10)
        {
            minuteString = "0" + minute;
        }
        else{
            minuteString = "" + minute;
        }
        String secondString;
        if (second<10)
        {
            secondString = "0" + second;
        }
        else{
            secondString = "" + second;
        }
        String timeString = minuteString + ":" + secondString;
        return timeString;
    }
}
