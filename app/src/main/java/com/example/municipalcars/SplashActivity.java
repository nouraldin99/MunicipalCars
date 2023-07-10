package com.example.municipalcars;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalcars.databinding.ActivitySplashBinding;
import com.example.municipalcars.feature.AppSharedData;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        getWindow().setStatusBarColor(getColor(R.color.black04));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        new CountDownTimer(750, 350) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
            }

            @Override
            public void onFinish() {
                // this function will be called when the time count is finished
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long leftTimeInMilliseconds) {
                    }

                    @Override
                    public void onFinish() {
                        gotToNextScreen();
                    }
                }.start();
            }
        }.start();
    }

    private void gotToNextScreen() {
        if (AppSharedData.isUserLogin()) {
            if (AppSharedData.getUserData().getType().equals("driver")){
                startActivity(new Intent(SplashActivity.this, MapActivity.class));
            }else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }

}