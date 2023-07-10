package com.example.municipalcars;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.municipalcars.databinding.ActivityMapBinding;
import com.example.municipalcars.feature.AppSharedData;
import com.example.municipalcars.feature.service_location.BackgroundLocationUpdateService;
import com.example.municipalcars.feature.service_location.ServiceTools;
import com.google.android.datatransport.BuildConfig;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import mumayank.com.airlocationlibrary.AirLocation;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityMapBinding binding;
    private GoogleMap mMap;

    MarkerOptions markerOptions;
    String id;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locationRef ;
    private ValueEventListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerOptions = new MarkerOptions();

        binding.btnBack.setOnClickListener(view -> finish());
        if (AppSharedData.getUserData().getPhoto() != null)
            Glide.with(this).load(AppSharedData.getUserData().getPhoto()).into(binding.imgUser);
        binding.btnProfile.setOnClickListener(view -> startActivity(new Intent(MapActivity.this, ProfileActivity.class)));
        if (AppSharedData.getUserData() != null && AppSharedData.getUserData().getType().equals("driver")) {
            startLocation();
            binding.btnBack.setVisibility(View.INVISIBLE);
        } else {
            binding.btnBack.setVisibility(View.VISIBLE);
            id=getIntent().getStringExtra("ID_D");
            if (id!=null) {
                locationRef= database.getReference("drivers_location").child(id);

                locationListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Get the updated location values
                            double latitude = Double.parseDouble(dataSnapshot.child("lat").getValue(String.class));
                            double longitude = Double.parseDouble(dataSnapshot.child("lng").getValue(String.class));
                            LatLng location = new LatLng(latitude, longitude);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 12.0f);
                            mMap.clear();
                            mMap.moveCamera(cameraUpdate);
                            markerOptions.position(location);
                            mMap.addMarker(markerOptions);
                        }else {
                            Toast.makeText(MapActivity.this, "هذا السائق لم يقم بالتحرك من قبل", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors
                        System.out.println("Error retrieving location: " + databaseError.getMessage());
                    }
                };

                locationRef.addValueEventListener(locationListener);
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationRef != null && locationListener != null) {
            locationRef.removeEventListener(locationListener);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(31.401776, 34.346993);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        float zoomLevel = 15.0f;
        LatLng targetLocation = new LatLng(sydney.latitude, sydney.longitude); // Desired target location coordinates
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(targetLocation, zoomLevel);
        mMap.animateCamera(cameraUpdate);

    }
    //******** Notification Receiver ********

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng location = new Gson().fromJson(intent.getStringExtra("location"), LatLng.class);
            if (location != null) {

                Log.e("TAG", "onReceivesssssss: " + intent.getStringExtra("location"));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15.0f);
                mMap.clear();
                mMap.moveCamera(cameraUpdate);
                markerOptions.position(location);
                mMap.addMarker(markerOptions);
            }

        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppSharedData.getUserData().getType().equals("driver")){
        try {
            if (receiver != null) {
                registerReceiver(receiver, new IntentFilter("FROM_LOCATION_BROADCAST"));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        }
    }
    ////////////////////////
    private AirLocation airLocation;


    // for Permission call back
    private ActivityResultLauncher<String> mPermissionResult2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startLocation();
        } else {
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (showRationale) {
                startLocation();
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + com.google.android.datatransport.BuildConfig.APPLICATION_ID)));
            }
        }
    });

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            startLocation();
        } else {
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            if (showRationale) {
                startLocation();
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            }
        }
    });

    // Location tracking function for AMB
    void startLocation() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(MapActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            mPermissionResult.launch(permission);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
                if (ActivityCompat.checkSelfPermission(MapActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionResult2.launch(permission);
                } else {
                    airLocation = new AirLocation(MapActivity.this, true, true, new AirLocation.Callbacks() {
                        @Override
                        public void onSuccess(@NotNull Location location) {
                            Log.e("TAG", location.getLatitude() + " - " + location.getLongitude());
                            if (!ServiceTools.isMyServiceRunning(BackgroundLocationUpdateService.class)) {
                                BackgroundLocationUpdateService.startLocationService(MapActivity.this);
                            } else {
                                if (ServiceTools.isMyServiceRunning(BackgroundLocationUpdateService.class)) {
                                    BackgroundLocationUpdateService.stopLocationService(MapActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                            if (locationFailedEnum == AirLocation.LocationFailedEnum.LocationOptimizationPermissionNotGranted) {
                                Toast.makeText(MapActivity.this, "الرجاء الموافقة على اعطاء اذن الوصول للموقع", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else {
                airLocation = new AirLocation(MapActivity.this, true, true, new AirLocation.Callbacks() {
                    @Override
                    public void onSuccess(@NotNull Location location) {
                        Log.e("TAG", location.getLatitude() + " - " + location.getLongitude());
                        if (!ServiceTools.isMyServiceRunning(BackgroundLocationUpdateService.class)) {
                            BackgroundLocationUpdateService.startLocationService(MapActivity.this);

                        } else {
                            if (ServiceTools.isMyServiceRunning(BackgroundLocationUpdateService.class)) {
                                BackgroundLocationUpdateService.stopLocationService(MapActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                        Log.e("TAG", "onGranted: onFailed");
                        if (locationFailedEnum == AirLocation.LocationFailedEnum.LocationOptimizationPermissionNotGranted) {
                            Toast.makeText(MapActivity.this, "الرجاء الموافقة على اعطاء اذن الوصول للموقع", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}