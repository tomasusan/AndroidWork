package com.example.oxgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WinActivity extends AppCompatActivity {

    private String time ;
    private int difficulty;
    private int second;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_win);
        DBHelper dbHelper = DBHelper.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView timeView = findViewById(R.id.timeResult);
        Intent intent = getIntent();
        time = intent.getStringExtra("Time");
        difficulty = intent.getIntExtra("Difficulty", -1);
        second = intent.getIntExtra("Second", -1);
        timeView.setText(time);

        switch (difficulty){
            case 4:
                dbHelper.updateTime("简单",second);
                break;
            case 6:
                dbHelper.updateTime("中等",second);
                break;
            case 8:
                dbHelper.updateTime("困难",second);
                break;
        }

        ImageView home = findViewById(R.id.home);
        ImageView setting = findViewById(R.id.setting);

        home.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, StartMenu.class);
            startActivity(intent1);
            finish();
        });

        setting.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, SettingActivity.class);
            startActivity(intent2);
        });

        // 禁用返回按钮
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 什么都不做，禁用返回键
            }
        });

    }
}