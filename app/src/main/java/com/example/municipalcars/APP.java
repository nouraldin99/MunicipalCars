package com.example.municipalcars;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.municipalcars.model.UserVehicle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class APP extends Application {

    public static String token = "";
    public static boolean hasNewCar = false;

    private static APP instance;
    public static UserVehicle selectedUserVehicle;
    public static FirebaseFirestore db;
    private static Context context;

    public static FirebaseAuth firebaseAuth;

    public static APP getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        context = getInstance();
    }
    public static Context getContext() {
        return context;
    }



}
