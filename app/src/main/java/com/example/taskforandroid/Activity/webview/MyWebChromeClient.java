package com.example.taskforandroid.Activity.webview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.VolumeShaper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toolbar;

public class MyWebChromeClient extends WebChromeClient {

    WebChromeClient.CustomViewCallback mCallback;

    View scrollView;
    View toolbar;
    FrameLayout fullVideo;

    private View customView = null;

    Activity context=null;


    public MyWebChromeClient(Activity context, View view1,View view2,View view3){

        this.context=context;
        fullVideo= (FrameLayout) view1;
        this.scrollView= view2;
        this.toolbar=  view3;

    }

    @Override
    public void onHideCustomView() {
        fullScreen();

        //退出全屏
        if (mCallback != null){
            mCallback.onCustomViewHidden();
        }
        toolbar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        fullVideo.removeAllViews();
        fullVideo.setVisibility(View.GONE);

        super.onHideCustomView();


    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        fullScreen();

        toolbar.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        fullVideo.setVisibility(View.VISIBLE);
        fullVideo.addView(view);

        mCallback = callback;

        super.onShowCustomView(view, callback);
    }

    private void fullScreen(){
        if (context.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }



}
