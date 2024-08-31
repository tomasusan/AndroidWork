package com.example.oxgame;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

    private TextView tvSound;
    private TextView tvVolume;
    private SeekBar sbSound;
    private SeekBar sbVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        initViews();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "onCreate: maxVolume = " + maxVolume);
        sbSound.setMax(maxVolume);
        //获取当前音量
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "onCreate: currentVolume = " + currentVolume);
        sbSound.setProgress(currentVolume);

        sbSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    //设置系统音量
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    seekBar.setProgress(currentVolume);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initViews() {
        tvSound = findViewById(R.id.tv_sound);
        tvVolume = findViewById(R.id.tv_volume);
        sbSound = findViewById(R.id.sb_volume);
        sbVolume = findViewById(R.id.sb_volume);
    }

}