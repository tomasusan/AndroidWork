package com.example.oxgame;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.setting_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_setting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtonListeners();
        setupVolumeControl();
    }

    //返回上一个页面
    private void setupButtonListeners() {
        ImageView returnpre = findViewById(R.id.returnpre_image);
        returnpre.setOnClickListener(view -> {
            finish();
        });

        Button exitButton = findViewById(R.id.exit_game);
        exitButton.setOnClickListener(view -> {
            finishAffinity();
        });
    }

    // 设置 SeekBar 控制音量
    private void setupVolumeControl() {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        SeekBar volumeSeekBar = findViewById(R.id.sb_volume);

        // 获取应用内最大和当前音量
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 仅调整应用内的音量
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 用户开始拖动 SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 用户停止拖动 SeekBar
            }
        });
    }
}
