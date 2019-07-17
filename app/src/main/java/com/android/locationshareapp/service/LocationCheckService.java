package com.android.locationshareapp.service;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.android.locationshareapp.helper.AppHelper;

public class LocationCheckService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (AppHelper.getBoolKey(getApplicationContext(), "isChecked") && !isServiceRunning()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                getApplicationContext().startService(new Intent(getApplicationContext(), LocationShareService.class));
            } else {
                Intent serviceIntent = new Intent(getApplicationContext(), LocationShareService.class);
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
            }
        }else{
            stopLocationService();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    private boolean isServiceRunning() {
        ActivityManager manager =
                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationShareService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void stopLocationService() {
        if (isServiceRunning())
            stopService(new Intent(getApplicationContext(), LocationShareService.class));
    }
}
