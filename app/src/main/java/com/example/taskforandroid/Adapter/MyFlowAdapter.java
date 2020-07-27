package com.example.taskforandroid.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.example.taskforandroid.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

//标签适配器
public class MyFlowAdapter extends TagAdapter<String> {

    private Context context;

    public MyFlowAdapter(Context context, List<String> datas) {
        super(datas);
        this.context=context;
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TextView tv = (TextView) inflater.inflate(R.layout.dialog_label_item, parent, false);
        tv.setText(s);
        return tv;
    }


}