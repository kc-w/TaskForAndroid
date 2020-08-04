package com.example.taskforandroid.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.example.taskforandroid.Adapter.task_item_Adapter;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment2 extends Fragment {


    SharedPreferences preferences;


    //session_id
    public String sessionid;

    //参数及对象
    public List<TaskAndUser> taskAndUserList=new ArrayList<TaskAndUser>();

    //列表控件
    public RecyclerView recyclerview;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_2, container, false);
        return view;
    }

    @Override//当Fragment所在的Activity启动完成后调用
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //得到列表组件
        recyclerview= getActivity().findViewById(R.id.recyclerview2);
        preferences = getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        sessionid= preferences.getString("sessionid","");


        RefreshLayout refreshLayout = (RefreshLayout)getActivity().findViewById(R.id.smartLayout2);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                okhttpDate();
                refreshlayout.finishRefresh(100);

            }
        });
//        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadmore(2000);
//            }
//        });

    }


    @Override//fragment可见时调用
    public void onStart() {
        super.onStart();
        //请求数据
        okhttpDate();


    }



    public void okhttpDate() {



        new Thread(new Runnable() {
            @Override
            public void run() {
                preferences = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
                String sessionid= preferences.getString("sessionid","null");
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder()
                        .addHeader("cookie",sessionid)
                        .url(getActivity().getResources().getString(R.string.url)+"/SelectTaskServlet?task_state=待批准")
                        .get()
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastMeaagge("网络异常,登录失败！");

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String data=response.body().string();
                        jsonJXDate(data);


                    }
                });

            }
        }).start();

    }

    //解析服务器返回的json数据加入到数据列表
    private void jsonJXDate(String date) {

        Gson gson = new Gson();

        taskAndUserList.clear();
        taskAndUserList = gson.fromJson(date, new TypeToken<List<TaskAndUser>>() {}.getType());

        Message msg=new Message();
        msg.what=1;
        handler.sendMessage(msg);

    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //实现列表显示控制
                    StaggeredGridLayoutManager layoutManager = new
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    //对象数组装入适配器
                    task_item_Adapter adapter=new task_item_Adapter(taskAndUserList,getActivity());
                    //设置布局显示格式
                    recyclerview.setLayoutManager(layoutManager);
                    recyclerview.setAdapter(adapter);
                    break;

            }
        }
    };

    //提示网络异常
    public void ToastMeaagge(String msg){
        Looper.prepare();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }


}