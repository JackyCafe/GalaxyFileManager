package com.jsk.galaxyfilemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private File path;
    private ViewGroup viewGroup;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView tv;
    private int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        viewGroup = (ViewGroup) findViewById(R.id.vg);
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
            Log.v("FileManager","file:"+f.getName());
            int i = f.getName().lastIndexOf('.');
            if (i > 0) {
                String extension = f.getName().substring(i+1);
                if(extension.equals("pdf"))
                {f.delete();

                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for(File f: files.listFiles()){
            int i = f.getName().lastIndexOf('.');
            if (i > 0) {
                String extension = f.getName().substring(i + 1);
                if (!extension.equals("pdf"))
                    sb.append(f.getName() + "\n");
                Log.v("FileManager", "file:" + f.getName());
            }
        }
        tv.setText(sb.toString());
        Log.v("FileManager","Done!");


        Snackbar.make(view,"pdf file have been deleted",Snackbar.LENGTH_LONG).show();
 //        Log.v("FileManager","Done!");
    }
}