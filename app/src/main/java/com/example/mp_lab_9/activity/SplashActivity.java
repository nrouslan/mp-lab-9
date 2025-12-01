package com.example.mp_lab_9.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mp_lab_9.R;
import com.example.mp_lab_9.util.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 секунды
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        new Handler().postDelayed(() -> checkAuthentication(), SPLASH_DELAY);
    }

    private void checkAuthentication() {
        if (sharedPrefManager.isLoggedIn()) {
            // Пользователь уже авторизован
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // Пользователь не авторизован
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        }
        finish();
    }
}