package com.example.municipalcars;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.municipalcars.databinding.ActivityCarDetailsBinding;
import com.example.municipalcars.model.UserData;
import com.google.gson.Gson;

public class CarDetailsActivity extends AppCompatActivity {

    ActivityCarDetailsBinding binding;
    UserData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCarDetailsBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(view -> finish());

            binding.btnLocation.setOnClickListener(view -> {
                if (data != null) {
                    startActivity(new Intent(CarDetailsActivity.this, MapActivity.class).putExtra("ID_D", data.getUserId()));
                }
            });
        geIntentData();
    }

    private void geIntentData() {
        String d = getIntent().getStringExtra("CAR");
        if (d != null) {
            data = new Gson().fromJson(d, UserData.class);
            setDataCar(data);
        }
    }

    private void setDataCar(UserData item) {
        if (item != null && item.getCar() != null) {
            if (item.getCar().getName() != null)
                binding.txtNameCar.setText(item.getCar().getName());
            if (item.getCar().getDetails() != null)
                binding.txtdesCar.setText(item.getCar().getDetails());
            if (item.getCar().getNumCar() != null)
                binding.txtNumCardCar.setText(item.getCar().getNumCar());
            if (item.getCar().getColorCar() != null)
                binding.txtColorCardDriver.setText(item.getCar().getColorCar());
            if (item.getNumCard() != null) binding.txtNumCardDriver.setText(item.getNumCard());
            if (item.getName() != null) binding.txtNameDriver.setText(item.getName());
            if (item.getPhone() != null) binding.txtPhoneDriver.setText(item.getPhone());
            if (item.getCar().getPhoto() != null)
                Glide.with(this).load(item.getCar().getPhoto()).into(binding.imageCar);
        }
    }
}