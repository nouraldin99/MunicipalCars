package com.example.municipalcars.feature;


import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class FirebaseReferances {

    private static final String USERS_REF = "Users";
    private static final String SLIDER_REF = "Slider";
    private static final String VERSION_REF = "Version";
    private static final String ALBUMS_REF = "Albums";
    private static final String IMAGES_REF = "images";
    private static final String PLACES_REF = "Places";
    private static final String COUNTRIES_REF = "Country";
    private static final String CUSTOM_ALBUMS_REF = "CustomAlbums";
    private static final String OS = "os";
    private static final String TOKEN_REF = "DeviceToken";

    public static FirebaseAuth getAuthReference() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getAuthCurrentUserReference() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    private static DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUsersListReference() {
        return getReference()
                .child(USERS_REF);
    }

    public static DatabaseReference getSliderListReference() {
        return getReference().child(SLIDER_REF);
    }
 public static DatabaseReference getVersionCode() {
        return getReference()
                .child(VERSION_REF).child("ANDROID");
    }

    public static DatabaseReference getPlacesListReference() {
        return getReference()
                .child(PLACES_REF);
    }

    public static DatabaseReference getPlacesDetailsReference(String key) {
        return getPlacesListReference().child(key);
    }

    public static DatabaseReference getCountriesListReference() {
        return getReference()
                .child(COUNTRIES_REF);
    }

    public static DatabaseReference getCustomAlbumsListReference() {
        return getReference()
                .child(CUSTOM_ALBUMS_REF)
                .child(getUserId());
    }

    public static DatabaseReference getCountryDetailsReference(String key) {
        return getReference()
                .child(COUNTRIES_REF)
                .child(key);
    }

    public static DatabaseReference getMyAlbumsListReference() {
        return getReference()
                .child(ALBUMS_REF)
                .child(getUserId());
    }

    public static DatabaseReference getImagesFromAlbumsListReference(String key) {
        return getReference()
                .child(ALBUMS_REF)
                .child(getUserId())
                .child(key)
                .child(IMAGES_REF);
    }

    public static DatabaseReference getUserReference(String userId) {
        return getUsersListReference().child(userId);
    }

    public static String getUserId() {
        String userId = (getAuthReference().getCurrentUser() != null) ? getAuthReference().getCurrentUser().getUid() : "";
       if (TextUtils.isEmpty(userId)) {
//            userId=(AppSharedData.getUserData() != null)? AppSharedData.getUserData().getUserId():"";
        }
        return userId;
    }

    public static DatabaseReference saveUserDeviceOs(String userId) {
        return getUserReference(userId).child(OS);
    }

    public static DatabaseReference saveUserFcmTokenReference(String userId) {
        return getUserReference(userId).child(TOKEN_REF);
    }

    private static StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static StorageReference getStorageImageReference(String imageName) {
        return getStorageReference().child(imageName);
    }

}
