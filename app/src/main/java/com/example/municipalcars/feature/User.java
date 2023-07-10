package com.example.municipalcars.feature;


import static com.example.municipalcars.feature.FirebaseReferances.getAuthCurrentUserReference;
import static com.example.municipalcars.feature.FirebaseReferances.getAuthReference;
import static com.example.municipalcars.feature.FirebaseReferances.saveUserDeviceOs;
import static com.example.municipalcars.feature.FirebaseReferances.saveUserFcmTokenReference;
import static com.google.firebase.messaging.reporting.MessagingClientEvent.SDKPlatform.ANDROID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.municipalcars.APP;
import com.example.municipalcars.R;
import com.example.municipalcars.model.UserData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.Objects;


public class User {

    private static String TAG = User.class.getSimpleName();
    private static Context context;
    private static UserData userData;

    public static void addNewDriver(UserData userData, RequestListener<UserData> requestListener) {
        Log.e("TAG", "userSignUp: " + userData.toString());
        APP.firebaseAuth.createUserWithEmailAndPassword(userData.getEmail(), userData.getPassword()).addOnCompleteListener(authResult -> {
            if (authResult.isSuccessful()) {
                FirebaseUser firebaseUser = getAuthCurrentUserReference();
                userData.setUserId(firebaseUser.getUid());
                saveDriverDataToDatabase(userData, requestListener);
                requestListener.onSuccess(userData);
                Log.e("TAG", "userSignUpDone: ");
            } else {
                requestListener.onFail(taskVoidException2(authResult));
                Log.e("TAG", "userSignUp:سسسس " + taskVoidException2(authResult));
            }
        });


    }

    public static void saveDriverDataToDatabase(UserData userData, RequestListener<UserData> requestListener) {
        Log.e(TAG, "saveUserDataToDatabase: " + userData.toString());
        APP.db.collection("Drivers").document(userData.getUserId()).set(UserData.toMap(userData)).addOnCompleteListener(documentReference -> {
            if (documentReference.isSuccessful()) {
                Log.e("TAG", "onSuccess: " + userData.getUserId());
                requestListener.onSuccess(userData);
            } else {
                Log.e(TAG, "saveUserDataToDatabase: xxxxx");
                requestListener.onFail(documentReference.getException().getMessage());
            }
        });
    }

    public static void updateUserDataToDatabase(UserData userData, RequestListener<UserData> requestListener) {
        Log.e(TAG, "saveUserDataToDatabase: " + userData.toString());
        APP.db.collection(AppSharedData.getUserData().getType().equals("admin")?"users":"Drivers").document(userData.getUserId()).update(UserData.toMap(userData)).addOnCompleteListener(documentReference -> {
            if (documentReference.isSuccessful()) {
                Log.e("TAG", "onSuccess: " + userData.getUserId());
                requestListener.onSuccess(userData);
            } else {
                Log.e(TAG, "saveUserDataToDatabase: xxxxx");
                getAuthReference().signOut();
                requestListener.onFail(documentReference.getException().getMessage());
            }
        });
    }

    public static void loginWithEmailAndPassword(Context context, String email, String password, RequestListener<UserData> requestListener) {
        APP.firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener(authResult -> {
            if (authResult.isSuccessful()) {
                getUserData(context, requestListener);
                Log.e("TAG", "loginWithEmailAndPassword: ");

            } else {
                requestListener.onFail(authResult.getException().getMessage());
                Log.e("TAG", "loginWithEmailAndPasswordFail: " + authResult.getException().getMessage());
            }
        });
    }

