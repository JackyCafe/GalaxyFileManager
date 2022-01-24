package com.jsk.galaxyfilemanager;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class  MyTask extends TimerTask {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd--HH時mm分ss");
    File path;
    String job;
    public MyTask(File path,String job){
        this.path = path;
        this.job = job;
    }

    @Override
    public void run() {
        File files = new File(path.getPath());
        for(File f: files.listFiles()){
            f.delete();
        }
        StringBuilder sb = new StringBuilder();
        for(File f: files.listFiles()){
            sb.append(f.getName());
        }
        String content = job+" 在時間:" + formatter.format(new Date()) + "完成";//
        Log.v("Jacky",content +"!\n");

    }
}