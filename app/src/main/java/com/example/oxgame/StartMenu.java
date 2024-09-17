package com.example.oxgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start_menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkAndShowLoggedInUser();
        setupButtonListeners();
        setupMusic();
    }

    private void setupMusic(){
        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putExtra("Action", 0);
        startService(musicIntent);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putExtra("Action", 1);
        musicIntent.putExtra("ResourceID", R.raw.nichijou);
        startService(musicIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 恢复音乐播放
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("Action", 2); // Change
        intent.putExtra("ResourceID", R.raw.nichijou);
        startService(intent);
    }

    //设置开始界面的按钮监听
    private void setupButtonListeners(){
        //开始游戏按钮
        Button startGameButton = findViewById(R.id.start_game);
        startGameButton.setOnClickListener(view -> {
            Intent selectDifficultyIntent = new Intent(this, SelectDifficulty.class);
            startActivity(selectDifficultyIntent);
        });

        //历史记录按钮
        Button historyRecordButton = findViewById(R.id.historical_record);
        historyRecordButton.setOnClickListener(view -> {
            Intent historyRecordIntent = new Intent(this, HistoryRecord.class);
            startActivity(historyRecordIntent);
        });

        //设置按钮
        Button settingButton = findViewById(R.id.setting);
        settingButton.setOnClickListener(view -> {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
        });

        //左上角的用户登陆按钮
        ImageView userImageButton = findViewById(R.id.user_image);
        userImageButton.setOnClickListener(view -> {
            showUserDialog();
        });
    }

    //登陆按钮的浮窗
    private void showUserDialog(){
        AlertDialog.Builder userDialog = new AlertDialog.Builder(this);

        // 加载自定义布局
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.user_login_dialog, null);
        userDialog.setView(dialogView);

        // 获取自定义布局中的视图
        EditText userNameEditText = dialogView.findViewById(R.id.user_name_edit);
        Button confirmLoginButton = dialogView.findViewById(R.id.confirm_login_button);
        Button cancelLoginButton = dialogView.findViewById(R.id.cancel_login_button);

        // 从SharedPreferences读取已保存的用户名
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("username", "");

        // 如果有保存的用户名，则预先填入EditText
        if (!savedUserName.isEmpty()) {
            userNameEditText.setText(savedUserName);
            userNameEditText.setSelection(userNameEditText.getText().length()); // 移动光标到末尾
        }

        // 创建弹窗实例
        AlertDialog dialog = userDialog.create();

        // 设置确认按钮的点击事件
        confirmLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUserName = userNameEditText.getText().toString();
                if (!enteredUserName.isEmpty()) {
                    // 处理输入的用户名
                    saveUserName(enteredUserName);
                    dialog.dismiss(); // 关闭弹窗
                } else {
                    userNameEditText.setError("用户名不能为空");
                }
            }
        });

        // 设置取消按钮的点击事件
        cancelLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // 直接关闭弹窗
            }
        });

        dialog.show();

        //设置弹窗大小
        //dialog.getWindow().setLayout(1250, ViewGroup.LayoutParams.WRAP_CONTENT);

        /// 获取屏幕宽度
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
        Rect bounds = windowMetrics.getBounds();
        int screenWidth = bounds.width();  // 获取屏幕宽度
        int screenHeight = bounds.height();

// 设置弹窗宽度为屏幕宽度的80%
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = (int) (screenWidth * 0.4); // 设置为屏幕宽度的80%
            //layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
            layoutParams.height = (int) (screenHeight * 0.5);
            window.setAttributes(layoutParams);
        }
    }

    //保存用户名
    public void saveUserName(String userName) {
        // 获取SharedPreferences实例
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 将用户名保存到SharedPreferences
        editor.putString("username", userName);
        editor.apply(); // 或 editor.commit(); 提交保存

        // 提示保存成功（可选）
        Toast toast = Toast.makeText(this, "用户名已保存", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 100);  // 在底部显示，并向上偏移200像素
        toast.show();
    }

    //启动游戏后提示已登录用户
    private void checkAndShowLoggedInUser() {
        // 获取SharedPreferences实例
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // 获取保存的用户名
        String savedUserName = sharedPreferences.getString("username", null);

        // 如果用户名不为空，显示Toast提示
        if (savedUserName != null) {
            String message = "当前已登录用户：" + savedUserName;
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 100);  // 在底部显示，并向上偏移200像素
            toast.show();
        }
    }
}