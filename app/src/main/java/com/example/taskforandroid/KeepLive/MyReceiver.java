package com.example.taskforandroid.KeepLive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.taskforandroid.KeepLive.onePx.KeepManager;
import com.igexin.sdk.PushManager;

//接收广播
public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_OFF)) {


            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);

            KeepManager.getInstance().startKeep(context);

            Log.e(getClass().getSimpleName(), "锁屏.....");


        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {


            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);

            KeepManager.getInstance().finishKeep();

            Log.e(getClass().getSimpleName(), "解锁.....");

        }else if(action.equals(Intent.ACTION_USER_PRESENT)){

            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);

            Log.e(getClass().getSimpleName(), "开屏.....");
        }


    }

}
