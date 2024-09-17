package com.example.oxgame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    public int action;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.nichijou); // `music` 是 `res/raw` 中的音乐文件名，不带扩展名
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onStart", "Start Called");
        int inputAction = intent.getIntExtra("Action", -1);
        Log.i("onStart", "Action: " + inputAction);
        switch (inputAction) {
            case 0:
                mediaPlayer.start();
                break;
            case 1:
                Log.i("Music", "Now Changing");
                int rID = intent.getIntExtra("ResourceID", -1);
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(this, rID);
                mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                break;
        }


        return START_STICKY; // 保持服务运行
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // 释放资源
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}