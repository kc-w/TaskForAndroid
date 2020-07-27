package com.example.taskforandroid.Activity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.example.taskforandroid.R;
import com.example.taskforandroid.Tool.GetDate;
import com.example.taskforandroid.Tool.PictureCompressUtil;
import com.example.taskforandroid.View.MyEditText;
import com.google.gson.Gson;
import okhttp3.*;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    TextView preset_time_tv ;
    //事项内容
    MyEditText content_et ;

    //执行人员选择按钮
    Button execute_button;

    //协助人员选择按钮
    Button assist_button;

    //日期选择按钮
    Button time_button;

    //选择图片按钮
    ImageButton imageButton;


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
        content_et = findViewById(R.id.content);

        //执行人选择按钮
        execute_button = findViewById(R.id.execute_button);

        //协助人选择按钮
        assist_button = findViewById(R.id.assist_button);

        //时间选择按钮
        time_button = findViewById(R.id.time_button);


        imageButton=findViewById(R.id.addImage);

        execute_button.setOnClickListener(this);
        assist_button.setOnClickListener(this);
        time_button.setOnClickListener(this);
        imageButton.setOnClickListener(this);

        getNames();

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

                Log.e("TAG", data );

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
    public void onClick(View v) {

        Message msg=new Message();


        switch (v.getId()){

            //选择图片
            case R.id.addImage:
                getImage();
                break;


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


    /**
     * 图文详情页面选择图片
     */
    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    String imageurl="";
    Bitmap bitmap;
    @Override//获取到该图片并调用接口将图片上传到服务器，上传成功以后获取到服务器返回的该图片的url
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {

            if(requestCode==1){

            }

            if(requestCode==2){
//                ContentResolver resolver = getContentResolver();
                // 获得图片的uri
                Uri uri = data.getData();


                //创建临时缓存，存放图片文件
                File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                BufferedOutputStream bos = null;

                try {
                    bitmap = PictureCompressUtil.getBitmapFormUri(context,uri);

                    bitmap = zoomImage(bitmap);

                    //开启输出流
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    //将bit数据装入文件中
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }



                Log.e("创建的临时文件存放地址", file.getAbsolutePath() );
                //发出请求
                okhttpDate(file);

            }


        }
    }
    //设置图片缩放
    public Bitmap zoomImage(Bitmap image) {
        int ImageWidth = image.getWidth();
        int ImageHeight = image.getHeight();
        //获取屏幕的宽高
        //获取全屏大小
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        //我的textview有左右留边  margin
        int actX = metrics.widthPixels;
        int actY = metrics.heightPixels;

        Matrix matrix = new Matrix();
        //进行等比例缩放程序
        matrix.postScale((float) (actX * 1.00 / ImageWidth), (float) (actX * 1.00 / ImageWidth));
        image = Bitmap.createBitmap(image, 0, 0, ImageWidth, ImageHeight, matrix, true);

        return image;

    }

    //将ImageSpan标签添加到EditText中
    public void addTextImage(Bitmap bitmap){
        // 根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(this, bitmap);


        // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "<img src=\"" + imageurl + "\" />";
        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串添加到edit输入框中
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        //得到可编辑的文本对象
        Editable edit_text = content_et.getEditableText();
        // 获取光标所在位置
        int index = content_et.getSelectionStart();

        //装入字符
        SpannableString newLine = new SpannableString("\n");
        //插入换行符
        edit_text.insert(index, newLine);


        //判断光标的位置
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            // 将选择的图片追加到EditText中光标所在位置
            edit_text.insert(index, spannableString);
        }
        //插入图片后换行
        edit_text.insert(index, newLine);
        Log.e("插入的图片地址" , spannableString.toString() );

    }


    public void okhttpDate(final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //得到存储的sessionid
                String sessionid= preferences.getString("sessionid","null");
                //创建一个OkHttpClient对象
                OkHttpClient client=new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", file.getName(), fileBody)
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
                        ToastMeaagge("网络异常!");
                    }

                    @Override//回调成功
                    public void onResponse(Call call, Response response) throws IOException {
                        String data=response.body().string();
                        if("上传图片失败".equals(data)){
                            ToastMeaagge(data);
                        }else {
                            Message msg=new Message();
                            msg.what=1;
                            handler.sendMessage(msg);
                            imageurl=data;
                            ToastMeaagge("图片上传成功");
                        }

                    }

                });

            }
        }).start();
    }


    //重写方法,如果返回true,菜单将被显示出来,如果返回false菜单将不被显示
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu2,menu);
        return true;
    }

    String jsonString;
    Task task;
    Gson gson = new Gson();;
    //重写菜单点击监听
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save://发布任务

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
                final String content = content_et.getText().toString();

                task.setName(name);
                task.setExecute_people(execute_people);
                task.setAssist_people(assist_people);
                task.setPreset_time(predict_time);
                task.setContent(content);


                jsonString = gson.toJson(task);


                final SaveDialog dialog = new SaveDialog(this,R.style.Dialog1);
                dialog.show();
                dialog.setHintText("确认提交本事件吗？");
                dialog.setLeftButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int day = 0;
                        try {
                            day = GetDate.maxdays(GetDate.time(),task.getPreset_time());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (day<=0 || "".equals(task.getName()) || "".equals(task.getStart_time()) || "".equals(task.getContent()) || "".equals(task.getExecute_people())){
                            Toast.makeText(context, "信息或日期填写错误!", Toast.LENGTH_SHORT).show();
                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
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
                                            ToastMeaagge("网络异常!");
                                        }

                                        @Override//回调成功
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String data=response.body().string();

                                            System.out.println(data);
                                            ToastMeaagge(data);

                                        }

                                    });

                                }
                            }).start();

                        }




                        dialog.dismiss();
                        //绑定要启动的Activity对象
                        Intent intent =new Intent(EditActivity.this, MainActivity.class);
                        //启动Activity
                        startActivity(intent);
                    }
                });

                dialog.setRightButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


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


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            final LabelDialog dialog = new LabelDialog(context,R.style.Dialog1,namelist);

            switch (msg.what){
                case 1:
                    addTextImage(bitmap);
                    break;

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
