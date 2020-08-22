package com.example.taskforandroid.KeepLive.onePx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.taskforandroid.KeepLive.MyReceiver;

import java.lang.ref.WeakReference;

public class KeepManager {
    private static final KeepManager mInstance = new KeepManager();

    private MyReceiver myReceiver;

    private WeakReference<Activity> mKeepActivity;

    private KeepManager() {}

    public static KeepManager getInstance() {
        return mInstance;
    }


    public void startKeep(Context context) {
        Intent intent = new Intent(context, PxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void finishKeep() {
        if (mKeepActivity != null) {
            Activity activity = mKeepActivity.get();
            if (activity != null) {
                activity.finish();
            }
            mKeepActivity = null;
        }
    }

    public void setKeepActivity(Activity activity) {
        this.mKeepActivity = new WeakReference<>(activity);
    }
}