package com.example.taskforandroid.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.taskforandroid.Service.TopService;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e(getClass().getSimpleName(), "锁屏.....");
            Intent service = new Intent(context, TopService.class);
            context.startService(service);
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.e(getClass().getSimpleName(), "解锁.....");
            Intent service = new Intent(context, TopService.class);
            context.startService(service);
        }else if(action.equals(Intent.ACTION_USER_PRESENT)){
            Log.e(getClass().getSimpleName(), "开屏.....");
            Intent service = new Intent(context, TopService.class);
            context.startService(service);
        }


    }

}
