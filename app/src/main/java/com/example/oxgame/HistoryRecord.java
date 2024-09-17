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

//        // 示例：插入或更新分数
//        dbHelper.updateTime("简单", 7);
//        dbHelper.updateTime("中等", 150);
//        dbHelper.updateTime("困难", 200);

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
                        simpleScoreTextView.setText(String.valueOf(time));
                        break;
                    case "中等":
                        mediumScoreTextView.setText(String.valueOf(time));
                        break;
                    case "困难":
                        difficultyScoreTextView.setText(String.valueOf(time));
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
        dbHelper.close();
    }
}
