package com.example.municipalcars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.municipalcars.databinding.ActivityLoginBinding;
import com.example.municipalcars.feature.AppSharedData;
import com.example.municipalcars.feature.LoadingDialog;
import com.example.municipalcars.feature.RequestListener;
import com.example.municipalcars.feature.User;
import com.example.municipalcars.model.UserData;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private LoadingDialog loadingDialog;

    boolean isValid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        initDialog();
        binding.txtSignUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        binding.btnSignIn.setOnClickListener(view -> {
            isValid=true;
            String email=binding.eTxtEmail.getText().toString().trim();
            String password=binding.eTxtPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()){
                if (password.length()<6){
                    binding.eTxtPassword.setError("الرجاء ادخال كلمة مرور طويلة");
                    isValid=false;
                }
                if (!email.contains("@")){
                    binding.eTxtEmail.setError("الرجاء ادخال بريد الالكتروني صحيح");
                isValid=false;
                }
                if (isValid){
                    login(email,password);
                }

            }else {
                if (email.isEmpty()){
                    binding.eTxtEmail.setError("الرجاء ادخال البريد الالكتروني");
                } if (!email.contains("@")){
                    binding.eTxtEmail.setError("الرجاء ادخال بريد الالكتروني صحيح");
                }

                if (password.isEmpty()){
                    binding.eTxtPassword.setError("الرجاء ادخال كلمة المرور");
                }else if (password.length()<6){
                    binding.eTxtPassword.setError("الرجاء ادخال كلمة مرور طويلة");
                }
            }
        });

    }

    public void initDialog() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setDialogCancelable();
    }

    private void login(String email, String password) {
        if (User.isNetworkConnected()) {
            loadingDialog.showDialog();
            User.loginWithEmailAndPassword(LoginActivity.this, email, password, new RequestListener<UserData>() {
                @Override
                public void onSuccess(UserData data) {

                    AppSharedData.setUserData(data);
                    AppSharedData.setUserLogin(true);
                    Log.e("TAG", "onSuccessLogIn: " + new Gson().toJson(data));
                    loadingDialog.hideDialog();
                    if (data.getType().equals("driver")){
                        startActivity(new Intent(LoginActivity.this, MapActivity.class));
                    }else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }

                    finish();
                }

                @Override
                public void onFail(String message) {
                    loadingDialog.hideDialog();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "onFailLogin: " + message);
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }
    }
}