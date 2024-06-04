package com.example.candycrush;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;

public class First extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Intent intent = new Intent(this, MediaPlayerService.class);
        startService(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(First.this, Menu.class));
                finish();
            }
        },5000);
    }

}