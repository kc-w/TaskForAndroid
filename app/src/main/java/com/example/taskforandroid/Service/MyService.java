package com.example.taskforandroid.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.example.taskforandroid.R;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyService extends GTIntentService {


    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public MyService() {
    }




    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override//接收 cid
    public void onReceiveClientId(Context context, String s) {

        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = preferences.edit();


        editor.putString("cid",s);
        editor.commit();





    }

    @Override//处理透传消息
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {

    }

    @Override//cid 离线上线通知
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override//各种事件处理回执
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }


    @Override//通知到达，只有个推通道下发的通知会回调此⽅法
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {


        Log.e(TAG, "收到通知!" );





    }

    @Override//通知点击，只有个推通道下发的通知会回调此⽅法
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {



        Log.e(TAG, "点击通知!" );

    }

    public void ToastMeaagge(String msg){
        Looper.prepare();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


}
