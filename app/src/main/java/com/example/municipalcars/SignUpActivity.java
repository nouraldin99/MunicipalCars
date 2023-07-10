package com.example.municipalcars;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalcars.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        binding.txtLogin.setOnClickListener(view -> finish());
        binding.btnSignIn.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, MainActivity.class)));
    }


}