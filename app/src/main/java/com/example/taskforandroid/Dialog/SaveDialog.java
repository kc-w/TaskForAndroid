package com.example.taskforandroid.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.taskforandroid.R;

//删除数据提示框
public class SaveDialog extends Dialog {


    //提示
    protected TextView hint1;
    //左,右按钮
    protected TextView LeftBtn;
    protected TextView RightBtn;

    //创建弹窗对象传入context和style样式
    public SaveDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 是否可以撤销
        this.setCancelable(false);
        //装入界面
        setContentView(R.layout.dialog_save);
        hint1 = (TextView) findViewById(R.id.dialog1_hint1);
        LeftBtn = (TextView) findViewById(R.id.dialog1_button1);
        RightBtn = (TextView) findViewById(R.id.dialog1_button2);
        //点击弹框外的区域关闭
        setCanceledOnTouchOutside(false);
    }

    //设置文字和点击事件
    public void setRightButton(String rightStr, View.OnClickListener clickListener) {
        RightBtn.setOnClickListener(clickListener);
        RightBtn.setText(rightStr);
    }

    public void setLeftButton(String leftStr, View.OnClickListener clickListener) {
        LeftBtn.setOnClickListener(clickListener);
        LeftBtn.setText(leftStr);
    }

    //设置提示内容
    public void setHintText(String str) {
        hint1.setText(str);
        hint1.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }
}