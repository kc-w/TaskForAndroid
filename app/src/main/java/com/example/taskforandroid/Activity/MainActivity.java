package com.example.taskforandroid.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.taskforandroid.Adapter.FragmentAdapter;
import com.example.taskforandroid.Adapter.task_item_Adapter;
import com.example.taskforandroid.Bean.Task;
import com.example.taskforandroid.Dialog.ChangePWDialog;
import com.example.taskforandroid.Fragment.*;
import com.example.taskforandroid.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.*;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity {



    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FragmentManager fm;
    private List<Fragment> fragmentlist;
    private FragmentAdapter adapter;
    private SearchView searchView;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(false);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        //实例化,加载viewpager的fragment
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //页面，数据源
        fragmentlist = new ArrayList<>();
        fragmentlist.add(new Fragment1());
        fragmentlist.add(new Fragment2());
        fragmentlist.add(new Fragment3());
        fragmentlist.add(new Fragment4());
        fragmentlist.add(new Fragment5());
        fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm,fragmentlist);

        //为viewpager设置适配器
        mViewPager.setAdapter(adapter);
        //绑定适配器
        mTabLayout.setupWithViewPager(mViewPager);
        ///MODE_SCROLLABLE可滑动的展示
        //MODE_FIXED固定展示
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);







        preferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = preferences.edit();


        checkNewVersion();

    }


    //检查更新
    public void checkNewVersion(){

        String Url=getResources().getString(R.string.url)+"/CheckNewVersionServlet";
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
                ToastMeaagge("网络异常,登录失败！");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //得到内容
                String data=response.body().string();
                new_version=Integer.valueOf(data);

                Message msg=new Message();
                msg.what=2;
                handler.sendMessage(msg);


            }
        });

    }

    int new_version=0;

    public void download(){

        long old_version = 0;
        try {
            PackageManager packageManager = getPackageManager();
            //getPackageName()是你当前程序的包名
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                old_version = packInfo.versionCode;
            } else{
                old_version = packInfo.getLongVersionCode();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoSuchMethodError e){
            e.printStackTrace();
        }

        if (new_version>old_version){



            AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                    .setTitle("更新提示")
                    .setMessage("有新版本!")
                    .setIcon(R.drawable.ico)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            String url = getResources().getString(R.string.url)+"/apk/TaskForAndroid.apk";

                            //判断安卓版本是否大于8
                            NotificationChannel channel = new NotificationChannel("download", "下载进度", NotificationManager.IMPORTANCE_HIGH);
                            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);

                            //初始化通知ui
                            final Notification.Builder nb = new Notification.Builder(context, "download");
                            nb.setSmallIcon(R.drawable.ico).setContentTitle("下载中");
                            nb.setProgress(100, 0, false).setAutoCancel(true);

                            final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            //创建通道，注意这一步很重要
                            manager.createNotificationChannel(channel);

                            //使用okhttp发送get请求
                            OkHttpClient client = new OkHttpClient();
                            Request.Builder builder = new Request.Builder();
                            Request request = builder.url(url).get().build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    InputStream inputStream = response.body().byteStream();
                                    //设置缓存文件放在/storage/sdcard/Android/data/<应用包名>/cache目录下
                                    File file = new File(getExternalCacheDir(), "TaskForAndroid.apk");
                                    FileOutputStream fos = new FileOutputStream(file);

                                    //目标文件的总大小
                                    long contentLength = response.body().contentLength();

                                    int length = 0;
                                    //字节数组缓冲区要设置为足够大，过小会导致io速度慢
                                    byte[] bytes = new byte[10240];
                                    while ((length = inputStream.read(bytes)) != -1) {
                                        fos.write(bytes, 0, length);

                                        //已经写入的文件内容所占百分数
                                        double percent = fos.getChannel().position() * 100.0 / contentLength;
                                        //手动刷新进度条
                                        nb.setProgress(100, (int) percent, false);
                                        manager.notify(1, nb.build());
                                    }

                                    fos.flush();
                                    fos.close();
                                    response.body().close();
                                    //下载完毕关闭要隐藏通知
                                    manager.cancel(1);

                                    //调出系统安装应用页面
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    //fileprovider要在清单文件中声明
                                    Uri contentUri = FileProvider.getUriForFile(getApplication(), "fileProvider", file);
                                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                    startActivity(intent);
                                }
                            });

                        }
                    })

                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            alertDialog1.show();




        }


    }





    //重写方法,如果返回true,菜单将被显示出来,如果返回false菜单将不被显示
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu1,menu);
        return true;
    }




    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:


                case 2:
                    download();
                    break;

            }
        }
    };

    //重写菜单点击监听
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){

            case R.id.search_history://新增任务
                //绑定要启动的Activity对象
                intent =new Intent(this, SearchActivity.class);
                //启动Activity
                startActivity(intent);
                break;


            case R.id.add_task://新增任务
                //绑定要启动的Activity对象
                intent =new Intent(this, EditActivity.class);
                //启动Activity
                startActivity(intent);
                break;

            case R.id.changePW://修改密码


                final ChangePWDialog dialog = new ChangePWDialog(this,R.style.Dialog1);
                dialog.show();
                dialog.setLeftButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final EditText hint1 =  dialog.findViewById(R.id.dialog1_hint1);
                        final EditText hint2 =  dialog.findViewById(R.id.dialog1_hint2);
                        final String oldPW = hint1.getText().toString().trim();
                        final String newPW = hint2.getText().toString().trim();

                        if (newPW.length()<5){
                            Toast.makeText(context,"设置失败，新密码长度过短！", Toast.LENGTH_SHORT).show();

                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //得到存储的sessionid
                                    String sessionid= preferences.getString("sessionid","null");

                                    //创建一个OkHttpClient对象
                                    OkHttpClient client=new OkHttpClient();

                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("oldPW", oldPW)
                                            .addFormDataPart("newPW", newPW)
                                            .build();

                                    //构建请求
                                    Request request=new Request.Builder()
                                            .url(getResources().getString(R.string.url)+"/ChangeUserServlet")
                                            .addHeader("cookie",sessionid)
                                            .post(requestBody)
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

                                            ToastMeaagge(data);
                                        }

                                    });

                                }
                            }).start();

                            finish();
                        }


                    }
                });

                dialog.setRightButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;


            case R.id.out://忘记账号
                editor.clear().commit();
                finish();
                break;




        }
        return true;
    }


    static {//static 代码段可以防止内存泄露
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.black, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
//            }
//        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();

                }
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            exit();
            //不执行父类点击事件
            return true;
        }
        //继续执行父类其他点击事件
        return super.onKeyUp(keyCode, event);
    }

    long time = 0;

    //退出方法
    private void exit() {
        //如果在两秒大于2秒
        if (System.currentTimeMillis() - time > 2000) {
            //获得当前的时间
            time = System.currentTimeMillis();
            Toast.makeText(this, "再点击一次退出应用程序", Toast.LENGTH_SHORT).show();
        } else {
            //点击在两秒以内
            ActivityCollector.finishAll();

        }
    }



}
