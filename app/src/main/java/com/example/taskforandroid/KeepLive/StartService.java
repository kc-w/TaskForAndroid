package com.example.taskforandroid.KeepLive;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.taskforandroid.KeepLive.MyReceiver;
import com.igexin.sdk.PushManager;

//重启服务
public class StartService extends Service {



    MyReceiver myReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //动态注册广播
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        this.registerReceiver(myReceiver, filter);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        PushManager.getInstance().initialize(this);


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        PushManager.getInstance().initialize(this);
        unregisterReceiver(myReceiver);
    }
}
