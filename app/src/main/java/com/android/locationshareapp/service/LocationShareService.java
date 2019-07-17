package com.android.locationshareapp.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.entity.LocationEntity;
import com.android.locationshareapp.db_room.room.AppDatabase;
import com.android.locationshareapp.ui.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class LocationShareService extends Service {

    private static final String TAG = "LocationShareService";
    String NOTIFICATION_CHANNEL_ID = "com.android.locationshareapp";
    private int UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationRequest locationRequest = null;
    private LocationCallback mLocationCallback = null;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private AppDatabase appDatabase = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (appDatabase == null) {
            appDatabase = AppDatabase.getDatabase(getApplicationContext());
        }
        createNotification();
        createLocationCallback();
        buildSettingRequest();
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void buildSettingRequest() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        } else {
            try {
                SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(locationRequest);
                LocationSettingsRequest mLocationSettingsRequest = builder.build();
                mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(settingsResponse -> {
                    if (ActivityCompat.checkSelfPermission(LocationShareService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        LocationShareService.this.stopSelf();
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(locationRequest,
                            mLocationCallback, Looper.myLooper());
                    Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> {
                    Log.e(TAG, e.toString());
//                        ResolvableApiException exception = (ResolvableApiException) e;
//                        try {
//                            exception.startResolutionForResult((MainActivity) MainActivity.this, 101);
//                        } catch (IntentSender.SendIntentException e1) {
//                            e1.printStackTrace();
//                        }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "locationResult"+locationResult);
                if (locationResult.getLastLocation() != null)
                    updateLocation(locationResult.getLastLocation());
            }
        };
    }

    private void updateLocation(Location mLastLocation) {
        long routeId = appDatabase.appDao().getMaxId();
        appDatabase.appDao().insertLocationItem(
                new LocationEntity(routeId,
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude())
        );
    }

    private void createNotification() {
        //  Log.d(TAG, "CreateNotification");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            String channelName = "Current location sharing";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name) + "- Current location sharing")
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                    .build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            startForeground(2, notification);
        } else {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name) + "- Current location sharing")
                    .setContentIntent(pendingIntent).build();
            startForeground(2, notification);
        }
    }

    private void onStopUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onDestroy() {
        //  Log.d(TAG, "onDestroy");
        stopForeground(true);
        onStopUpdate();
        super.onDestroy();
    }
}
