package com.example.taskforandroid.Activity.ImageGetter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import com.example.taskforandroid.Activity.EditActivity;
import com.example.taskforandroid.Activity.ItemActivity;
import com.example.taskforandroid.Activity.MainActivity;
import org.xml.sax.XMLReader;

import java.util.Locale;

public class MyTagHandler implements Html.TagHandler {
    Context mContext;

    public MyTagHandler(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

        // 处理标签<img>  
        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
            // 获取长度  
            int len = output.length();
            // 获取图片地址  
            ImageSpan[] images = output.getSpans(len-1, len, ImageSpan.class);
            String imgURL = images[0].getSource();

            // 使图片可点击并监听点击事件  
            output.setSpan(new ClickableImage(mContext, imgURL), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class ClickableImage extends ClickableSpan {

        private String url;
        private Context context;

        public ClickableImage(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {

            //绑定要启动的Activity对象
            Intent intent =new Intent(mContext, BigImgActivity.class);
            //得到bundle对象
            Bundle bundle = new Bundle();
            //保存字符串
            bundle.putString("imgURL", url);
            //通过intent将bundle传到另个Activity
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //启动Activity
            mContext.startActivity(intent);

        }
    }
}

