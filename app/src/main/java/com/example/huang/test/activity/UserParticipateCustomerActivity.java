package com.example.huang.test.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huang.test.R;
import com.example.huang.test.adapt.CommentAdapt;
import com.example.huang.test.entity.*;
import com.example.huang.test.entity.UserCustomActivity;
import com.example.huang.test.net.Controller;
import com.example.huang.test.net.Ip;
import com.example.huang.test.utils.AddAndRemoveAndNum;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.Payutils;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import info.hoang8f.widget.FButton;

public class UserParticipateCustomerActivity extends AppCompatActivity {

    UserCustomActivity userCustomActivity;
    ImageView imageurl;
    TextView view,incharge,phone,address,enstarttime,enstoptime,
            actstarttime,actstoptime,tip,info,enlistnumber,total,userpaymoney;
    FButton submit;
    Integer userid,personnum,id;
    User user;
    AddAndRemoveAndNum pick;
    BigDecimal TotalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_participate_customer);
        init();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             int  enlist=userCustomActivity.getEnlistnum();
              int to=  userCustomActivity.getTotal();
                if(TimeUtils.getNow().after(userCustomActivity.getEnlistStartTime())&&
                        TimeUtils.getNow().before(userCustomActivity.getEnlistStopTime())){
                    if(userCustomActivity.getEnlistnum().compareTo(userCustomActivity.getTotal())==0){
                        Toast.makeText(UserParticipateCustomerActivity.this,"报名人数已满",Toast.LENGTH_SHORT).show();

                    }else if(userCustomActivity.getTotal().compareTo(userCustomActivity.getEnlistnum()+personnum)==-1){
                        Toast.makeText(UserParticipateCustomerActivity.this,"场馆人数不够",Toast.LENGTH_SHORT).show();
                    } else {
                        if(personnum==0){
                            Toast.makeText(UserParticipateCustomerActivity.this,"选择报名人数",Toast.LENGTH_SHORT).show();

                        }else
                        GetUserinfo();
                    }

                }else {
                    Toast.makeText(UserParticipateCustomerActivity.this,"不在报名时间范围内",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void init(){
        pick=(AddAndRemoveAndNum) findViewById(R.id.pick);
        submit=(FButton) findViewById(R.id.submit);
        total=(TextView)findViewById(R.id.total);
        userCustomActivity=(UserCustomActivity)getIntent().getSerializableExtra("usercustomerinfo");
        userid=getIntent().getIntExtra("userid",0);
        incharge=(TextView) findViewById(R.id.incharge);
        phone=(TextView) findViewById(R.id.phone);
        info=(TextView) findViewById(R.id.info);
        enstarttime=(TextView) findViewById(R.id.enstarttime);
        enstoptime=(TextView) findViewById(R.id.enstoptime);
        actstarttime=(TextView) findViewById(R.id.actstarttime);
        actstoptime=(TextView) findViewById(R.id.actstoptime);
        address=(TextView) findViewById(R.id.address);
        tip=(TextView) findViewById(R.id.tip);
        userpaymoney=(TextView) findViewById(R.id.userpaymoney);
        enlistnumber=(TextView)findViewById(R.id.enlistnumber);
        incharge.setText(userCustomActivity.getVenues().getUserCharge().getUserTruename());
        phone.setText(userCustomActivity.getVenues().getPhone());
        address.setText(userCustomActivity.getVenues().getProvinces().getProvince()+userCustomActivity.getVenues().getAreas().getArea()+userCustomActivity.getVenues().getCities().getCity()+userCustomActivity.getVenues().getVenuesAddress());
        enstarttime.setText(TimeUtils.ConvertYYMMDD(userCustomActivity.getEnlistStartTime()));
        enstoptime.setText(TimeUtils.ConvertYYMMDD(userCustomActivity.getEnlistStopTime()));
        actstarttime.setText(TimeUtils.ConvertYYMMDD(userCustomActivity.getActivitiesStartTime()));
        actstoptime.setText(TimeUtils.ConvertYYMMDD(userCustomActivity.getActivitiesStopTime()));
        imageurl=(ImageView) findViewById(R.id.imageurl);
        info.setText("\t\t\t\t"+userCustomActivity.getDetailinfo());
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        userpaymoney.setText(userCustomActivity.getUserpay()+"");
        enlistnumber.setText(userCustomActivity.getEnlistnum()+"/"+userCustomActivity.getTotal());
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.coll_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        //  actionBar.setCustomView(mytoolview);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        collapsingToolbarLayout.setTitle(userCustomActivity.getActivityname());
        Glide.with(UserParticipateCustomerActivity.this).load(Ip.ip+userCustomActivity.getImageurl()).into(imageurl);
        pick.setnum(0+"");
        personnum=0;
        TotalPrice=userCustomActivity.getUserpay().multiply(new BigDecimal(personnum));
        total.setText(TotalPrice+"");
        pick.listener(new AddAndRemoveAndNum.Addlistener() {
            @Override
            public void Changelistener(int num) {
                personnum=num;
                TotalPrice=userCustomActivity.getUserpay().multiply(new BigDecimal(num));
                total.setText(userCustomActivity.getUserpay().multiply(new BigDecimal(num))+"");
                if(num==userCustomActivity.getTotal()-userCustomActivity.getEnlistnum()){
                    pick.SetaddButtonC(false, Color.GRAY);
                }else {
                    pick.SetaddButton(true, R.drawable.ic_add_black_24dp);
                }
            }
        });
    }

    void GetUserinfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getUserinfobyid;
                Map map=new HashMap();
                map.put("userid", userid);
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
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what){
                case StataCode.net_faile:
                    Toast.makeText(UserParticipateCustomerActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.getUserinfo:
                    jsonInfo=gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user=gson.fromJson(jsonInfo.getMsg(),User.class);
                    if(TotalPrice.compareTo(user.getUserBalance())>0){
                        showdialog();
                    }else{
                        JoinActivityThread();
                    }
                    // Toast.makeText(OrderAndAllocationActivity.this, TotalPrice+"", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.joinactivity:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        id=Integer.parseInt(jsonInfo.getMsg());
                        Pay();
                    }else{
                        Toast.makeText(UserParticipateCustomerActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.joineditactivity:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        userCustomActivity.setEnlistnum(personnum+userCustomActivity.getEnlistnum());
                        Toast.makeText(UserParticipateCustomerActivity.this,"预约成功", Toast.LENGTH_SHORT).show();
                        enlistnumber.setText(userCustomActivity.getEnlistnum()+"/"+userCustomActivity.getTotal());

                    }else{
                        Toast.makeText(UserParticipateCustomerActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.getComment:
                    break;
            }
        }
    };
    void showdialog(){
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("用户您好");
        dialog.setMessage("您的账号余额不足，是否需要充值余额？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(UserParticipateCustomerActivity.this,AccountActivity.class);
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
    void JoinActivityThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.partuseract;
                Map map=new HashMap();
                map.put("userid", userid);
                map.put("paymentState",0);
                map.put("userCustomActivityId",userCustomActivity.getUserCustomActivityId());
                map.put("participateState",0);
                map.put("totalnum",personnum);
                map.put("paymentMoney",TotalPrice);
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
                    message.what= StataCode.joinactivity;
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
    void Pay(){
        User user1=new User();
        Orders orders=new Orders();
        user1.setUserid(userid);
        user1.setUserBalance(TotalPrice);
        orders.setIncomeState(-1);
        orders.setMedium(StataCode.account);
        orders.setIncomeUserId(userCustomActivity.getUserid());
        Payutils payutils=new Payutils(user1,orders,getApplicationContext());
        payutils.Paydialog(getFragmentManager());
        payutils.setInfo("密码不匹配，可以到场馆活动管理里重新支付");
        payutils.getIstrue(new Payutils.isTrue() {
            @Override
            public void istrue(boolean flag) {
                if(flag){
                    JoineditActivityThread();
                }else {
                    Toast.makeText(UserParticipateCustomerActivity.this,"密码不匹配，可以到场馆活动管理里重新支付", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    void JoineditActivityThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.editpartuseract;
                Map map=new HashMap();
                map.put("participateActivitiesIdUser",id);
                map.put("userCustomActivityId",userCustomActivity.getUserCustomActivityId());
                map.put("totalnum",personnum);
                map.put("paymentState",1);
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
                    message.what= StataCode.joineditactivity;
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
