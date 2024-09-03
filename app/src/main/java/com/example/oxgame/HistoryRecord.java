package com.example.oxgame;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistoryRecord extends AppCompatActivity {

    private SQLiteDatabase db;
    private TextView simpleScoreTextView, mediumScoreTextView, difficultyScoreTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.historical_score);

        // 从SharedPreferences中获取用户名
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // 打印日志查看当前存储的用户名
//        if (username != null) {
//            Log.d("SharedPreferences", "Current stored username: " + username);
//        } else {
//            Log.d("SharedPreferences", "No username found in SharedPreferences.");
//        }

        // 检查获取到的用户名是否为null或空字符串
        if (username == null || username.isEmpty()) {
            // 如果用户名为null或空，使用默认值
            username = "default_user";
        }

        // 使用获取到的用户名创建或打开数据库
        db = openOrCreateDatabase(username + ".db", MODE_PRIVATE, null);
        //用于测试数据库是否成功创建或打开
//        if (db != null) {
//            Log.d("Database", "Successfully created or opened the database:" + username + ".db");
//        } else {
//            Log.e("Database", "Unable to create or open database:" + username + ".db");
//        }
        db.execSQL("CREATE TABLE IF NOT EXISTS times (difficulty TEXT PRIMARY KEY, time INTEGER)");

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

        // 示例：插入或更新分数
        updateTime("简单", 200);
        updateTime("中等", 150);
        updateTime("困难", 200);
    }

    // 初始化视图组件
    private void initView() {
        simpleScoreTextView = findViewById(R.id.simple_time);
        mediumScoreTextView = findViewById(R.id.medium_time);
        difficultyScoreTextView = findViewById(R.id.difficulty_time);
    }

    // 显示当前分数
    private void displayTimes() {
        Cursor cursor = db.rawQuery("SELECT * FROM times", null);
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

    // 返回上一个页面
    private void setupButtonListeners() {
        ImageView returnpre = findViewById(R.id.returnpre_image);
        returnpre.setOnClickListener(view -> {
            finish();
        });
    }

    // 更新分数方法
    private void updateTime(String difficulty, int time) {
        // 检查该难度级别的分数是否已存在
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE difficulty = ?", new String[]{difficulty});
        if (cursor.moveToFirst()) {
            // 时间存在且新记录时间更短
            int currentTime = cursor.getInt(cursor.getColumnIndexOrThrow("time"));
            if (time < currentTime) {
                ContentValues values = new ContentValues();
                values.put("time", time);
                db.update("times", values, "difficulty = ?", new String[]{difficulty});
            }
        } else {
            // 分数不存在，插入新纪录
            ContentValues values = new ContentValues();
            values.put("difficulty", difficulty);
            values.put("time", time);
            db.insert("times", null, values);
        }
        cursor.close();

        // 更新界面显示的分数
        displayTimes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();  // 关闭数据库连接
        }
    }
}