    public static void getUserData(Context context, RequestListener<UserData> requestListener) {
        CollectionReference usersRef = APP.db.collection("users");
        CollectionReference usersRef2 = APP.db.collection("Drivers");
        FirebaseUser firebaseUser = getAuthCurrentUserReference();
        String uid = firebaseUser.getUid();
        userData = new UserData();
        if (usersRef.document(uid).get().isSuccessful()) {
        }
        usersRef.document(uid).get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Log.e("Firestore", "User exists!");
                if (task.isSuccessful()) {
                    Log.e("Firestoresss", "User exists!");
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        userData = documentSnapshot.toObject(UserData.class);
                        requestListener.onSuccess(userData);
//                    AppSharedData.setUserData(userData);
                        Log.e("TAG", "getUserData: " + new Gson().toJson(userData));
                    } else {
                        requestListener.onFail(context.getString(R.string.no_data_found));
                    }
                }
            } else {
                Log.e("Firestore222", "User exists!");
                usersRef2.document(uid).get().addOnCompleteListener(task1 -> {
                    DocumentSnapshot document2 = task1.getResult();
                    if (document2.exists()) {
                        if (task1.isSuccessful()) {
                            Log.e("Firestore", "User exists!wwwwwwwwww");
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            if (documentSnapshot != null) {
                                userData = documentSnapshot.toObject(UserData.class);
                                requestListener.onSuccess(userData);
                                Log.e("TAG", "getUserData: " + new Gson().toJson(userData));
                            } else {
                                requestListener.onFail(context.getString(R.string.no_data_found));
                            }
                        } else {
                            requestListener.onFail(Objects.requireNonNull(task1.getException()).getMessage());
                        }
                    }
                });
            }
        });

    }

    public static void saveNewFcmToken(String userId, String token, RequestListener<Boolean> requestListener) {
        if (!TextUtils.isEmpty(userId)) {
            saveUserDeviceOs(userId).setValue(ANDROID);
            saveUserFcmTokenReference(userId).setValue(token).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    requestListener.onSuccess(true);
                } else {
                    requestListener.onFail("Save Token Failed");
                }
            });
        } else requestListener.onFail("Save Token Failed");
    }

    public static void resetPassword(String email, RequestListener<String> requestListener) {
        getAuthReference().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess("");
            } else {
                requestListener.onFail(taskVoidException(task));
            }
        });
    }

    private static String taskVoidException(Task<Void> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            return "خطأ في كلمة المرور";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            return "كلمة المرور غير مشابهة";
        } catch (FirebaseAuthInvalidUserException e) {
            return "كلمة المرور غير مشابهة";
        } catch (FirebaseNoSignedInUserException e) {
            return "خطأ في البريد الالكتروني";
        } catch (FirebaseAuthEmailException e) {
            return "خطأ في البريد الالكتروني";
        } catch (FirebaseAuthUserCollisionException e) {
            return "هناك خطأ ما";
        } catch (Exception e) {
            return "هناك خطأ ما";
        }
    }

    private static String taskVoidException2(Task<AuthResult> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseAuthWeakPasswordException e) {
            return "خطأ في كلمة المرور";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            return "كلمة المرور غير مشابهة";
        } catch (FirebaseAuthInvalidUserException e) {
            return "كلمة المرور غير مشابهة";
        } catch (FirebaseNoSignedInUserException e) {
            return "خطأ في البريد الالكتروني";
        } catch (FirebaseAuthEmailException e) {
            return "خطأ في البريد الالكتروني";
        } catch (FirebaseAuthUserCollisionException e) {
            return "هناك خطأ ما";
        } catch (Exception e) {
            return "هناك خطأ ما";
        }
    }


    public static void changeUserInDatabase(UserData userData, RequestListener<String> requestListener) {

        APP.db.collection("users").document(Objects.requireNonNull(APP.firebaseAuth.getUid())).set(UserData.toMap(userData)).addOnCompleteListener(documentReference -> {
            if (documentReference.isSuccessful()) {
                AppSharedData.setUserData(userData);
                Log.e("TAG", "onSuccess: " + userData.getUserId());
                requestListener.onSuccess("done");
            } else {
                getAuthReference().signOut();
                requestListener.onFail(documentReference.getException().getMessage());
            }
        });
    }

    public static boolean isNetworkConnected() {
        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) APP.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equals("WIFI")) ;
            if (ni.isConnected())
                HaveConnectedWifi = true;
            if (ni.getTypeName().equals("MOBILE"))
                if (ni.isConnected())
                    HaveConnectedMobile = true;
        }
        return HaveConnectedWifi || HaveConnectedMobile;
    }

    public static void uploadImage(Uri imageUri, String imageName, RequestListener<String> listener) {

        // Create a reference to the Firebase Storage location where you want to upload the image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageName);

        // Create metadata for the image file (optional)
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/*") // Change the content type as per your image type
                .build();

        // Upload the file to Firebase Storage
        UploadTask uploadTask = storageReference.putFile(imageUri, metadata);

        // Register a listener to get the upload task completion status
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Retrieve the download URL of the uploaded image
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Get the download URL of the uploaded image
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();

                    // Notify the listener about the successful upload
                    if (listener != null) {
                        listener.onSuccess(imageUrl);
                    }
                } else {
                    // Notify the listener about the upload failure
                    if (listener != null) {
                        listener.onFail(task.getException().toString());
                    }

                    Log.e(TAG, "Image upload failed: " + task.getException());
                }
            }
        });
    }

}
