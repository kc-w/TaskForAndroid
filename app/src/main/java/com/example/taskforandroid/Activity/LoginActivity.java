package com.example.taskforandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.taskforandroid.Dialog.LoadingDialog;
import com.example.taskforandroid.Dialog.SaveDialog;
import com.example.taskforandroid.KeepLive.JobSchedule.MyJobService;
import com.example.taskforandroid.R;
import com.example.taskforandroid.KeepLive.backgroundMusic.MusicService;
import com.example.taskforandroid.KeepLive.StartService;
import com.example.taskforandroid.Tool.MobileInfoUtils;
import com.example.taskforandroid.KeepLive.onePx.KeepManager;
import com.igexin.sdk.PushManager;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    EditText edit1;
    EditText edit2;

    LoadingDialog.Builder builder;
    LoadingDialog dialog;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //读取登录历史信息userinfo
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = preferences.edit();


        //初始化个推
        PushManager.getInstance().initialize(this);

        //开启服务注册广播MyReceiver监听锁屏，解锁
        Intent intent1 = new Intent(this, StartService.class);
        startService(intent1);

        //启动后台服务播放音乐
//        Intent intent2 = new Intent(this, MusicService.class);
//        startService(intent2);



        //开启JobSchedule定期拉活
//        Intent intent3 = new Intent(this, MyJobService.class);
//        startService(intent3);



        //关闭电池优化
        closeBatteryCapacity();

        //检测是否开启通知权限
        if (!isNotificationEnabled()) {
            gotoSet();
        }




        String autoStart = preferences.getString("autoStart",null);

        if (autoStart==null){
            //开启自动启动权限
            autoStart();
        }









//        AppOpsManager ops = (AppOpsManager) this.getSystemService(APP_OPS_SERVICE);
//
//        Field[]  fields = ops.getClass().getDeclaredFields();
//
//        for (int i=0;i<fields.length;i++){
//
//
//            if ("int".equals(fields[i].getType().toString()) && Modifier.isStatic(fields[i].getModifiers())){
//                try {
//                    Log.e("TAG", fields[i].getName()+" : "+fields[i].get(null));
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }





        context=this;

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar_login);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(false);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Button button = findViewById(R.id.login_button);

        edit1 = findViewById(R.id.number);
        edit2 = findViewById(R.id.password);





        String number = preferences.getString("number","");
        String password = preferences.getString("password","");


        if(!"".equals(number)){

            edit1.setText(number);
            edit2.setText(password);

            //进入应用判断账号密码是否正确
            okhttpDate(number,password);

        }

        //等待登录弹窗
        builder=new LoadingDialog.Builder(this);
        //登录弹窗
        builder.setMessage("加载中...").setCancelable(false);
        dialog=builder.create();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                //获取登录用户名及密码
                okhttpDate(edit1.getText().toString(),edit2.getText().toString());
            }
        });
    }





    //关闭电池优化
    private void closeBatteryCapacity(){

        PowerManager pm  = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())){
                Intent intent = new Intent();
                String packageName = getPackageName();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                this.startActivity(intent);
            }
        }

    }




    //检测是否开启通知权限
    private boolean isNotificationEnabled() {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(this).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;

    }

    //跳转开启通知权限
    private void gotoSet() {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void autoStart(){


        final SaveDialog dialog = new SaveDialog(this,R.style.Dialog1);
        dialog.show();
        dialog.setHintText("正常接收消息推送需要开启自动启动权限");

        dialog.setLeftButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setRightButton("前往开启", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileInfoUtils.jumpStartInterface(context);
                editor.putString("autoStart","1");
                editor.commit();
                dialog.dismiss();
            }
        });




    }







    //像服务器发出登录请求并获得回传数据
    public void okhttpDate(String number, String password) {
        String Url=getResources().getString(R.string.url)+"/LoginServlet?number="+number+"&password="+password;
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)//连接超时
                        .writeTimeout(2, TimeUnit.SECONDS)//上传超时时间
                        .readTimeout(2, TimeUnit.SECONDS)//下载超时时间
                        .build();
                Request request=new Request.Builder()
                        .url(Url)
                        .build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            dialog.dismiss();
                            ToastMeaagge("网络异常,登录失败！");

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            dialog.dismiss();
                            //得到session
                            Headers headers =response.headers();

                            List cookies = headers.values("Set-Cookie");

                            String session = (String) cookies.get(0);
                            String sessionid = session.substring(0,session.indexOf(";"));

                            //得到内容
                            String data=response.body().string();



                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                int id = jsonObject.getInt("id");
                                if (id==-1){
                                    ToastMeaagge("账号或者密码错误！");
                                }else {
                                    String name = jsonObject.getString("name");
                                    String number = jsonObject.getString("number");
                                    String password = jsonObject.getString("password");
                                    String department = jsonObject.getString("department");
                                    int permission = jsonObject.getInt("permission");
                                    //发送跳转信息
                                    gotoMain(id,name,number,password,department,permission,sessionid);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });


    }

    //跳转
    public void gotoMain(int id,String name,String number,String password,String department,int permission,String sessionid){

        dialog.dismiss();



        //登录成功的话保存用户名和密码,以及session
        editor.putInt("id",id);
        editor.putString("name",name);
        editor.putString("number",number);
        editor.putString("password",password);
        editor.putString("department",department);
        editor.putInt("permission",permission);
        editor.putString("sessionid",sessionid);
        editor.commit();

        String cid = preferences.getString("cid","null");

        if ("null".equals(cid) || cid == null){

            ToastMeaagge("SDK异常,退出后重新进入!");

        }else {

            OkHttpClient client=new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("cid", cid)
                    .build();

            //构建请求
            Request request=new Request.Builder()
                    .url(getResources().getString(R.string.url)+"/ChangeUserServlet")
                    .addHeader("cookie",sessionid)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastMeaagge("网络异常!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String data=response.body().string();

                    //绑定要启动的Activity对象
                    Intent intent =new Intent(context, MainActivity.class);
                    //启动Activity
                    startActivity(intent);
                }
            });

        }







    }

    public void ToastMeaagge(String msg){
        Looper.prepare();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }



}
