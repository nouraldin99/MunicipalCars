package com.example.municipalcars;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.municipalcars.databinding.ActivityAddCarBinding;
import com.example.municipalcars.databinding.DialogPickImageBinding;
import com.example.municipalcars.feature.BaseActivity;
import com.example.municipalcars.feature.RequestListener;
import com.example.municipalcars.feature.User;
import com.example.municipalcars.model.Car;
import com.example.municipalcars.model.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddCarActivity extends BaseActivity {

    ActivityAddCarBinding binding;

    boolean isValid = true;
    private File fileSelectedUserImage = null;
    private static final int CAMERA_REQUEST = 1888;
    private final int CAMERA_PERMISSION_REQUEST = 184864;
    private static final int PICK_IMAGE = 1844;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAddCarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnAddPhoto.setOnClickListener(view -> showDialogChoseImage());
        binding.btnSave.setOnClickListener(view -> {
            if (User.isNetworkConnected()) {
                isValid = true;
                String nameCar = binding.eTxtNameCar.getText().toString().trim();
                String detailsCar = binding.eTxtDetailsCar.getText().toString().trim();
                String typeCar = binding.eTxtTypeCar.getText().toString().trim();
                String numCar = binding.eTxtNum.getText().toString().trim();
                String colorCar = binding.eTxtColor.getText().toString().trim();
                String driverName = binding.eTxtNameDriver.getText().toString().trim();
                String numD = binding.eTxtNumCard.getText().toString().trim();
                String address = binding.eTxtAddressDriver.getText().toString().trim();
                String phoneDriver = binding.eTxtPhone.getText().toString().trim();
                String email = binding.eTxtEmail.getText().toString().trim();
                String password = binding.eTxtPassword.getText().toString().trim();

                if (nameCar.isEmpty()) {
                    binding.eTxtNameCar.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (detailsCar.isEmpty()) {
                    binding.eTxtDetailsCar.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (typeCar.isEmpty()) {
                    binding.eTxtTypeCar.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (numCar.isEmpty()) {
                    binding.eTxtNum.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (colorCar.isEmpty()) {
                    binding.eTxtColor.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (driverName.isEmpty()) {
                    binding.eTxtNameDriver.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (numD.isEmpty()) {
                    binding.eTxtNumCard.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (address.isEmpty()) {
                    binding.eTxtAddressDriver.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (phoneDriver.isEmpty()) {
                    binding.eTxtPhone.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (email.isEmpty()) {
                    binding.eTxtEmail.setError("هذا الحقل مطلوب");
                    isValid = false;
                }
                if (password.isEmpty()) {
                    binding.eTxtPassword.setError("هذا الحقل مطلوب");
                    isValid = false;
                } else if (password.length() < 6) {
                    binding.eTxtPassword.setError("أدخل كلمة مرور قوية");
                    isValid = false;
                }
                if (isValid) {

                    if (fileSelectedUserImage != null) {
                        showProgress();
                        User.uploadImage(Uri.fromFile(fileSelectedUserImage), binding.eTxtNameDriver.getText().toString().trim(), new RequestListener<String>() {
                            @Override
                            public void onSuccess(String data) {

                                UserData userData = new UserData();
                                Car car = new Car();

                                car.setTypeCar(typeCar);
                                car.setColorCar(colorCar);
                                car.setName(nameCar);
                                car.setDetails(detailsCar);
                                car.setNumCar(numCar);
                                car.setPhoto(data);

                                userData.setName(driverName);
                                userData.setPhone(phoneDriver);
                                userData.setAddress(address);
                                userData.setEmail(email);
                                userData.setType("driver");
                                userData.setNumCard(numD);
                                userData.setPassword(password);
                                userData.setCar(car);

                                validateInputs(userData);
                            }

                            @Override
                            public void onFail(String message) {
                                Log.e(TAG, "onFail: " + message);
                            }
                        });
                    } else {
                        showErrorMessage("الرجاء ادخال صورة للسيارة");
                    }
                }

            } else {

                hideProgress();
                showErrorMessage("لا يوجد اتصال بالشبكة");

            }
        });
    }

    private void pickImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted
                pickImageFromCamera();
            } else {
                // Camera permission has been denied
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.imageUser.setImageBitmap(bitmap);
            Glide.with(this).load(bitmap).into(binding.imageUser);

            File file = new File(getCacheDir(), "image.jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileSelectedUserImage = file;

            Log.e(TAG, "onActivityResult: " + fileSelectedUserImage.getPath());
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI and save it to a file
            Uri uri = data.getData();
            binding.imageUser.setImageURI(uri);
            Glide.with(this).load(uri).into(binding.imageUser);

            File file = new File(getCacheDir(), "image.jpg");
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                Log.e(TAG, "onActivityResult: ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileSelectedUserImage = file;
        }
    }


    public void showDialogChoseImage() {
        DialogPickImageBinding bindingDialog = DialogPickImageBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(bindingDialog.getRoot());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bindingDialog.btnCamera.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                dialog.dismiss();
            } else {
                // Camera permission has already been granted
                dialog.dismiss();
                pickImageFromCamera();

            }

        });
        bindingDialog.btnGallery.setOnClickListener(view -> {
            dialog.dismiss();
            pickImageFromGallery();
        });
        dialog.show();
    }

    public void validateInputs(UserData data) {
        User.addNewDriver(data, new RequestListener<UserData>() {
            @Override
            public void onSuccess(UserData data) {
                Toast.makeText(AddCarActivity.this, "تم اضافة السيارة بنجاح", Toast.LENGTH_LONG).show();
                hideProgress();
                finish();
            }

            @Override
            public void onFail(String message) {
                hideProgress();
                showErrorMessage(message);
                Log.e("TAG", "onFailLogin: " + message);

            }
        });

    }
}