package com.example.municipalcars.feature.service_location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseReferences {

    private static final String AMBULANCE_REF = "Users";
    private static final String DRIVERS_LOCATION_REF = "drivers_location";

    public static DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUsersListReference() {
        return getReference().child(AMBULANCE_REF);
    }

    public static DatabaseReference getUserReference(String userId) {
        return getUsersListReference().child(userId);
    }

    public static DatabaseReference getDriversLocationReference() {
        return getReference().child(DRIVERS_LOCATION_REF);
    }

    public static DatabaseReference getDriverLocationReference(String userId) {
        return getDriversLocationReference().child(userId);
    }

}
