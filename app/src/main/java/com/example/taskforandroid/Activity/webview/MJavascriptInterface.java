package com.example.taskforandroid.Activity.webview;

import android.content.Context;
import android.content.Intent;

import com.example.taskforandroid.Activity.BigImgActivity;

public class MJavascriptInterface {
    private Context context;
    private String [] imageUrls;

    public MJavascriptInterface(Context context,String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent();
        //所有图片地址
        intent.putExtra("ImageUrls", imageUrls);
        //当前图片地址
        intent.putExtra("thisImageUrl", img);
        intent.setClass(context, BigImgActivity.class);
        context.startActivity(intent);
    }


}