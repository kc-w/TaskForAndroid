package com.example.taskforandroid.KeepLive.JobSchedule;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.e(TAG, "JobScheduler拉活开启");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startJob(this);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void startJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        setPersisted 在设备重启依然执行
        JobInfo.Builder builder = new JobInfo.Builder(8, new ComponentName(context
                .getPackageName(), MyJobService.class.getName())).setPersisted(true);
        //小于7.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            builder.setPeriodic(1000);
        } else {
            builder.setMinimumLatency(1000);
        }
        assert jobScheduler != null;
        jobScheduler.schedule(builder.build());
    }


}