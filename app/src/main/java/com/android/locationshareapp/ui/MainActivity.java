package com.android.locationshareapp.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.locationshareapp.R;
import com.android.locationshareapp.db_room.entity.TripEntity;
import com.android.locationshareapp.db_room.room.AppDatabase;
import com.android.locationshareapp.helper.AppHelper;
import com.android.locationshareapp.service.LocationShareService;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch switchState = findViewById(R.id.switchState);
        switchState.setChecked(AppHelper.getBoolKey(this, "isChecked"));
        handleForeGroundService();
        switchState.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_trip_detail, null);
            if (isChecked) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(customLayout);
                builder.setCancelable(false);
                builder.setTitle("Trip Detail");
                builder.setPositiveButton("Submit", (dialogInterface, i) -> {
                    EditText etTripFrom = customLayout.findViewById(R.id.etTripFrom);
                    EditText etDest = customLayout.findViewById(R.id.etDest);
                    if (TextUtils.isEmpty(etTripFrom.getText()) || TextUtils.isEmpty(etDest.getText())) {
                        Toast.makeText(MainActivity.this, "Both Field Are Required.", Toast.LENGTH_SHORT).show();
                        switchState.setChecked(false);
                    } else {
                        AppDatabase
                                .getDatabase(getApplicationContext())
                                .appDao()
                                .insertRouteItem(
                                        new TripEntity(
                                                etTripFrom.getText().toString(),
                                                etDest.getText().toString()
                                        )
                                );
                        AppHelper.putKey(MainActivity.this, "isChecked", true);
                        handleForeGroundService();
                        dialogInterface.dismiss();
                    }
                });
                builder.create();
                builder.show();
            } else {
                AppHelper.putKey(MainActivity.this, "isChecked", false);
                handleForeGroundService();
            }
        });
        findViewById(R.id.btnTrips).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, TripsActivity.class)));
    }

    private void handleForeGroundService() {
        boolean isChecked = AppHelper.getBoolKey(this, "isChecked");
        if (isChecked && !isServiceRunning()) {
            if (isPermissionGranted()) {
                startLocationService();
            } else {
                requestPermission();
            }
        } else if (!isChecked && isServiceRunning()) {
            stopLocationService();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE);
    }

    private void startLocationService() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            getApplicationContext().startService(new Intent(getApplicationContext(), LocationShareService.class));
        } else {
            Intent serviceIntent = new Intent(getApplicationContext(), LocationShareService.class);
            ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
        }
    }

    private void stopLocationService() {
        stopService(new Intent(MainActivity.this, LocationShareService.class));
    }

    private boolean isServiceRunning() {
        ActivityManager manager =
                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if (LocationShareService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    AppHelper.getBoolKey(this, "isChecked") &&
                    !isServiceRunning())
                handleForeGroundService();
        }
    }
}
