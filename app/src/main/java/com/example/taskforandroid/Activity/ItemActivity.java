package com.example.taskforandroid.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskforandroid.Activity.webview.MJavascriptInterface;
import com.example.taskforandroid.Activity.webview.MyWebChromeClient;
import com.example.taskforandroid.Activity.webview.MyWebViewClient;
import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.Dialog.ChangePWDialog;
import com.example.taskforandroid.R;
import com.example.taskforandroid.Tool.GetSystemUtils;
import com.example.taskforandroid.View.RichEditorNew;
import com.google.gson.Gson;


import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    RichEditorNew item9_tv;


    //得到用户权限
    int permission ;
    //得到用户id
    int id ;
    String name ;

    int task_id;
    TaskAndUser taskAndUser;

//    Button button1;
//    Button button2;
//    Button button3;
//    Button button4;
//    Button button5;
//    Button button6;
//    Button button7;

    Toolbar toolbar;



//    MyImageGetter myImageGetter;
//    MyTagHandler tagHandler;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);


        context = this;

        //找到标题栏控件
        toolbar=findViewById(R.id.toolbar_item);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        //读取登录历史信息userinfo
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);
        editor = preferences.edit();

        permission = preferences.getInt("permission",0);

        id = preferences.getInt("id",-1);

        name = preferences.getString("name","");




        //读取intent的数据给bundle对象
        Bundle bundle = this.getIntent().getExtras();
        task_id = bundle.getInt("task_id");

        item1_tv=findViewById(R.id.item1_tv);
        item2_tv=findViewById(R.id.item2_tv);
        item3_tv=findViewById(R.id.item3_tv);
        item4_tv=findViewById(R.id.item4_tv);
        item5_tv=findViewById(R.id.item5_tv);
        item51_tv = findViewById(R.id.item51_tv);
        item6_tv=findViewById(R.id.item6_tv);
        item7_tv=findViewById(R.id.item7_tv);
        item8_tv=findViewById(R.id.item8_tv);
        item9_tv=findViewById(R.id.richEditor);


//        myImageGetter = new MyImageGetter(this, item9_tv);
//        tagHandler = new MyTagHandler(this);









    }


    MenuItem item1;
    MenuItem item2;
    MenuItem item3;
    MenuItem item4;
    MenuItem item5;
    MenuItem item6;

    //重写方法,如果返回true,菜单将被显示出来,如果返回false菜单将不被显示
    public boolean onCreateOptionsMenu(Menu menu) {
        //fragment使用menu
        //inflater.inflate(R.menu.yourxml, menu);


        //activity使用menu
        getMenuInflater().inflate(R.menu.toolbar_menu4,menu);
        item1 = menu.findItem(R.id.agree);
        item2 = menu.findItem(R.id.noagree);
        item3 = menu.findItem(R.id.finish);
        item4 = menu.findItem(R.id.delete);
        item5 = menu.findItem(R.id.receive);
        item6 = menu.findItem(R.id.add_progress);


        okhttpDate();


        return true;
    }


    //重写菜单点击监听
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //监听返回按钮,关闭当前活动
            case android.R.id.home:
                finish();
                break;

            case R.id.agree://批准
                okhttpUpdate("agree");
                break;


            case R.id.noagree://取消批准
                okhttpUpdate("noagree");
                break;

            case R.id.finish://确认完成
                okhttpUpdate("finish");
                break;


            case R.id.delete://删除事件
                okhttpUpdate("delete");
                break;

            case R.id.receive://确认事件
                okhttpUpdate("receive");
                break;

            case R.id.add_progress://添加进度

                Intent intent = new Intent(context, ProgressActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("task_id", task_id);
                intent.putExtras(bundle);
                startActivity(intent);

                break;
        }
        return true;
    }






    //发出请求修改使用者状态
    public void okhttpUpdate(final String flag) {

            //得到存储的sessionid
            String sessionid= preferences.getString("sessionid","null");

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(getResources().getString(R.string.url)+"/UpStaetServlet?task_id="+task_id+"&flag="+flag)
                        .addHeader("cookie",sessionid)
                        .get()
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        ToastMeaagge("网络异常!");

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        finish();
                        String data=response.body().string();
                        ToastMeaagge(data);

                    }
                });

    }




    //发出请求获得task数据
    public void okhttpDate() {

            //得到存储的sessionid
            String sessionid= preferences.getString("sessionid","null");
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url(getResources().getString(R.string.url)+"/SelectTaskServlet?task_id="+task_id)
                    .addHeader("cookie",sessionid)
                    .get()
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastMeaagge("网络异常！");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String data=response.body().string();
                    jsonJXDate(data);

                }
            });

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





    //获取html中的链接
    public static String [] returnImageUrlsFromHtml(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }
        if (imageSrcList == null || imageSrcList.size() == 0) {
            Log.e("imageSrcList","无图片链接");
            return null;
        }
        return imageSrcList.toArray(new String[imageSrcList.size()]);
    }





    Gson gson = new Gson();
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:


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


                    //下载权限需要
                    GetSystemUtils.verifyStoragePermissions(context);


                    FrameLayout frameLayout = findViewById(R.id.framelayout);
                    ScrollView scrollView = findViewById(R.id.scrollview);


                    //允许javascript执行
                    item9_tv.getSettings().setJavaScriptEnabled(true);
                    //设置缓存开启
                    item9_tv.getSettings().setAppCacheEnabled(true);
                    //设置可以调用数据库
                    item9_tv.getSettings().setDatabaseEnabled(true);
                    //设置dom存储
                    item9_tv.getSettings().setDomStorageEnabled(true);

                    //帮助WebView处理各种请求事件；
                    item9_tv.setWebViewClient(new MyWebViewClient());
                    item9_tv.setWebChromeClient(new MyWebChromeClient(context,frameLayout,scrollView,toolbar));

                    String[] imageUrls = returnImageUrlsFromHtml(taskAndUser.getTask().getContent());

                    //暴露一个java对象给js，使得js可以直接调用方法，（暴露的对象，对象名）
                    item9_tv.addJavascriptInterface(new MJavascriptInterface(context,imageUrls,task_id), "listener");


                    //载入html
                    item9_tv.loadDataWithBaseURL("", taskAndUser.getTask().getContent(), "text/html", "utf-8", null);





