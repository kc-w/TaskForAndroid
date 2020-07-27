package com.example.taskforandroid.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.taskforandroid.Activity.ImageGetter.MyImageGetter;
import com.example.taskforandroid.Activity.ImageGetter.MyTagHandler;
import com.example.taskforandroid.Bean.Task;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.R;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class ItemActivity extends BaseActivity {


    TextView item1_tv;
    TextView item2_tv;
    TextView item3_tv;
    TextView item4_tv;
    TextView item5_tv;
    TextView item51_tv;
    TextView item6_tv;
    TextView item7_tv;
    TextView item8_tv;
    TextView item9_tv;

    int id;
    TaskAndUser taskAndUser;

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;

    MyImageGetter myImageGetter;
    MyTagHandler tagHandler;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar_item);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        //读取intent的数据给bundle对象
        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getInt("task_id");

        item1_tv=findViewById(R.id.item1_tv);
        item2_tv=findViewById(R.id.item2_tv);
        item3_tv=findViewById(R.id.item3_tv);
        item4_tv=findViewById(R.id.item4_tv);
        item5_tv=findViewById(R.id.item5_tv);
        item51_tv = findViewById(R.id.item51_tv);
        item6_tv=findViewById(R.id.item6_tv);
        item7_tv=findViewById(R.id.item7_tv);
        item8_tv=findViewById(R.id.item8_tv);
        item9_tv=findViewById(R.id.item9_tv);
        myImageGetter = new MyImageGetter(this, item9_tv);
        tagHandler = new MyTagHandler(this);

        //批准
        button1=findViewById(R.id.agree);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpUpdate("agree");
            }
        });
        //不批准
        button2=findViewById(R.id.noagree);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpUpdate("noagree");
            }
        });
        //完成
        button3=findViewById(R.id.finish);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpUpdate("finish");
            }
        });
        //删除
        button4=findViewById(R.id.delete);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpUpdate("delete");
            }
        });

        //确认
        button5=findViewById(R.id.receive);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpUpdate("receive");
            }
        });


        //读取登录历史信息userinfo
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = preferences.edit();


        okhttpDate();

    }




    //重写菜单点击监听
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //监听返回按钮,关闭当前活动
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    //发出请求获得task数据
    public void okhttpUpdate(final String flag) {

        new Thread(new Runnable() {
            //得到存储的sessionid
            String sessionid= preferences.getString("sessionid","null");

            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(getResources().getString(R.string.url)+"/UpStaetServlet?task_id="+id+"&flag="+flag)
                        .addHeader("cookie",sessionid)
                        .get()
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        finish();
                        ToastMeaagge("网络异常!");

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String data=response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            String message = jsonObject.getString("message");
                            ToastMeaagge(message);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                });

            }
        }).start();

        finish();

    }




    //发出请求获得task数据
    public void okhttpDate() {

        new Thread(new Runnable() {
            //得到存储的sessionid
            String sessionid= preferences.getString("sessionid","null");

            @Override
            public void run() {
                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(getResources().getString(R.string.url)+"/SelectTaskServlet?task_id="+id)
                        .addHeader("cookie",sessionid)
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
    private void jsonJXDate(String data) {
        if(data!=null) {

            Gson gson = new Gson();
            taskAndUser = gson.fromJson(data, TaskAndUser.class);

            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);


        }
    }


    Gson gson = new Gson();
    public Handler handler=new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //得到用户权限
                    int permission = preferences.getInt("permission",0);
                    //得到用户id
                    int id = preferences.getInt("id",-1);
                    String name = preferences.getString("name","");



                    item1_tv.setText(taskAndUser.getTask().getName());


                    item2_tv.setText(taskAndUser.getUser().getName());

                    item3_tv.setText(taskAndUser.getTask().getStart_time().substring(0,10));
                    item4_tv.setText(taskAndUser.getTask().getPreset_time().substring(0,10));

                    String prople1=taskAndUser.getTask().getExecute_people();
                    if (prople1.length()!=0){
                        if(prople1.indexOf("全体员工")==-1){
                            item5_tv.setText(prople1.substring(1,prople1.length()-1).replace("\"",""));
                        }else {
                            item5_tv.setText(prople1.substring(1,prople1.length()-1).replace("\"","").substring(0,4));
                        }
                    }
                    String prople2=taskAndUser.getTask().getAssist_people();
                    if (prople2.length()!=0){
                        if(prople2.indexOf("全体员工")==-1){
                            item51_tv.setText(prople2.substring(1,prople2.length()-1).replace("\"",""));
                        }else {
                            item51_tv.setText(prople2.substring(1,prople2.length()-1).replace("\"","").substring(0,4));
                        }

                    }

                    if (taskAndUser.getUser1().getId()==0){
                        item6_tv.setText("等待批准");
                        item7_tv.setText("等待批准");
                    }else {
                        item6_tv.setText(taskAndUser.getUser1().getName());
                        item7_tv.setText(taskAndUser.getTask().getAgree_time().substring(0,10));
                    }


                    item8_tv.setText(taskAndUser.getTask().getState());

                    item9_tv.setText(Html.fromHtml(taskAndUser.getTask().getContent(), myImageGetter, tagHandler));
                    item9_tv.setMovementMethod(LinkMovementMethod.getInstance());



                    if("待批准".equals(taskAndUser.getTask().getState())){
                        if (permission>=taskAndUser.getUser().getPermission() && permission>=2){
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                        }


                        if (id==taskAndUser.getTask().getStart_id()){
                            button4.setVisibility(View.VISIBLE);
                        }

                    }

                    if("进行中".equals(taskAndUser.getTask().getState())){
                        if (!"".equals(taskAndUser.getTask().getExecute_people())){
                            Map execute_map = gson.fromJson(taskAndUser.getTask().getExecute_people(), Map.class);
                            if("未确认".equals(execute_map.get(name)) ){
                                button5.setVisibility(View.VISIBLE);
                            }
                        }

                        if (!"".equals(taskAndUser.getTask().getAssist_people())){
                            Map assist_map = gson.fromJson(taskAndUser.getTask().getAssist_people(), Map.class);
                            if("未确认".equals(assist_map.get(name))){
                                button5.setVisibility(View.VISIBLE);
                            }
                        }

                    }


                    if(id==taskAndUser.getTask().getStart_id() && "进行中".equals(taskAndUser.getTask().getState())){

                        button3.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        }
    };



}
