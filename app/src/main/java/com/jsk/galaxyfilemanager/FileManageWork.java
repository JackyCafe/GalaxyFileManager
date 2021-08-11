package com.jsk.galaxyfilemanager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileOutputStream;

public class FileManageWork extends Worker {
    File path;
    public FileManageWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.i("doWork","doWork");
        writeTest();
        if (deleteFile()){
            File files = new File(path.getPath());
            Log.v("doWork","delete.....:");
            for(File f: files.listFiles()){
                Log.v("doWork","after:"+f.getName());
            }
        }

        return Result.success();
    }

    private boolean deleteFile()
    {
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.i("doWork","ServiceStart");
        writeTest();
        File files = new File(path.getPath());
        for(File f: files.listFiles()){
            Log.v("doWork","before:"+f.getName());
            int i = f.getName().lastIndexOf('.');
            if (i > 0) {
                String extension = f.getName().substring(i+1);
                if(extension.equals("pdf"))
                {f.delete();}
            }
        }
        return true;
    }

    private void writeTest()
    {
        File files = new File(path.getPath(),"test.txt");
        try {
            FileOutputStream out = new FileOutputStream(files);
            out.write("test!!!".getBytes());
            out.flush();
            out.close();
        }catch (Exception e){
            Log.i("doWork",e.toString());
        }
    }
}