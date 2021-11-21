package com.jsk.galaxyfilemanager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class StartServiceOnBootReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID=1;
    private JobScheduler mJobScheduler;

    NotificationManager mgr;
    Notification myNotification;
    NotificationCompat.Builder builder;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {

        mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent it = new Intent(context, MainActivity.class);
        builder = new NotificationCompat.Builder(context).setTicker("message");
        Intent appIntent=null;
        appIntent = new Intent(context,MainActivity.class);
        appIntent.setAction(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//關鍵的一步，設定啟動模式
        PendingIntent contentIntent =PendingIntent.getActivity(context, 0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("YH","custom",NotificationManager.IMPORTANCE_HIGH);
            mgr.createNotificationChannel(notificationChannel);
            myNotification = builder.setContentIntent(contentIntent).setContentTitle("管理")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId("YH")
                    .setContentText("download 資料夾").build();
            myNotification.flags = Notification.FLAG_AUTO_CANCEL;
            mgr.notify(10, myNotification);
        }
        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int jobId = 201;
        JobInfo jobInfo = new JobInfo.Builder(jobId
                , new ComponentName(context.getPackageName(), FileJobService.class.getName()))
                .setMinimumLatency(1)
                .setOverrideDeadline(1000*60*15)
                .setImportantWhileForeground(false)
                .setRequiresDeviceIdle(false)
                .build();
        mJobScheduler.schedule(jobInfo);

        mJobScheduler.schedule(jobInfo);
        Log.v("Jacky","OnReceiver");




    }
}