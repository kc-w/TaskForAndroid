package com.example.taskforandroid.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.taskforandroid.R;
import com.example.taskforandroid.Receiver.MyReceiver;

public class TopService extends Service {


    private MediaPlayer mMediaPlayer;
    public static final int SERVICE_ID = 100;

    MyReceiver myReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //MediaPlayer播放静音音乐
//        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.test);
//        mMediaPlayer.setLooping(true);



        Log.e(getClass().getSimpleName(), "创建了前台服务");

        //动态注册广播
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        this.registerReceiver(myReceiver, filter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {   //4.3以下
            startForeground(SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {//4.3-7.0
            startForeground(SERVICE_ID, new Notification());
            //通过启动一个inner服务隐藏通知栏
            startService(new Intent(this, InnerService.class));
        } else {    //8.0以上
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("service2", "前台服务", NotificationManager.IMPORTANCE_HIGH);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Notification notification = new NotificationCompat.Builder(this, "service2")
                        .setContentTitle ("状态")
                        .setContentText ("正在运行中,请勿关闭")
                        .setSmallIcon (R.drawable.ico)
                        .setLargeIcon (BitmapFactory.decodeResource (getResources (),R.drawable.ico))
                        .build ();
                startForeground(SERVICE_ID, notification);

            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startPlayMusic();
//            }
//        }).start();
        return START_STICKY;
    }

//    private void startPlayMusic() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.start();
//        }
//    }
//
//    private void stopPlayMusic() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//        }
//    }


    public static class InnerService extends Service{

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e(getClass().getSimpleName(), "InnerService 创建了");
            //在8.0之前创建一个服务id相同的前台服务再取消掉可以将通知栏隐藏掉
            startForeground(SERVICE_ID, new Notification());
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，取消通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(SERVICE_ID);
        }
        unregisterReceiver(myReceiver);
//        stopPlayMusic();
    }
}