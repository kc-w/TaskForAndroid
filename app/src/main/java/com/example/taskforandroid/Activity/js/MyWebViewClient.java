package com.example.taskforandroid.Activity.js;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


//WebViewClient主要帮助WebView处理各种通知、请求事件；
public class MyWebViewClient extends WebViewClient {




    @Override//网页加载完成回调
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        Log.e("TAG", "网页加载完成" );
        //调用javascript方法
        view.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++){" +
                "    objs[i].onclick=function() {" +
                "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                "    }  " +
                "}" +
                "})()");


    }



    @Override//开始加载网络，这个函数只会被调用一次，当网页内内嵌的frame 发生改变时也不会调用onPageStarted
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }


}