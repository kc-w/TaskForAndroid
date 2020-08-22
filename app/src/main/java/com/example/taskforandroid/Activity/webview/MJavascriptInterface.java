package com.example.taskforandroid.Activity.webview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.example.taskforandroid.Activity.BigImgActivity;
import com.example.taskforandroid.Activity.ProgressActivity;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

//js调用java方法
public class MJavascriptInterface {
    private Context context;
    private String [] imageUrls;
    private int task_id;

    public MJavascriptInterface(Context context,String[] imageUrls,int task_id) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.task_id=task_id;
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

    @android.webkit.JavascriptInterface
    public void changeProgress(String html,String progresshtml) {


//        String str = taskAndUser.getTask().getContent();
//        String regex = "<p style=\"border:1px solid #000000;\">汇报人:"+name+"(.*?)"+"</br>";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//
//        String replace="";
//
//        while (matcher.find()) {
//            replace=matcher.group(0);
//        }
//
//        if (!"".equals(replace) ){
//
//            button7.setVisibility(View.VISIBLE);
//
//        }else {
//            button7.setVisibility(View.GONE);
//        }




        SharedPreferences preferences = context.getSharedPreferences("userinfo", MODE_PRIVATE);
        String name = preferences.getString("name","");



        if (html.indexOf(name)>-1){

            Log.e("TAG", html);
            Log.e("TAG", progresshtml);

            Intent intent = new Intent();
            intent.putExtra("task_id", task_id);
            intent.putExtra("html", html);
            intent.putExtra("progresshtml", progresshtml);
            intent.setClass(context, ProgressActivity.class);
            context.startActivity(intent);


        }

    }



}