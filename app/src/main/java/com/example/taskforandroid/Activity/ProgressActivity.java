package com.example.taskforandroid.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.taskforandroid.Bean.TaskAndUser;
import com.example.taskforandroid.Listener.KeyboardStateObserver;
import com.example.taskforandroid.R;
import com.example.taskforandroid.Tool.GetSystemUtils;
import com.example.taskforandroid.Tool.ImageUtils;
import com.example.taskforandroid.View.RichEditor;
import com.example.taskforandroid.View.RichEditorNew;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProgressActivity extends BaseActivity{


    SharedPreferences preferences;


    int task_id;

    String html;
    String progresshtml;

    Context context;

    //进度内容
    RichEditorNew richEditor;

    //键盘顶部栏目
    HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        //找到标题栏控件
        Toolbar toolbar=findViewById(R.id.toolbar_progress);
        //将标题栏设置为自定义toolbar组件
        this.setSupportActionBar(toolbar);
        //决定左上角图标是否可以点击
        getSupportActionBar().setHomeButtonEnabled(true);
        //给左上角自动加上一个返回图标,默认id为android.R.id.home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置是否显示toolbar的标题
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        context=this;


        //读取登录历史信息userinfo
        preferences = this.getSharedPreferences("userinfo", MODE_PRIVATE);


        richEditor=findViewById(R.id.richEditor);

        horizontalScrollView=findViewById(R.id.insertList);

        richEditor.focusEditor();



        //读取intent的数据给bundle对象
        Bundle bundle = this.getIntent().getExtras();
        task_id = bundle.getInt("task_id");
        html = bundle.getString("html");
        progresshtml = bundle.getString("progresshtml");


        richEditor.setHtml(progresshtml);



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




    //显示顶部菜单栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu3,menu);
        return true;
    }


    //重写菜单点击监听
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //监听返回按钮,关闭当前活动
            case android.R.id.home:
                finish();
                break;

            //提交进度
            case R.id.submit_progress:




                try {
                    publish();
                } catch (IOException e) {
                    e.printStackTrace();
                }


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
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频(视频时长不超过十秒)"), 3);
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

            //得到文件真实路径
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
                        ToastMeaagge("网络异常,任务上传失败!");
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
                            //开始完整上传
                            publishTask(httpHtml);

                        }
                    }

                });

            }
        }

    }



    private void publishTask(final String httpHtml) {

        if ("".equals(httpHtml) || httpHtml==null){

            Toast.makeText(this, "请填写内容", Toast.LENGTH_SHORT).show();

        }else {




            if (progresshtml==null || "".equals(progresshtml)){

                Log.e("TAG", "增加进度" );


                //得到存储的sessionid
                String sessionid= preferences.getString("sessionid","null");
                //创建一个OkHttpClient对象
                OkHttpClient client=new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", String.valueOf(task_id))
                        .add("flag", "add")
                        .add("httpHtml", httpHtml)
                        .build();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/ProgressServlet")
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

                        Intent intent = new Intent();
                        intent.putExtra("task_id", task_id);
                        intent.setClass(context, ItemActivity.class);
                        context.startActivity(intent);
                        ToastMeaagge(response.body().string());

                    }

                });


            }else {

                //得到存储的sessionid
                String sessionid= preferences.getString("sessionid","null");
                //创建一个OkHttpClient对象
                OkHttpClient client=new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("id", String.valueOf(task_id))
                        .add("flag", "change")
                        .add("html", html)
                        .add("progresshtml", progresshtml)
                        .add("httpHtml", httpHtml)
                        .build();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url)+"/ProgressServlet")
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

                        Intent intent = new Intent();
                        intent.putExtra("task_id", task_id);
                        intent.setClass(context, ItemActivity.class);
                        context.startActivity(intent);
                        ToastMeaagge(response.body().string());

                    }

                });

            }



        }



    }









}