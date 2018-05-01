package com.example.huang.test.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.adapt.PayAdapt;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.VenuesAllocation;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayActivity extends AppCompatActivity {
    RecyclerView infopay;
    SmartRefreshLayout infofresh;
    User user;
    List<Orders> orderses;
    PayAdapt payAdapt;
    int page=1;
    int currentpage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        init();
        infofresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                orderses.clear();
                getPayThread(1);
                page = 1;
            }
        });
        infofresh.setEnableAutoLoadMore(false);
        infofresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                 infofresh.setEnableLoadMore(true);
                currentpage = page;
                page = page + 1;
                getPaymore(page);

            }
        });
        getPayThread(page);
    }
    void init(){
        user=(User) getIntent().getSerializableExtra("user");
        infopay=(RecyclerView) findViewById(R.id.infopay);
        infofresh=(SmartRefreshLayout) findViewById(R.id.infofresh);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("交易记录");
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        infopay.setLayoutManager(layoutManager);
        orderses=new ArrayList<>();


    }
    void getPayThread(final int page){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.payhistory;
                Map map=new HashMap();
                map.put("userid", user.getUserid());
                map.put("order","desc");
                map.put("sort", "time");
                map.put("page", page);
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
                    message.what= StataCode.net_success;
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
    void getPaymore(final int page){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.payhistory;
                Map map=new HashMap();
                map.put("userid", user.getUserid());
                map.put("order","desc");
                map.put("sort", "time");
                map.put("page", page);
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
                    message.what= StataCode.getmore;
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
            DataGrid<Orders> dataGrid ;
            switch (msg.what){
                case StataCode.net_faile:
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    Toast.makeText(PayActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.net_success:
                    dataGrid=gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<Orders>>() {
                    }.getType());
                    orderses.addAll(dataGrid.getRows());
                    payAdapt=new PayAdapt(orderses);
                    infopay.setAdapter(payAdapt);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.getmore:
                    dataGrid = null;

                    dataGrid=gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<Orders>>() {
                    }.getType());
                    //   int position=collectionses.size();
                    // collectionses.clear();
                    if (dataGrid.getRows().size() == 0) {
                        Toast.makeText(PayActivity.this, "没有数据了，不要在拉了", Toast.LENGTH_SHORT).show();
                        page = currentpage;
                    }
                    orderses.addAll(dataGrid.getRows());
                    //venuesInfoAdapt.addData(collectionses);
//                    info = collectionses.toString();
//                    Log.d("we", info);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
