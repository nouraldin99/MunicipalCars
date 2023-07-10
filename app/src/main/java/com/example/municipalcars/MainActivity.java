package com.example.municipalcars;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.municipalcars.adapter.ItemCarsAdapter;
import com.example.municipalcars.databinding.ActivityMainBinding;
import com.example.municipalcars.feature.AppSharedData;
import com.example.municipalcars.model.Car;
import com.example.municipalcars.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ItemCarsAdapter itemCarsAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("Drivers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.btnAddCar.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddCarActivity.class)));
        binding.btnProfile.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        if (AppSharedData.getUserData().getPhoto()!=null)  Glide.with(this).load(AppSharedData.getUserData().getPhoto()).into(binding.imgUser);

    }

    @Override
    protected void onResume() {
        super.onResume();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        // Process the documents here
                        List<UserData> userList = new ArrayList<>();
                        for (DocumentSnapshot document : documents) {

                            Log.e(TAG, "onComplete: " + document.getData());
                            Car car = new Car();
                            UserData user = new UserData();
                            String userId = document.getId();
                            long createdAt = document.getLong("createdAt");
                            String email = document.getString("email");
                            String name = document.getString("name");
                            String password = document.getString("password");
                            String type = document.getString("type");
                            String phone = document.getString("phone");
                            String address = document.getString("address");
                            String numCard = document.getString("numCard");
                            String photo = document.getString("photo");

                            // Retrieve the car data
                            long carCreatedAt = document.get("car.createdAt", Long.class);
                            String carType = document.getString("car.typeCar");
                            String carColor = document.getString("car.colorCar");
                            String carNum = document.getString("car.numCar");
                            String carPhoto = document.getString("car.photo");
                            String carName = document.getString("car.name");
                            String carDetails = document.getString("car.details");

                            if (String.valueOf(carCreatedAt) != null)
                                car.setCreatedAt(carCreatedAt);
                            if (carType != null) car.setTypeCar(carType);
                            if (carColor != null) car.setColorCar(carColor);
                            if (carNum != null) car.setNumCar(carNum);
                            if (carPhoto != null) car.setPhoto(carPhoto);
                            if (carName != null) car.setName(carName);
                            if (carDetails != null) car.setDetails(carDetails);

                            if (userId != null) user.setUserId(userId);
                            if (String.valueOf(createdAt) != null) user.setCreatedAt(createdAt);
                            if (email != null) user.setEmail(email);
                            if (name != null) user.setName(name);
                            if (password != null) user.setPassword(password);
                            if (type != null) user.setType(type);
                            if (phone != null) user.setPhone(phone);
                            if (address != null) user.setAddress(address);
                            if (numCard != null) user.setNumCard(numCard);
                            if (photo != null) user.setPhoto(photo);
                            user.setCar(car);
                            userList.add(user);


                        }

                        initialAdapter(userList);
                    }
                } else {

                }
            }
        });
    }

    private void initialAdapter(List<UserData> list) {
        itemCarsAdapter = new ItemCarsAdapter(MainActivity.this, list, new ItemCarsAdapter.OnClickListener() {
            @Override
            public void onItemClick(UserData item, int position) {
                startActivity(new Intent(MainActivity.this, CarDetailsActivity.class).putExtra("CAR",new Gson().toJson(item)));
            }
        });
        binding.rvCars.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        binding.rvCars.setAdapter(itemCarsAdapter);

    }


}