package com.jsk.galaxyfilemanager;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileJobService extends JobService {
    private static final int MESSAGE_ID = 100;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

//        new SimpleDownloadTask().execute(jobParameters);
        mJobHandler.sendMessage(Message.obtain(mJobHandler, MESSAGE_ID, jobParameters));
//        jobFinished(jobParameters, true);
        return true;
    }

    @SuppressLint("SpecifyJobSchedulerIdRange")
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
//        mJobHandler.removeMessages(MESSAGE_ID);
        mJobHandler.removeCallbacksAndMessages(null);
        return false;
    }
//
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    private Handler mJobHandler = new Handler
            (new Handler.Callback()
            { @Override
            public boolean handleMessage(Message msg)
            {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd--HH時mm分ss");
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File files = new File(path.getPath());
                for(File f: files.listFiles()){
                    f.delete();
                }


                JobParameters param = (JobParameters) msg.obj;
                jobFinished(param, true);
                String content = " 在時間:" + formatter.format(new Date()) + "完成";//
                Log.v("Jacky", param.getJobId()+":"+content +"!\n");
                return true;
            } });


    private class SimpleDownloadTask extends AsyncTask<JobParameters, Void, Boolean> {
        private JobParameters mJobParam;

        @Override
        protected Boolean doInBackground(JobParameters... params) {
            mJobParam = params[0];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd--HH時mm分sss");
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File files = new File(path.getPath());
            for(File f: files.listFiles()){
                f.delete();
            }

            String content = " " + mJobParam.getJobId() + "。在時間:" + formatter.format(new Date()) + "完成";//
            Log.v("Jacky", content +"!\n");
            return false;

        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            jobFinished(mJobParam, !isSuccess);
        }
    }
}
