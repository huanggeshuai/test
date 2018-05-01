package com.example.huang.test.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huang.test.R;
import com.example.huang.test.dialog.VenuesDialog;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.DataCallBack;
import com.example.huang.test.utils.DatePicker;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.Payutils;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.example.huang.test.utils.UpLoadPhoto;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public class UserCustomActivity extends AppCompatActivity implements DataCallBack,UpLoadPhoto.OnUploadProcessListener  {
    EditText actname,actnumber,actdetail,paymoney;
    TextView venuesname,enliststarttime,
            enlistendtime,actstarttime,actendtime,venuestime,userpaymoney;
    ImageView uploadimage;
    CardView imageview;
    Button submit,cancel;
   String timeflag;
    String enliststart,imgpath,
            enlistend,actstart,actend,venuesopentime,venuesclosetime;
    int venallocationid,vid;
    BigDecimal venmoney,totalmoney;
    int lasttime,actlasttime;
    User user;
    Integer id=1;
    private int userincomeid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_custom);
        init();
        enliststarttime.setEnabled(false);
        enlistendtime.setEnabled(false);
        actstarttime.setEnabled(false);
        actendtime.setEnabled(false);

        venuesname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final VenuesDialog venuesDialog=new VenuesDialog(getApplicationContext());
                venuesDialog.show(getFragmentManager(),"");
                venuesDialog.setCancelable(false);
                venuesDialog.getVenuesData(new VenuesDialog.GetVenuesData() {
                    @Override
                    public void get(int userid,int allocationid, int venuesid,int time, BigDecimal money, String venues, String alloactionname, String opentime, String closetime) {
                        venuesname.setText(venues+":"+alloactionname);
                        venallocationid=allocationid;
                        userincomeid=userid;
                        lasttime=time;
                        vid=venuesid;
                        venmoney=money;
                        venuesopentime=opentime;
                        venuesclosetime=closetime;
                        venuestime.setText("场馆开启时间:"+opentime+"\n"+
                                "场馆关闭时间:"+closetime+"\n"+
                                "场馆所需金额:"+money+"/小时"+"\n"+
                                "场馆营业"+lasttime+"小时");
                        enliststarttime.setEnabled(true);
                        venuesDialog.dismiss();
                    }
                });
            }
        });
         enliststarttime.setOnClickListener(new TimeClick());
         enlistendtime.setOnClickListener(new TimeClick());
         actstarttime.setOnClickListener(new TimeClick());
         actendtime.setOnClickListener(new TimeClick());
         enliststarttime.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void afterTextChanged(Editable editable) {
                 try {
                     if(TimeUtils.Camplare_Time(TimeUtils.getNowtime(),editable.toString())){
                         Toast.makeText(UserCustomActivity.this,"活动时间要提前一天开始",Toast.LENGTH_SHORT).show();
                         enliststarttime.setText("选择报名开始时间");
                     }else {
                         enliststart=enliststarttime.getText().toString();
                         enliststarttime.setText("活动报名开始时间:"+enliststart);
                         enlistendtime.setEnabled(true);
                     }
                 } catch (ParseException e) {
                     e.printStackTrace();
                 }
             }
         });
        enlistendtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if(TimeUtils.Camplare_Time(enliststart,editable.toString())){
                        Toast.makeText(UserCustomActivity.this,"报名结束时间要早于开始时间",Toast.LENGTH_SHORT).show();
                        enlistendtime.setText("选择报名结束时间");
                    }else {
                        enlistend=enlistendtime.getText().toString();
                        enlistendtime.setText("活动报名结束时间:"+enlistend);
                        actstarttime.setEnabled(true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        actstarttime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if(TimeUtils.Camplare_Time(enlistend,editable.toString())){
                        Toast.makeText(UserCustomActivity.this,"活动报名结束时间要早于活动开始时间",Toast.LENGTH_SHORT).show();
                        actstarttime.setText("选择活动开始时间");
                    }else {
                        actstart=actstarttime.getText().toString();
                        String[] time= actstart.split(" ");
                        if(TimeUtils.Camplare_Time(time[0]+" "+venuesopentime,editable.toString())){
                            Toast.makeText(UserCustomActivity.this,"活动报名结束时间要晚于场馆开馆时间",Toast.LENGTH_SHORT).show();
                            actstarttime.setText("选择活动开始时间");
                        }else {
                            actstarttime.setText("活动开始时间:"+actstart);
                            actendtime.setEnabled(true);
                        }

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        });
        actendtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if(TimeUtils.Camplare_Time(actstart,editable.toString())){
                        Toast.makeText(UserCustomActivity.this,"活动报名结束时间要晚于活动开始时间",Toast.LENGTH_SHORT).show();
                        actendtime.setText("选择活动结束时间");
                    }else {
                        actend=actendtime.getText().toString();
                        String[] time= actend.split(" ");
                        if(TimeUtils.Camplare_Time(editable.toString(),time[0]+" "+venuesclosetime)){
                            Toast.makeText(UserCustomActivity.this,"活动报名结束时间要早于场馆闭馆时间",Toast.LENGTH_SHORT).show();
                            actendtime.setText("选择活动结束时间");
                        }else if((actlasttime=TimeUtils.TimeDifferencebyyear(actstart,actend))>lasttime) {
                            Toast.makeText(UserCustomActivity.this,"活动时间大于场馆活动时间",Toast.LENGTH_SHORT).show();
                            actendtime.setText("选择活动结束时间");
                        }else {
                            actendtime.setText("活动结束时间:"+actend);
                            Toast.makeText(UserCustomActivity.this,actlasttime+"",Toast.LENGTH_SHORT).show();
                             totalmoney=venmoney.multiply(new BigDecimal(actlasttime));
                            userpaymoney.setText("活动持续时间:"+actlasttime+"\n"
                                    +"需要缴费:"+totalmoney
                            );
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(UserCustomActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UserCustomActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else {
                    openAlbum();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isnotnull()){
                    GetUserinfo();
                }else {
                    Toast.makeText(UserCustomActivity.this,"所填信息不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private boolean isnotnull(){
        boolean flag=false;
        Map map=new HashMap();
        map.put("userid",user.getUserid().toString());
        map.put("venuesid",String.valueOf(vid));
        map.put("venuesAllocationId",String.valueOf(venallocationid));
        map.put("activitiesStartTime",actstart);
        map.put("activitiesStopTime",actend);
        map.put("enlistStartTime",enliststart);
        map.put("enlistStopTime",enlistend);
        map.put("total",actnumber.getText().toString());
        map.put("paymentmoney",totalmoney.toString());
        map.put("activityname",actname.getText().toString());
        map.put("userpay",paymoney.getText().toString());
        map.put("detailinfo",actdetail.getText().toString());
        map.put("imageurl",imgpath);
        Set set = map.entrySet();
        for(Iterator iter = set.iterator(); iter.hasNext();){
            Map.Entry entry = (Map.Entry)iter.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if(TextUtils.isEmpty(value)){
                flag=false;
                break;
            }else {
                flag=true;
            }
        }
        return flag;
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,StataCode.CHOOSE_PHOTO);//打开相册

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                openAlbum();
            }
            else {
                Toast.makeText(this,"权限不允许",Toast.LENGTH_SHORT).show();
            }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case StataCode.CHOOSE_PHOTO:
                if(data!=null){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImagebefore(data);

                    }
                }else {
                    Toast.makeText(this,"请选择一张图片",Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        String imagepath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)) {
            String docid = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docid.split(":")[1];//解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagepath = getImage(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docid));
                imagepath = getImage(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagepath=getImage(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagepath=uri.getPath();
        }
        display(imagepath);
    }
    private void handleImagebefore(Intent data){
        Uri uri=data.getData();
        String impath=getImage(uri,null);
        display(impath);
    }
    private String getImage(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void display(String imagepath){
        if(imagepath!=null){
          //  Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            Glide.with(this).load(imagepath).into(uploadimage);
            imgpath=imagepath;
          //  handler.sendEmptyMessage(TO_UPLOAD_FILE);
        }else {
            Toast.makeText(this,"找不到",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,imagepath,Toast.LENGTH_SHORT).show();
    }

    void init(){
        totalmoney=new BigDecimal(0);
        user=(User) getIntent().getSerializableExtra("user");
        actname=(EditText) findViewById(R.id.actname);
        actnumber=(EditText) findViewById(R.id.actnumber);
        actdetail=(EditText) findViewById(R.id.actdetail);
        paymoney=(EditText) findViewById(R.id.paymoney);
        venuesname=(TextView) findViewById(R.id.venuesname);
        enliststarttime=(TextView) findViewById(R.id.enliststarttime);
        enlistendtime=(TextView) findViewById(R.id.enlistendtime);
        actstarttime=(TextView) findViewById(R.id.actstarttime);
        actendtime=(TextView) findViewById(R.id.actendtime);
        uploadimage=(ImageView)findViewById(R.id.uploadimage);
        imageview=(CardView) findViewById(R.id.imageview);
        submit=(Button) findViewById(R.id.submit);
        cancel=(Button) findViewById(R.id.cancel);
        venuestime=(TextView) findViewById(R.id.venuestime);
        userpaymoney=(TextView) findViewById(R.id.userpaymoney);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("创建自定义活动");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onUploadDone(int responseCode, String message) {
        Message msg = Message.obtain();
        msg.what = StataCode.UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        handler.sendMessage(msg);
    }
    private void toUploadFile()
    {
        String fileKey = "file";
        UpLoadPhoto uploadUtil = UpLoadPhoto.getInstance();;
        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        Map params = new HashMap();
        // params.put("orderId", "11111");
        params.put("userCustomActivityId",id.toString());
        uploadUtil.uploadFile( imgpath,fileKey, Controller.uploadimage,params);
    }
    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = StataCode.UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        handler.sendMessage(msg );
    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = StataCode.UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        handler.sendMessage(msg );
    }

    class  TimeClick implements View.OnClickListener{
       @Override
       public void onClick(View view) {
           switch (view.getId()){
               case R.id.enliststarttime:
                   DatePicker datePicker=new DatePicker(StataCode.DataandTime);
                   datePicker.show(getFragmentManager(),"");
                   timeflag="enliststarttime";
                   break;
               case R.id.enlistendtime:
                  datePicker=new DatePicker(StataCode.DataandTime);
                   datePicker.show(getFragmentManager(),"");
                   timeflag="enlistendtime";
                   break;
               case R.id.actstarttime:
                   datePicker=new DatePicker(StataCode.DataandTime);
                   datePicker.show(getFragmentManager(),"");
                   timeflag="actstarttime";
                   break;
               case R.id.actendtime:
                  datePicker=new DatePicker(StataCode.DataandTime);
                   datePicker.show(getFragmentManager(),"");
                   timeflag="actendtime";
                   break;

           }
       }
   }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.TO_UPLOAD_FILE:
                    toUploadFile();
                    break;
                case StataCode.UPLOAD_INIT_PROCESS:

                    break;
                case StataCode.UPLOAD_IN_PROCESS:
                    break;
                case StataCode.UPLOAD_FILE_DONE:
                    String string=msg.obj.toString();
                    jsonInfo=gson.fromJson(string,JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        Toast.makeText(UserCustomActivity.this,jsonInfo.getMsg(),Toast.LENGTH_SHORT).show();
                        Payment();
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(UserCustomActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.add_usercustomer:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        id=Integer.parseInt(jsonInfo.getMsg());
                        handler.sendEmptyMessage(StataCode.TO_UPLOAD_FILE);
                    }else{
                        Toast.makeText(UserCustomActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.edit_usercustomer:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                       Toast.makeText(UserCustomActivity.this,"活动提交成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(UserCustomActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.getUserinfo:
                    jsonInfo=gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user=gson.fromJson(jsonInfo.getMsg(),User.class);
                    if(totalmoney.compareTo(user.getUserBalance())>0){
                        showdialog();
                    }else{
                        AddUserCustomerActivity();
                    }
                    // Toast.makeText(OrderAndAllocationActivity.this, TotalPrice+"", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void getData(String... Data) {
     switch (timeflag){
         case "enliststarttime":
           //  enliststart=Data[0];
             enliststarttime.setText(Data[0]);
             break;
         case "enlistendtime":
             //  enliststart=Data[0];
             enlistendtime.setText(Data[0]);
             break;
         case "actstarttime":
             //  enliststart=Data[0];
             actstarttime.setText(Data[0]);
             break;
         case "actendtime":
             //  enliststart=Data[0];
             actendtime.setText(Data[0]);
             break;
     }
    }
    void AddUserCustomerActivity(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.usercustomeractivity;
                Map map=new HashMap();
                map.put("userid",user.getUserid());
                map.put("venuesid",vid);
                map.put("venuesAllocationId",venallocationid);
                map.put("activitiesStartTime",actstart);
                map.put("activitiesStopTime",actend);
                map.put("enlistStartTime",enliststart);
                map.put("enlistStopTime",enlistend);
                map.put("total",actnumber.getText().toString());
                map.put("paymentmoney",totalmoney);
                map.put("activityname",actname.getText().toString());
                map.put("userpay",paymoney.getText().toString());
                map.put("detailinfo",actdetail.getText().toString());
                Bundle bundle=new Bundle();
                String mes=null;
                try {
                    mes=new Dopost().dopost(url,map);
                    bundle.putString("info",mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes!=null){
                    Message message=new Message();
                    message.what= StataCode.add_usercustomer;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else {
                    Message message=new Message();
                    message.what= StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
    void Payment(){
        User user1 =new User();
        Orders orders =new Orders();
        user1.setUserid(user.getUserid());
        user1.setUserBalance(totalmoney);
        orders.setMedium(StataCode.account);
        orders.setIncomeUserId(userincomeid);
        orders.setIncomeState(-1);
        Payutils payutils=new Payutils(user1,orders,getApplicationContext());
        payutils.Paydialog(getFragmentManager());
        payutils.setInfo("密码不匹配，可以重新支付");
        payutils.getIstrue(new Payutils.isTrue() {
            @Override
            public void istrue(boolean flag) {
                if(flag){
                SetPayState();
                }else {

                }
            }
        });
    }
    void SetPayState(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.editusercustomeractivity;
                Map map=new HashMap();
                map.put("userCustomActivityId",id);
                map.put("payState",1);
                Bundle bundle=new Bundle();
                String mes=null;
                try {
                    mes=new Dopost().dopost(url,map);
                    bundle.putString("info",mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes!=null){
                    Message message=new Message();
                    message.what= StataCode.edit_usercustomer;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else {
                    Message message=new Message();
                    message.what= StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    void showdialog(){
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("用户您好");
        dialog.setMessage("您的账号余额不足，是否需要充值余额？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(UserCustomActivity.this,AccountActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        } );
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
    void GetUserinfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getUserinfobyid;
                Map map=new HashMap();
                map.put("userid", user.getUserid());
                Bundle bundle=new Bundle();
                String mes=null;
                try {
                    mes=new Dopost().dopost(url,map);
                    bundle.putString("userinfo",mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes!=null){
                    Message message=new Message();
                    message.what= StataCode.getUserinfo;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else {
                    Message message=new Message();
                    message.what= StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }

            }
        }).start();
    }
}
