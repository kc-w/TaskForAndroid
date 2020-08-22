package com.example.taskforandroid.KeepLive.JobSchedule;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.igexin.sdk.PushManager;

//定时执行任务
public class MyJobService extends JobService {


    private static final String TAG = "MyJobService";
    public static final int PERIODIC_TIME = 10 * 1000;
    private int jobId;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "JobScheduler拉活开启");

        //初始化个推
        PushManager.getInstance().initialize(this);

        JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (mJobScheduler != null) {

            mJobScheduler.cancel(jobId);
            JobInfo.Builder builder = new JobInfo.Builder(startId++, new ComponentName(getPackageName(), MyJobService.class.getName()));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //执行的最小延迟时间
                builder.setMinimumLatency(PERIODIC_TIME);
                //执行的最长延时时间
                builder.setOverrideDeadline(PERIODIC_TIME);
                //线性重试方案
                builder.setBackoffCriteria(PERIODIC_TIME, JobInfo.BACKOFF_POLICY_LINEAR);
            } else {
                //每隔5秒运行一次
                builder.setPeriodic(PERIODIC_TIME);
                builder.setRequiresDeviceIdle(true);
            }
            builder.setRequiresCharging(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            //设置设备重启后，是否重新执行任务
            builder.setPersisted(true);
            jobId = mJobScheduler.schedule(builder.build());

            if (jobId <= 0) {
                Log.e(TAG, "拉活失败:"+jobId);
            } else {
                Log.e(TAG, "拉活成功:"+jobId);
            }
        }

        return START_STICKY;

    }



}