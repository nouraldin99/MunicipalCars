package com.example.municipalcars.feature.service_location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.municipalcars.APP;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class FusedLocationSingleton implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "FusedLocationSingleton";
    /***********************************************************************************************
     * properties
     **********************************************************************************************/
    private static FusedLocationSingleton mInstance = null;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public final static int FAST_LOCATION_FREQUENCY = 10 * 1000;
    public final static int LOCATION_FREQUENCY = 15 * 1000;
//    public final static int FAST_LOCATION_FREQUENCY = APP.getInstance().getSharedPreferences().readBoolean(WSharedPreferences.IS_LABOUR_BUSY, false) && !APP.getInstance().getSharedPreferences().readString(WSharedPreferences.IS_LABOUR_BUSY_ORDER_NUMBER).equals("-1") && APP.getInstance().getSharedPreferences().readString(WSharedPreferences.IS_LABOUR_BUSY_ORDER_STATUS).equals("1") ? 5 * 1000 : 200 * 1000;
//    public final static int LOCATION_FREQUENCY = APP.getInstance().getSharedPreferences().readBoolean(WSharedPreferences.IS_LABOUR_BUSY, false) && !APP.getInstance().getSharedPreferences().readString(WSharedPreferences.IS_LABOUR_BUSY_ORDER_NUMBER).equals("-1") && APP.getInstance().getSharedPreferences().readString(WSharedPreferences.IS_LABOUR_BUSY_ORDER_STATUS).equals("1") ? 10 * 1000 : 400 * 1000;

    /***********************************************************************************************
     * methods
     **********************************************************************************************/
    /**
     * constructor
     */
    public FusedLocationSingleton() {
        buildGoogleApiClient();
    }

    /**
     * destructor
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLocationUpdates();
    }

    public static FusedLocationSingleton getInstance() {
        if (null == mInstance) {
            mInstance = new FusedLocationSingleton();
        }
        return mInstance;
    }

    ///////////// 1

    /**
     * builds a GoogleApiClient
     */
    private synchronized void buildGoogleApiClient() {
        // setup googleapi client
        mGoogleApiClient = new GoogleApiClient.Builder(APP.getInstance())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // setup location updates
        configRequestLocationUpdate();
    }

    ///////////// 2

    /**
     * config request location update
     */
    @SuppressLint({"MissingPermission", "RestrictedApi"})

    private void configRequestLocationUpdate() {
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY)
                .setFastestInterval(FAST_LOCATION_FREQUENCY);
    }

    ///////////// 3

    /**
     * request location updates
     */

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );
    }

    /**
     * start location updates
     */
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public void startLocationUpdates() {
        // connect and force the updates
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    /**
     * removes location updates from the FusedLocationApi
     */
    public void stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    /**
     * get last available location
     *
     * @return last known location
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(
            anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}
    )
    public Location getLastLocation() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            // return last location
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            startLocationUpdates(); // start the updates
            return null;
        }
    }

    /***********************************************************************************************
     * GoogleApiClient Callbacks
     **********************************************************************************************/
    @Override
    public void onConnected(Bundle bundle) {
        // do location updates
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection to Google Play services was lost for some reason
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect(); // attempt to establish a new connection
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /***********************************************************************************************
     * Location Listener Callback
     **********************************************************************************************/
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        if (location != null) {
            Log.d(TAG, "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());

//            GlobalData.myLocation = new MyLocation(location.getLatitude(),location.getLongitude());
            // send location in broadcast
            Intent intent = new Intent(INTENT_FILTER_LOCATION_UPDATE);
            intent.putExtra(LBM_EVENT_LOCATION_UPDATE, location);
            LocalBroadcastManager.getInstance(APP.getInstance()).sendBroadcast(intent);
        }
    }
    public static final String LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate";
    public static final String INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate";
}