//                    item9_tv.setDownloadListener(DownloadTask.getDefaultDownloadListener(context));

//                    item9_tv.setText(Html.fromHtml(taskAndUser.getTask().getContent(), myImageGetter, tagHandler));
//                    item9_tv.setMovementMethod(LinkMovementMethod.getInstance());



                    if("待批准".equals(taskAndUser.getTask().getState())){
                        if (permission>=taskAndUser.getUser().getPermission() && permission>=2){
                            //确认批准按钮
                            item1.setVisible(true);
                            //取消批准按钮
                            item2.setVisible(true);
                        }


                        if (id==taskAndUser.getTask().getStart_id()){
                            //删除按钮
                            item4.setVisible(true);
                        }

                    }

                    if("进行中".equals(taskAndUser.getTask().getState())){

                        //添加进度按钮
                        item6.setVisible(true);

                        if(id==taskAndUser.getTask().getStart_id()){
                            //确认完成按钮
                            item3.setVisible(true);
                        }

                        if (!"".equals(taskAndUser.getTask().getExecute_people())){
                            Map execute_map = gson.fromJson(taskAndUser.getTask().getExecute_people(), Map.class);
                            if("未确认".equals(execute_map.get(name)) ){
                                //确认进度按钮
                                item5.setVisible(true);
                            }
                        }

                        if (!"".equals(taskAndUser.getTask().getAssist_people())){
                            Map assist_map = gson.fromJson(taskAndUser.getTask().getAssist_people(), Map.class);
                            if("未确认".equals(assist_map.get(name))){
                                //确认进度按钮
                                item5.setVisible(true);
                            }
                        }




//                        String str = taskAndUser.getTask().getContent();
//                        String regex = "<p style=\"border:1px solid #000000;\">汇报人:"+name+"(.*?)"+"</br>";
//                        Pattern pattern = Pattern.compile(regex);
//                        Matcher matcher = pattern.matcher(str);

//                        String replace="";

//                        while (matcher.find()) {
//                            replace=matcher.group(0);
//                        }
//
//                        if (!"".equals(replace) ){
//
//                            button7.setVisibility(View.VISIBLE);
//
//                        }else {
//                            button7.setVisibility(View.GONE);
//                        }

                    }




                    break;



            }
        }
    };



}
