package com.example.taskforandroid.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.taskforandroid.Adapter.MyFlowAdapter;
import com.example.taskforandroid.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class LabelDialog extends Dialog {



    //左,右按钮
    TextView LeftBtn;
    TextView RightBtn;
    TagFlowLayout tagFlowLayout;
    ArrayList<String> names = new ArrayList<>();

    Set<Integer> selectedList;

    public LabelDialog(Context context, int style,ArrayList<String> names) {
        super(context, style);
        this.names=names;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 是否可以撤销
//        this.setCancelable(false);
        //装入界面
        setContentView(R.layout.dialog_label);

        LeftBtn = findViewById(R.id.dialog1_button1);
        RightBtn =  findViewById(R.id.dialog1_button2);
        tagFlowLayout = findViewById(R.id.id_flowlayout);

        //点击弹框外的区域关闭
        setCanceledOnTouchOutside(false);


        initData();


    }



    private void initData() {
        MyFlowAdapter adapter=new MyFlowAdapter(getContext(),names);
        tagFlowLayout.setAdapter(adapter);
        //点击时的回调方法
        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {

                Log.e("TAG", "onTagClick"+position);

                return true;
            }
        });
        //点击时回调所选中的集合
        tagFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                selectedList=selectPosSet;
            }
        });
//        //预先设置选中
//        adapter.setSelectedList(1,3,5,7,8,9);

    }

    public List<String> getNames(){

        //选中的下标集合
        List<String> select_names = new ArrayList();
        if (selectedList!=null){
            for (int i:selectedList){

                select_names.add(names.get(i));
                if("全体员工".equals(names.get(i))){
                    break;
                }
            }
        }

        return select_names;

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



    @Override
    public void onBackPressed() {
        dismiss();
    }




}
