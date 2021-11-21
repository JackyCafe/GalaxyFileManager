package com.jsk.galaxyfilemanager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private File path;
    private ViewGroup viewGroup;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView tv,tv_time,tv_setup;
    private int PERMISSION_REQUEST_CODE = 200;

    private TextView numText;
    private int hours = 0;
    private int min = 15;
    private NumberPicker hourPicker,minPicker;
    private JobScheduler mJobScheduler;

    private SharedPreferences spf;
    private SharedPreferences.Editor editor ;
    private Date date;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        tv_time = findViewById(R.id.tv_time);
        tv_setup = findViewById(R.id.tv_setup);
        path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        hourPicker = (NumberPicker)findViewById(R.id.numberPicker_H);
        minPicker = (NumberPicker) findViewById(R.id.numberPicker_M);
        hourPicker.setMaxValue(12);
        hourPicker.setMinValue(0);

        minPicker.setMinValue(15);
        minPicker.setMaxValue(59);
        hourPicker.setValue(0);
        minPicker.setValue(min);
        date = new Date();
        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        spf  = getSharedPreferences("my_data.log",MODE_PRIVATE);
        editor = spf.edit();

        String last_time = spf.getString("date","");
        int param_hours = spf.getInt("hours",0);
        int param_min = spf.getInt("min",0);
        tv_time.setText("上次儲存時間: "+ last_time);
        tv_setup.setText("儲存參數:"+param_hours+":"+param_min);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hourPicker.setValue(newVal);
            }
        });
        minPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minPicker.setValue(newVal);
            }
        });

        if(checkPermission()){
            readTheFiles();
            Log.v("FileManager","have argee permission");
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            }else{
             ActivityCompat.requestPermissions(this,  PERMISSIONS_STORAGE,PERMISSION_REQUEST_CODE);
            }

        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== PERMISSION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
               Log.v("FileManager","need to argee permission");

                readTheFiles();
            }
            else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }


    }

    private boolean checkPermission() {
            if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.R) {
                return Environment.isExternalStorageManager();
            }else{
                int writePerm =   ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int readPerm = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
                return writePerm== PackageManager.PERMISSION_GRANTED && readPerm==PackageManager.PERMISSION_GRANTED;
            }
    }

    private void readTheFiles() {
        File files = new File(path.getPath());
        StringBuilder sb = new StringBuilder();
        for(File f: files.listFiles()){
            sb.append(f.getName()+"\n");
            Log.v("FileManager","file:"+f.getName());
        }
        tv.setText(sb.toString());
        Log.v("FileManager","Done!");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    readTheFiles();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void delete(View view) {
        File files = new File(path.getPath());
        tv.setText("");
        for(File f: files.listFiles()){
                f.delete();
        }
        StringBuilder sb = new StringBuilder();
        for(File f: files.listFiles()){
                sb.append(f.getName());
        }
        tv.setText(sb.toString());
        Log.v("FileManager","Done!");
        Snackbar.make(view,"pdf file 已被刪除",Snackbar.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void send(View view) {
        hours = hourPicker.getValue();
        min = minPicker.getValue();
        editor.putString("date",sdf.format(date));
        editor.commit();
        editor.putInt("hours",hours);
        editor.commit();
        editor.putInt("min",min);
        editor.commit();
        String last_time = spf.getString("date","");
        int param_hours = spf.getInt("hours",0);
        int param_min = spf.getInt("min",0);
        tv_time.setText("上次儲存時間: "+ last_time);
        tv_setup.setText("排程時間:"+param_hours+":"+param_min);

        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int jobId = 200;
        JobInfo jobInfo = new JobInfo.Builder(jobId
                , new ComponentName(getPackageName(), FileJobService.class.getName()))
                .setMinimumLatency(1)
                .setOverrideDeadline(1000*60*15)
                .setImportantWhileForeground(false)
                .setRequiresDeviceIdle(false)
                .build();
        mJobScheduler.schedule(jobInfo);
//        Log.i("Jacky","MainActivity:"+hours+":"+min);
//        Intent it = new Intent(this,StartService.class);
//        it.putExtra("hours",hours);
//        it.putExtra("min",min);
//        startService(it);
    }
}