package com.example.taskforandroid.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.taskforandroid.Adapter.task_item_Adapter;
import com.example.taskforandroid.Bean.Task;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.Dialog.SaveDialog;
import com.example.taskforandroid.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener{

    // 用来装日期的
    private Calendar calendar;
    //时间选择弹窗
    private DatePickerDialog dialog;

    Context context;

    LinearLayout l1;
    LinearLayout l2;

    SharedPreferences preferences;

    EditText e1;
    EditText e2;
    EditText e3;
    EditText e41;
    EditText e42;
    Button button;



    //参数及对象
    public List<TaskAndUser> taskAndUserList=new ArrayList<TaskAndUser>();

    //列表控件
    public RecyclerView recyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initView();

    }

    public void initView(){

        context=this;

        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);

        recyclerview= findViewById(R.id.recyclerviewSearch);
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        e41 = findViewById(R.id.e41);
        e42 = findViewById(R.id.e42);
        button = findViewById(R.id.submit_search);

        e41.setOnClickListener(this);
        e42.setOnClickListener(this);
        button.setOnClickListener(this);

    }


    //重写菜单点击监听
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

        }
        return true;


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


            //监听日期点击
            case R.id.e41:
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                e41.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;

            case R.id.e42:
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view,int year,int monthOfYear,int dayOfMonth) {
                                e42.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;

            case R.id.submit_search:


                String task_name = e1.getText().toString();
                String start_name = e2.getText().toString();
                String agree_name = e3.getText().toString();
                String start_time = e41.getText().toString();
                String end_time = e42.getText().toString();


                String sql="select table1.* from "+
                        "(select "+
                        "task.id as a1,task.name as a2,task.content as a3,task.start_id as a4,task.start_time as a5,task.preset_time as a6,"+
                        "task.execute_people as a7,task.assist_people as a8,task.agree_id as a9,task.agree_time as a10,task.finish_time as a11,task.state as a12,"+
                        "user.id as b1,user.name as b2,user.number as b3,user.password as b4,user.department as b5,user.permission as b6,"+
                        "user1.id as c1,user1.name as c2,user1.number as c3,user1.password as c4,user1.department as c5,user1.permission as c6 "+
                        "from task,user,user as user1 where task.start_id = user.id and task.agree_id = user1.id)  as table1 "+
                        "where (1 = 1)";

                if(!task_name.equals("")) {
                    sql = sql + " and ( table1.a2 = '"+task_name+"')";
                }

                if(!start_name.equals("")){
                    sql = sql + " and ( table1.b2 = '"+start_name+"')";
                }

                if(!agree_name.equals("")){
                    sql = sql + " and ( table1.c2 = '"+agree_name+"')";
                }

                if(!start_time.equals("") && !end_time.equals("")){
                    sql = sql + " and (table1.a5 between '"+start_time+"' and '"+end_time+"')";
                }else if(start_time.equals("") && !end_time.equals("")){
                    sql = sql + " and (table1.a5 <= '"+end_time+"')";
                }else if (!start_time.equals("") && end_time.equals("")){
                    sql = sql + " and (table1.a5 >= '"+start_time+"')";
                }else {

                }



                //得到存储的sessionid
                String sessionid= preferences.getString("sessionid","null");

                //创建一个OkHttpClient对象
                OkHttpClient client=new OkHttpClient();
                //设置发送头,发送的数据类型
                MediaType mediaType=MediaType.parse("application/json; charset=utf-8");
                //构建请求
                Request request=new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/SearchServlet")
                        .addHeader("cookie",sessionid)
                        .post(RequestBody.create(sql, mediaType ))
                        .build();

                Call call = client.newCall(request);
                //执行请求,并产生回调
                call.enqueue(new Callback() {
                    @Override//回调失败
                    public void onFailure(Call call, IOException e) {
                        ToastMeaagge("网络异常!");
                    }

                    @Override//回调成功
                    public void onResponse(Call call, Response response) throws IOException {

                        String data=response.body().string();
                        jsonJXDate(data);
                    }

                });

                break;
        }
    }

    //解析服务器返回的json数据加入到数据列表
    private void jsonJXDate(String data) {

        if(data!=null && data.length()>3) {
            Gson gson = new Gson();

            taskAndUserList.clear();
            taskAndUserList = gson.fromJson(data, new TypeToken<List<TaskAndUser>>() {}.getType());

            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);
        }else {

            ToastMeaagge("未查询到数据!");

        }
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:



                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.VISIBLE);

                    //实现列表显示控制
                    StaggeredGridLayoutManager layoutManager = new
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                    //对象数组装入适配器
                    task_item_Adapter adapter=new task_item_Adapter(taskAndUserList,context);
                    //设置布局显示格式
                    recyclerview.setLayoutManager(layoutManager);
                    recyclerview.setAdapter(adapter);
                    break;

            }
        }
    };



}
