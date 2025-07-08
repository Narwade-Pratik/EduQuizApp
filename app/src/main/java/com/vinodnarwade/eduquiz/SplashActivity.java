package com.vinodnarwade.eduquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView ivSplashLogo;
    LinearLayout llSplash;
    TextView tvSplashTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ivSplashLogo = findViewById(R.id.ivsplashlogo);
        llSplash = findViewById(R.id.llsplash);
        tvSplashTitle = findViewById(R.id.tvsplashtitle);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);
    }
}