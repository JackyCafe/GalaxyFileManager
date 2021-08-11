package com.jsk.galaxyfilemanager;

import androidx.annotation.Nullable;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class StartService extends Service {
    public static final String SYNC_DATA_WORK_NAME = "sync_data_work_name";
    public static final long DELAY_TIME_MILLIS = 3000;
    public static final String TAG_SYNC_DATA = "TAG_SYNC_DATA";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Jacky","onStart");
        PeriodicWorkRequest work =
                new PeriodicWorkRequest.Builder(FileManageWork.class, 1, TimeUnit.SECONDS)
                        .addTag(TAG_SYNC_DATA)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.SECONDS)
                        .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("unique", ExistingPeriodicWorkPolicy.KEEP,work);

        return super.onStartCommand(intent, flags, startId);

    }

}
