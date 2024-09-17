package com.example.oxgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectDifficulty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_difficutly);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.select_difficulty), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtonListeners();
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

    //返回上一个页面
    private void setupButtonListeners(){
        ImageView returnPre = findViewById(R.id.returnpre_image);
        returnPre.setOnClickListener(view -> {
            finish();
        });

        Button easyButton = findViewById(R.id.simple_game);
        easyButton.setOnClickListener(view -> {
            Intent chessBoardIntent = new Intent(this, ChessBoardActivity.class);
            chessBoardIntent.putExtra("Row", 4);
            startActivity(chessBoardIntent);
        });

        Button mediumButton = findViewById(R.id.medium_game);
        mediumButton.setOnClickListener(view -> {
            Intent chessBoardIntent = new Intent(this, ChessBoardActivity.class);
            chessBoardIntent.putExtra("Row", 6);
            startActivity(chessBoardIntent);
        });

        Button hardButton = findViewById(R.id.difficulty_game);
        hardButton.setOnClickListener(view -> {
            Intent chessBoardIntent = new Intent(this, ChessBoardActivity.class);
            chessBoardIntent.putExtra("Row", 8);
            startActivity(chessBoardIntent);
        });
    }

}