package com.example.huang.test.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.adapt.CommentAdapt;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.ParticipateVenuesActivitiey;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.VenuesAllocation;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.Payutils;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanks.htextview.evaporate.EvaporateTextView;
import com.hanks.htextview.fall.FallTextView;
import com.hanks.htextview.line.LineTextView;
import com.hanks.htextview.rainbow.RainbowTextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import info.hoang8f.widget.FButton;
import yanzhikai.textpath.AsyncTextPathView;

public class OrderAndAllocationActivity extends AppCompatActivity {
NumberPickerView numberPickerView;
    TextView des,alloc,price,total;
    VenuesAllocation venuesAllocation;
    Integer userid;
    FButton submit;
    BigDecimal TotalPrice;
    User user;
    int id;
    List<ParticipateVenuesActivitiey> participateVenuesActivitieys;
    AsyncTextPathView info;
    RecyclerView comment;
    CommentAdapt commentAdapt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_and_allocation);
        venuesAllocation=(VenuesAllocation) getIntent().getSerializableExtra("venuesallocation");
        userid=(Integer) getIntent().getIntExtra("userid",0);
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    void init() throws ParseException {
        participateVenuesActivitieys=new ArrayList<>();
        comment=(RecyclerView) findViewById(R.id.commentinfo);
        info=(AsyncTextPathView) findViewById(R.id.info);
        submit=(FButton) findViewById(R.id.submit);
        des=(TextView) findViewById(R.id.des);
        alloc=(TextView) findViewById(R.id.alloc);
        price=(TextView) findViewById(R.id.price);
        numberPickerView=(NumberPickerView) findViewById(R.id.pick);
        total=(TextView) findViewById(R.id.total);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        des.setText(venuesAllocation.getRemark());
        if(venuesAllocation.getVenuesConfiguration()==1){
            alloc.setText("豪华厅");
        }else {
            alloc.setText("普通厅");
        }
        price.setText(venuesAllocation.getFeeScale()+"/小时");
        //  actionBar.setCustomView(mytoolview);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle(venuesAllocation.getVenues().getVenuesName());

        int timedifference= TimeUtils.TimeDifference(venuesAllocation.getVenues().getOpenTime(),venuesAllocation.getVenues().getCloseTime());
        String[] city=new String[timedifference];
        for(int i=0;i<timedifference;i++){
            city[i]=i+1+"小时";
        }
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        comment.setLayoutManager(layoutManager);
        getCommentThread();
        numberPickerView.setDisplayedValues(city);
        numberPickerView.setMinValue(1);
        numberPickerView.setMaxValue(city.length);
        numberPickerView.setValue(1);
        total.setText(venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue()))+"元");
        TotalPrice=venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue()));
        numberPickerView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TotalPrice=venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue()));
                        total.setText(venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue()))+"元");
                    }
                });
                //
            }
        });
    submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GetUserinfo();
        }
    });
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
            DataGrid<ParticipateVenuesActivitiey> dataGrid;
            switch (msg.what){
                case StataCode.net_faile:
                    Toast.makeText(OrderAndAllocationActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OrderAndAllocationActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.joineditactivity:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        Toast.makeText(OrderAndAllocationActivity.this,"预约成功", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(OrderAndAllocationActivity.this,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.getComment:
                    dataGrid=gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<ParticipateVenuesActivitiey>>() {
                    }.getType());

                    participateVenuesActivitieys.addAll(dataGrid.getRows());
                    if(participateVenuesActivitieys.size()==0){
                        comment.setVisibility(View.GONE);
                        info.setVisibility(View.VISIBLE);
                        info.startAnimation(0, 1);
                    }else {
                        comment.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);

                    }
                    commentAdapt=new CommentAdapt(participateVenuesActivitieys);
                    comment.setAdapter(commentAdapt);
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
                Intent intent=new Intent(OrderAndAllocationActivity.this,AccountActivity.class);
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
                String url= Controller.joinActivityVenues;
                Map map=new HashMap();
                map.put("userid", userid);
                map.put("venuesid", venuesAllocation.getVenuesid());
                map.put("venuesAllocationId", venuesAllocation.getVenuesAllocationId());
                map.put("enlistNum", numberPickerView.getValue());
                map.put("paymentMoney", venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue())));
                map.put("paymentState",0);
                map.put("userState",0);
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
        user1.setUserBalance(venuesAllocation.getFeeScale().multiply(new BigDecimal(numberPickerView.getValue())));
        orders.setIncomeState(-1);
        orders.setMedium(StataCode.account);
        orders.setIncomeUserId(venuesAllocation.getVenues().getVenuesUserCharge());
        Payutils payutils=new Payutils(user1,orders,getApplicationContext());
        payutils.Paydialog(getFragmentManager());
        payutils.setInfo("密码不匹配，可以到场馆活动管理里重新支付");
        payutils.getIstrue(new Payutils.isTrue() {
            @Override
            public void istrue(boolean flag) {
               if(flag){
                   JoineditActivityThread();
               }else {
                   Toast.makeText(OrderAndAllocationActivity.this,"密码不匹配，可以到场馆活动管理里重新支付", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
    void JoineditActivityThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.joineditOrderActivityVenues;
                Map map=new HashMap();
                map.put("participateVenuesActivieyId",id);
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
    void getCommentThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.joineditActivityVenuesinfo;
                Map map=new HashMap();
                map.put("venuesAllocationId",venuesAllocation.getVenuesAllocationId());
                map.put("rows",100);
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
                    message.what= StataCode.getComment;
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
