package com.example.municipalcars;

import static android.content.ContentValues.TAG;

import static com.example.municipalcars.feature.FirebaseReferances.getAuthReference;

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
import com.example.municipalcars.databinding.ActivityProfileBinding;
import com.example.municipalcars.databinding.DialogLogoutBinding;
import com.example.municipalcars.databinding.DialogPickImageBinding;
import com.example.municipalcars.feature.AppSharedData;
import com.example.municipalcars.feature.BaseActivity;
import com.example.municipalcars.feature.RequestListener;
import com.example.municipalcars.feature.User;
import com.example.municipalcars.feature.service_location.BackgroundLocationUpdateService;
import com.example.municipalcars.feature.service_location.ServiceTools;
import com.example.municipalcars.model.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends BaseActivity {

    ActivityProfileBinding binding;
    private File fileSelectedUserImage = null;
    private static final int CAMERA_REQUEST = 1888;
    private final int CAMERA_PERMISSION_REQUEST = 184864;
    private static final int PICK_IMAGE = 1844;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnAddPhoto.setOnClickListener(view -> showDialogChoseImage());

        if (AppSharedData.getUserData() != null) {
            UserData userData = AppSharedData.getUserData();
            setData(userData);
        }
        binding.btnSave.setOnClickListener(view -> {
            if (User.isNetworkConnected()) {
                showProgress();
                if (fileSelectedUserImage != null) {
                    User.uploadImage(Uri.fromFile(fileSelectedUserImage), AppSharedData.getUserData().getUserId(), new RequestListener<String>() {
                        @Override
                        public void onSuccess(String data) {
                            validateInputs(binding.eTxtNameDriver.getText().toString().trim(), binding.eTxtEmail.getText().toString().trim(), binding.eTxtPhoneDriver.getText().toString().trim(), data);
                        }

                        @Override
                        public void onFail(String message) {

                        }
                    });

                } else {
                    validateInputs(binding.eTxtNameDriver.getText().toString().trim(), binding.eTxtEmail.getText().toString().trim(), binding.eTxtPhoneDriver.getText().toString().trim(), AppSharedData.getUserData().getPhoto() != null ? AppSharedData.getUserData().getPhoto() : null);
                }
            } else {

                hideProgress();
                showErrorMessage("لا يوجد اتصال بالشبكة");

            }
        });

        binding.btnLogout.setOnClickListener(view -> {
            showDialogLogout();
        });


    }

    public void validateInputs(String name, String email, String phone, String url) {
        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && url!=null) {

            UserData userData = AppSharedData.getUserData();
            userData.setEmail(email);
            userData.setName(name);
            userData.setPhone(phone);
            userData.setPhoto(url);
            User.updateUserDataToDatabase(userData, new RequestListener<UserData>() {
                @Override
                public void onSuccess(UserData data) {
                    AppSharedData.setUserData(data);
                    setData(AppSharedData.getUserData());
                    hideProgress();
                }

                @Override
                public void onFail(String message) {
                    hideProgress();
                    showErrorMessage(message);
                    Log.e("TAG", "onFailLogin: " + message);

                }
            });
        }else {
            showErrorMessage("هناك بعض البيانات الفارغة عليك تعبئتها");
        }

    }

    private void showDialogLogout() {
        DialogLogoutBinding bindingDialog = DialogLogoutBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(bindingDialog.getRoot());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bindingDialog.btnLogout.setOnClickListener(view -> {
            getAuthReference().signOut();
            AppSharedData.setUserLogin(false);
            AppSharedData.setUserData(null);

            if (ServiceTools.isMyServiceRunning(BackgroundLocationUpdateService.class)) {
                BackgroundLocationUpdateService.stopLocationService(ProfileActivity.this);
            }

            startActivity(new Intent(ProfileActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            dialog.dismiss();
            finish();
        });
        bindingDialog.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
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


    private void setData(UserData userData) {
        if (userData != null) {
            if (userData.getName() != null) binding.eTxtNameDriver.setText(userData.getName());
            if (userData.getPhone() != null) binding.eTxtPhoneDriver.setText(userData.getPhone());
            if (userData.getEmail() != null) binding.eTxtEmail.setText(userData.getEmail());
            if (userData.getPhoto() != null)
                Glide.with(ProfileActivity.this).load(userData.getPhoto()).into(binding.imageUser);

        }
    }
}