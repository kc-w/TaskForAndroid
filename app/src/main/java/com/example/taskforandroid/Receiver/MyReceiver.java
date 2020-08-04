package com.example.taskforandroid.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.taskforandroid.Activity.PxActivity;
import com.example.taskforandroid.Service.MyService;
import com.example.taskforandroid.Service.StartService;
import com.example.taskforandroid.Service.TopService;
import com.igexin.sdk.PushManager;

//接收广播
public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e(getClass().getSimpleName(), "锁屏.....");

            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);


            Intent it = new Intent(context, PxActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);


        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.e(getClass().getSimpleName(), "解锁.....");

            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);


            context.sendBroadcast(new Intent("finish"));
            Intent main = new Intent(Intent.ACTION_MAIN);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            main.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(main);

        }else if(action.equals(Intent.ACTION_USER_PRESENT)){
            Log.e(getClass().getSimpleName(), "开屏.....");

            PushManager.getInstance().initialize(context);

            Intent service2 = new Intent(context, StartService.class);
            context.startService(service2);
        }


    }

}
