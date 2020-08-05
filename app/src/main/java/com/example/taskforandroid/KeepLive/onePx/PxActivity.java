package com.example.taskforandroid.KeepLive.onePx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.taskforandroid.Activity.BaseActivity;


public class PxActivity extends BaseActivity {


    private static final String TAG = "SinglePixelActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "启动1像素Activity");
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 1;
        params.height = 1;
        params.x= 0;
        params.y = 0;
        window.setAttributes(params);
        KeepManager.getInstance().setKeepActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "关闭1像素Activity");
    }
}