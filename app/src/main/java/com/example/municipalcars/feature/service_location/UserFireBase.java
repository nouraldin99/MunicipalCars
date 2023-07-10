package com.example.municipalcars.feature.service_location;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class UserFireBase {
    private static String TAG = UserFireBase.class.getSimpleName();


    public static void changeDriverLocation(String id, double lat, double lng, RequestListener<LatLng> requestListener) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("lat", lat + "");
        params.put("lng", lng + "");
        FirebaseReferences.getDriverLocationReference(id).updateChildren(params).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                requestListener.onSuccess(new LatLng(lat, lng));


            } else {
                requestListener.onFail(task.toString(), 0);
            }
        });
    }

}
