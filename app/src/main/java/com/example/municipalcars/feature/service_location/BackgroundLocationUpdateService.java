package com.example.municipalcars.feature.service_location;

import static com.example.municipalcars.feature.service_location.FusedLocationSingleton.INTENT_FILTER_LOCATION_UPDATE;
import static com.example.municipalcars.feature.service_location.FusedLocationSingleton.LBM_EVENT_LOCATION_UPDATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.municipalcars.R;
import com.example.municipalcars.feature.AppSharedData;
import com.example.municipalcars.feature.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.HashMap;

public class BackgroundLocationUpdateService extends Service {

    private static final String TAG = BackgroundLocationUpdateService.class.getSimpleName();
    private Context context;

    private String latitude = "0.0", longitude = "0.0";

    private static BackgroundLocationUpdateService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }//met

    public static void setInstance(BackgroundLocationUpdateService instance) {
        BackgroundLocationUpdateService.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setInstance(this);
        onHandle();
    }

    private void onHandle() {

        StartForeground();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                stopSelf();
            }
        }
        FusedLocationSingleton.getInstance().startLocationUpdates();
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdated, new IntentFilter(INTENT_FILTER_LOCATION_UPDATE));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service Stopped");
        setInstance(null);
        if (mLocationUpdated != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationUpdated);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void StartForeground() {

//        registerReceiver(stopReceiver, new IntentFilter("stop"));



        String CHANNEL_ID = "channel_location";
        String CHANNEL_NAME = "Your Location Updates";

        NotificationCompat.Builder builder = null;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setSound(null, null);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setSound(null);
        }

        String title =  "اشعار تحديث لموقعك" ;
        String text =  "انت متصل الان" ;

        builder.setContentTitle(title);
        builder.setContentText(text);
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPriority(NotificationManager.IMPORTANCE_LOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
        }
        builder.setCategory(Notification.CATEGORY_SERVICE);
        builder.setSmallIcon(R.drawable.location);
        builder.setColor(getResources().getColor(R.color.white));
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        Notification notification = builder.build();
        startForeground(23111996, notification);

    }

    private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LBM_EVENT_LOCATION_UPDATE);
            Log.e(TAG, "mLocationUpdated onReceive: Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            UserFireBase.changeDriverLocation(AppSharedData.getUserData().getUserId() + "", location.getLatitude(), location.getLongitude(), new RequestListener<LatLng>() {
                @Override
                public void onSuccess(LatLng data) {

                    Intent intent = new Intent();
                    intent.setAction("FROM_LOCATION_BROADCAST");
                    intent.putExtra("location", new Gson().toJson(data));
                    sendBroadcast(intent);
                    Log.e(TAG, "onSuccess8888888888888: " + data.toString());

                }

                @Override
                public void onFail(String message, int code) {
                    Bundle params = new Bundle();
                    params.putString("changeLocation", "onFail() - " + message + " - " + code);
                    FirebaseAnalytics.getInstance(context).logEvent(TAG, params);
                }

            });

        }
    };

    @SuppressLint("HardwareIds")
    public static String getDeviceId() {
        return Settings.Secure.getString(instance.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void updateLocationToMap(float direction, String speedString) {
        Intent intent = new Intent("com.newsolutions.UserFireBasedispatch.LOCATION_CHANGED");
        intent.putExtra("location", latitude + "," + longitude);
        intent.putExtra("direction", direction);
        intent.putExtra("speedString", speedString);
        sendBroadcast(intent);
    }

    static String replaceNonstandardDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (isNonstandardDigit(ch)) {
                int numericValue = Character.getNumericValue(ch);
                if (numericValue >= 0) {
                    builder.append(numericValue);
                }
            } else if (ch == '٫') {
                builder.append(".");
            } else {
                builder.append(ch);
            }
        }
//        Log.e(TAG, "replaceNonstandardDigits: " + input + " - > " + builder.toString());
        return builder.toString();
    }

    private static boolean isNonstandardDigit(char ch) {
        return Character.isDigit(ch) && !(ch >= '0' && ch <= '9');
    }

    public static void startLocationService(Context context) {
        Log.d(TAG, " startLocationService: ");
        if (isInstanceCreated()) return;
        Intent intentService = new Intent(context, BackgroundLocationUpdateService.class);


        context.stopService(intentService);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startService(intenAddPersonalVehicleFragment.java:330tService);
            context.startForegroundService(intentService);
        } else {
            //lower then Oreo, just start the service.
            context.startService(intentService);
        }
    }

    public static void stopLocationService(Context context) {
        Log.d(TAG, "stopLocationService: ");
        Intent intentService = new Intent(context, BackgroundLocationUpdateService.class);
        context.stopService(intentService);

        FusedLocationSingleton.getInstance().stopLocationUpdates();

    }

    // request for update Location To Server as lookups needs
    public void updateLocationToServer(double lat, double lng) {

        if (User.isNetworkConnected()) {

            HashMap<String, Object> params = new HashMap<>();
            params.put("latitude", lat);
            params.put("longitude", lng);

//            APP.getInstance().getApiServiceAuth().updateLocation(params).enqueue(new Callback<MainResponse>() {
//                @Override
//                public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
//                    if (response.isSuccessful()) {
//                        if (response.body() != null) {
//                            if (response.body().isStatus()) {
//                                Log.e(TAG, "onResponse: mLocationUpdated");
////                                if (addressesAdapter != null) {
////                                    addressesAdapter.setSelectedPosition((int) params.get("is_default") == 0 ? -1 : pos);
////                                }
//                            } else {
////                                makeToast(response.message());
//                            }
//                        } else {
////                            makeToast(response.message());
//                        }
//                    } else {
////                        makeToast(response.message());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MainResponse> call, Throwable throwable) {
//                    String msg = ToolUtils.getThrowableErrorMsg(throwable);
//                    int code = ToolUtils.getErrorCode(throwable);
////                    if (code == 401) {
////                        RequestUtils.refreshToken(AddressesActivity.this, new OnRefreshListener() {
////                            @Override
////                            public void onSuccess() {
////                                requestEditAddressAsDefault(pos, model);
////                            }
////
////                            @Override
////                            public void onFail(String message) {
////                                AppSharedData.endUserSession(AddressesActivity.this);
////                            }
////                        });
////                    } else
////                        makeToast(code + " - " + msg);
//                }
//            });
        }

//        else {
//            warningUserWithBlur(getStrFromR(R.string.sorry_there_is_no_internet_connection), getStrFromR(R.string.retry), ok -> new Handler().postDelayed(() -> requestEditAddressAsDefault(pos, model), AppConstant.DELAY_RETRY));
//        }

    }


}