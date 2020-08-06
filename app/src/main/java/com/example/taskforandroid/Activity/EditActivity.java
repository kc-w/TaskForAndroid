package com.example.taskforandroid.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.taskforandroid.Bean.Task;
import com.example.taskforandroid.Dialog.LabelDialog;
import com.example.taskforandroid.Dialog.SaveDialog;
import com.example.taskforandroid.Listener.KeyboardStateObserver;
import com.example.taskforandroid.R;
import com.example.taskforandroid.Tool.GetSystemUtils;
import com.example.taskforandroid.Tool.ImageUtils;
import com.example.taskforandroid.View.RichEditorNew;
import com.google.gson.Gson;

import okhttp3.*;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditActivity extends BaseActivity implements View.OnClickListener{


    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Context context;

    //事件名称
    EditText name_et;

    //执行人员
    TextView execute_people_tv;
    //协助人员
    TextView assist_people_tv;
    //预计完成的天数
    TextView preset_time_tv;
    //事件详情
    RichEditorNew richEditor;

    //执行人员选择按钮
    Button execute_button;

    //协助人员选择按钮
    Button assist_button;

    //日期选择按钮
    Button time_button;


    //键盘顶部栏目
    HorizontalScrollView horizontalScrollView;



    //装入人员
    ArrayList<String> namelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar_edit);
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


        context =this;

        //事件名称
        name_et = findViewById(R.id.name);
        //执行人员
        execute_people_tv = findViewById(R.id.execute_people);
        //协助人员
        assist_people_tv = findViewById(R.id.assist_people);
        //预计完成的天数
        preset_time_tv = findViewById(R.id.preset_time);
        //事项内容
        richEditor = findViewById(R.id.richEditor);

        //执行人选择按钮
        execute_button = findViewById(R.id.execute_button);

        //协助人选择按钮
        assist_button = findViewById(R.id.assist_button);

        //时间选择按钮
        time_button = findViewById(R.id.time_button);



        execute_button.setOnClickListener(this);
        assist_button.setOnClickListener(this);
        time_button.setOnClickListener(this);

        getNames();



        //读取登录历史信息userinfo
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);


        richEditor=findViewById(R.id.richEditor);
        horizontalScrollView=findViewById(R.id.insertList);

        //自动获取焦点
        richEditor.focusEditor();

        //软键盘弹出监听
        KeyboardStateObserver.getKeyboardStateObserver(this).
            setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                @Override
                public void onKeyboardShow() {
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onKeyboardHide() {
                    horizontalScrollView.setVisibility(View.GONE);
                }
            });

    }

    public void getNames(){

        String Url=getResources().getString(R.string.url)+"/SelectUserNameServlet";
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
                ToastMeaagge("网络异常！");

            }

            @Override//回调成功
            public void onResponse(Call call, Response response) throws IOException {
                String data=response.body().string();


                data = data.substring(1, data.length()-1);

                namelist=new ArrayList<String>();

                String names[]=data.replace("\"","").split(",");
                namelist.add("全体员工");
                for (int i=0;i<names.length;i++){
                    if ("无".equals(names[i])){
                        continue;
                    }
                    namelist.add(names[i]);
                }

            }

        });

    }


    @Override
    public void onClick(View view) {

        Message msg=new Message();


        switch (view.getId()){


            //选择执行人员
            case R.id.execute_button:

                msg.what=2;
                handler.sendMessage(msg);

                break;


            //选择协助人员
            case R.id.assist_button:

                msg.what=3;
                handler.sendMessage(msg);

                break;

            //选择预计时间
            case R.id.time_button:

                msg.what=4;
                handler.sendMessage(msg);

                break;

        }


    }





    //重写方法,如果返回true,菜单将被显示出来,如果返回false菜单将不被显示
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu2,menu);
        return true;
    }




    //重写菜单点击监听
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save://发布任务


                checkData();

                break;

            case android.R.id.home:
                //绑定要启动的Activity对象
                Intent intent =new Intent(this, MainActivity.class);
                //启动Activity
                startActivity(intent);
                break;

        }
        return true;
    }






    //图片
    public void insertImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选
        startActivityForResult(intent, 1);
    }

    //音频
    public void insertAudio(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选
        startActivityForResult(intent, 2);
    }

    //视频
    public void insertVideo(View view) {

        if (android.os.Build.BRAND.equals("Huawei")) {
            Intent intentPic = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentPic, 3);
        }
        if (android.os.Build.BRAND.equals("Xiaomi")) {//是否是小米设备,是的话用到弹窗选取入口的方法去选取视频
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 3);
        } else {//直接跳到系统相册去选取视频
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
            }
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 3);
        }
    }

    //文件
    public void insertFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选
        startActivityForResult(intent, 4);
    }



    @Override//获取到该图片并调用接口将图片上传到服务器，上传成功以后获取到服务器返回的该图片的url
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            //ContentResolver resolver = getContentResolver();
            Uri uri = data.getData();


            if(requestCode==1){


                //得到文件真实路径
                String filePath = GetSystemUtils.getFilePathFromUri(this,  uri,".jpg");


                //压缩图片
                Bitmap bitmap = null;
                try {
                    bitmap = ImageUtils.getSmallBitmap(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //将数据保存到应用目录下并返回保存路径
                filePath = GetSystemUtils.saveToSdCard(this,bitmap);

                //插入图片
                richEditor.insertImage(filePath,"","");


            }

            if(requestCode==2){
                String filePath = GetSystemUtils.getFilePathFromUri(this,  uri,".mp3");
                //插入音频
                richEditor.insertAudio(filePath,"");

            }


            if(requestCode==3){

                String path = GetSystemUtils.getRealPathFromURI(this,uri);

                String suffix = path.substring(path.lastIndexOf(".") + 1);

                String filePath = GetSystemUtils.getFilePathFromUri(this,  uri,"."+suffix);

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
                mmr.setDataSource(filePath);
                String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
                int int_duration = Integer.parseInt(duration);
                if (int_duration > 11000) {
                    Toast.makeText(this, "视频时长已超过10秒，请重新选择", Toast.LENGTH_SHORT).show();

                }else {
                    //插入视频
                    richEditor.insertVideo(filePath,"","",context);
                }


            }

            if(requestCode==4){
                String filePath = GetSystemUtils.getFilePathFromUri(this,  uri,".tmp");
                //插入文件
                richEditor.insertFileWithDown(filePath,"上传的文件");

            }

        }
    }






    String jsonString;
    Task task;
    Gson gson = new Gson();

    public void checkData(){

        task=new Task();
        //事件名称
        String name =name_et.getText().toString();
        //执行人员
        String execute_people = execute_people_tv.getText().toString();
        if (!"".equals(execute_people) ){
            Map<String,String> execute_map= new HashMap<>();
            String execute_people_names[] = execute_people.substring(1,execute_people.length()-1).split(",");
            for (int i=0;i<execute_people_names.length;i++){

                execute_map.put(execute_people_names[i].trim(),"未确认");
            }
            execute_people = gson.toJson(execute_map);
        }



        //协助人员
        String assist_people = assist_people_tv.getText().toString();
        if (!"".equals(assist_people) ){
            Map<String,String> assist_map= new HashMap<>();
            String assist_people_names[] = assist_people.substring(1,assist_people.length()-1).split(",");
            for (int i=0;i<assist_people_names.length;i++){
                assist_map.put(assist_people_names[i].trim(),"未确认");
            }
            assist_people = gson.toJson(assist_map);
        }


        //预计完成的日期
        String predict_time = preset_time_tv.getText().toString();
        //事项内容
        String content = richEditor.getHtml();

        task.setName(name);
        task.setExecute_people(execute_people);
        task.setAssist_people(assist_people);
        task.setPreset_time(predict_time);
        task.setContent(content);



        int day = 0;
        try {
            day = GetSystemUtils.maxdays(GetSystemUtils.time(),task.getPreset_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (day<0 || "".equals(task.getName()) || "".equals(task.getStart_time()) || "".equals(task.getContent()) || "".equals(task.getExecute_people())){
            Toast.makeText(context, "信息不完整或日期填写错误!", Toast.LENGTH_SHORT).show();
        }else {
            try {
                //数据完整性校验通过后开始上传文件
                publish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }



    private String orginHtml = "";
    private int index = 0;
    private int max = 0;



    //开始上传文件
    public void publish() throws IOException {

        final ProgressDialog progressDialog = new ProgressDialog(this);


        index = 0;
        orginHtml = richEditor.getHtml();

        List<String> allSrcAndHref = richEditor.getAllSrcAndHref();
        if (allSrcAndHref == null || allSrcAndHref.size() == 0) {
            //无资源链接直接开始上传
            publishTask(orginHtml);
        } else {
            max = allSrcAndHref.size();

            progressDialog.setProgress(index);
            progressDialog.setTitle("文件上传中...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(max);
            //设置屏幕不可点击
            progressDialog.setCancelable(false);
            progressDialog.show();

            for (String src : allSrcAndHref) {

                final String orginSrc = src;

                File file = new File(orginSrc);

                Log.e("替换的路径", orginSrc);

                //得到存储的sessionid
                String sessionid= preferences.getString("sessionid","null");
                //创建一个OkHttpClient对象
                OkHttpClient client=new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("keyname", file.getName(), fileBody)
                        .build();



                // 传入 RequestBody
                Request request = new Request.Builder()
                        .addHeader("cookie",sessionid)
                        .url(getResources().getString(R.string.url)+"/FileServlet")
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                //执行请求,并产生回调
                call.enqueue(new Callback() {
                    @Override//回调失败
                    public void onFailure(Call call, IOException e) {
                        progressDialog.cancel();
                        ToastMeaagge("网络异常,上传失败!");
                    }

                    @Override//回调成功
                    public void onResponse(Call call, Response response) throws IOException {
                        String path = response.body().string();
                        //将本地资源链接替换为远程资源链接
                        orginHtml = orginHtml.replace(orginSrc, path);
                        //更新进度
                        checkStatus(orginHtml);
                    }

                    public void checkStatus(String httpHtml) {

                        ++index;
                        progressDialog.setProgress(index);

                        if (index >= max) {

                            progressDialog.cancel();
                            //开始完整任务上传
                            publishTask(httpHtml);

                        }
                    }

                });

            }
        }

    }



    //提交完整的内容
    public void publishTask(String httpHtml){

        task.setContent(httpHtml+"</br></br></br>");

        jsonString = gson.toJson(task);

        //得到存储的sessionid
        String sessionid= preferences.getString("sessionid","null");

        //创建一个OkHttpClient对象
        OkHttpClient client=new OkHttpClient();
        //设置发送头,发送的数据类型
        MediaType mediaType=MediaType.parse("application/json; charset=utf-8");
        //构建请求
        Request request=new Request.Builder()
                .url(getResources().getString(R.string.url)+"/AddTaskServlet")
                .addHeader("cookie",sessionid)
                .post(RequestBody.create(jsonString, mediaType ))
                .build();

        Call call = client.newCall(request);
        //执行请求,并产生回调
        call.enqueue(new Callback() {
            @Override//回调失败
            public void onFailure(Call call, IOException e) {
                ToastMeaagge("网络异常，任务提交失败!");
            }

            @Override//回调成功
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                finish();
                ToastMeaagge(data);

            }

        });

    }






    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            final LabelDialog dialog = new LabelDialog(context,R.style.Dialog1,namelist);

            switch (msg.what){



                case 2:
                    dialog.show();
                    dialog.setLeftButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog.getNames().size()==0){
                                execute_people_tv.setText("");
                            }else {
                                execute_people_tv.setText(dialog.getNames().toString());
                            }

                            dialog.dismiss();
                        }
                    });

                    dialog.setRightButton("清空", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            execute_people_tv.setText("");
                            dialog.dismiss();
                        }
                    });


                    break;

                case 3:
                    dialog.show();
                    dialog.setLeftButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog.getNames().size()==0){
                                assist_people_tv.setText("");
                            }else {
                                assist_people_tv.setText(dialog.getNames().toString());
                            }

                            dialog.dismiss();
                        }
                    });

                    dialog.setRightButton("清空", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            assist_people_tv.setText("");
                            dialog.dismiss();
                        }
                    });


                    break;

                case 4:

                    // 用来装日期的
                    Calendar calendar;

                    calendar = Calendar.getInstance();
                    DatePickerDialog dialog1 = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    preset_time_tv.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                                }
                            }, calendar.get(Calendar.YEAR), calendar
                            .get(Calendar.MONTH), calendar
                            .get(Calendar.DAY_OF_MONTH));
                    dialog1.show();

                    break;

            }
        }
    };





}
