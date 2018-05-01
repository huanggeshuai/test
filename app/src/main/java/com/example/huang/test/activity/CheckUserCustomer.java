package com.example.huang.test.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.adapt.JoinCustomerActAdapt;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.ParticipateActivitiesUser;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.UserCustomActivity;
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

import yanzhikai.textpath.AsyncTextPathView;

public class CheckUserCustomer extends AppCompatActivity {
    RecyclerView infovenuesactivity;
    SmartRefreshLayout infofresh;
    User user;
    UserCustomActivity userCustomActivit;
    List<ParticipateActivitiesUser> userCustomActivities;
    int page = 1;
    int currentpage;
    AsyncTextPathView info;
    JoinCustomerActAdapt userCustomerManageAdapt;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            DataGrid<ParticipateActivitiesUser> dataGrid;
            switch (msg.what) {
                case StataCode.net_faile:
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    Toast.makeText(CheckUserCustomer.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.net_success:
                    dataGrid = gson.fromJson(bundle.getString("info"),
                            new TypeToken<DataGrid<ParticipateActivitiesUser>>() {
                            }.getType());
                    userCustomActivities.addAll(dataGrid.getRows());
                    if (userCustomActivities.size() == 0) {
                        infovenuesactivity.setVisibility(View.GONE);
                        info.setVisibility(View.VISIBLE);
                        info.startAnimation(0, 1);
                    } else {
                        infovenuesactivity.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                    }
                    userCustomerManageAdapt = new JoinCustomerActAdapt(userCustomActivities, getFragmentManager());
                    infovenuesactivity.setAdapter(userCustomerManageAdapt);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.getmore:
                    dataGrid = null;

                    dataGrid = gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<ParticipateActivitiesUser>>() {
                    }.getType());
                    if (dataGrid.getRows().size() == 0) {
                        Toast.makeText(CheckUserCustomer.this, "没有数据了，不要在拉了", Toast.LENGTH_SHORT).show();
                        page = currentpage;
                    }
                    userCustomActivities.addAll(dataGrid.getRows());
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_customer);
        init();
    }

    void init() {
        info = (AsyncTextPathView) findViewById(R.id.info);
        userCustomActivit = (UserCustomActivity) getIntent().getSerializableExtra("participateVenuesActivitieys");
        infofresh = (SmartRefreshLayout) findViewById(R.id.infofresh);
        infovenuesactivity = (RecyclerView) findViewById(R.id.infovenuesactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("查看自定义活动用户信息");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        infovenuesactivity.setLayoutManager(layoutManager);
        userCustomActivities = new ArrayList<>();
        infofresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                userCustomActivities.clear();
                getVenureactivityThread(1);
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
                getinfomore(page);
            }
        });
        getVenureactivityThread(1);
    }

    void getVenureactivityThread(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.showpartuseract;
                Map map = new HashMap();
                map.put("userCustomActivityId", userCustomActivit.getUserCustomActivityId());
//                map.put("order","desc");
//                map.put("sort", "time");
                map.put("page", page);
//                map.put("order", "desc");
//                map.put("sort", "createtime");
                Bundle bundle = new Bundle();
                String mes = null;
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("info", mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes != null) {
                    Message message = new Message();
                    message.what = StataCode.net_success;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    void getinfomore(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.showpartuseract;
                Map map = new HashMap();
                map.put("userCustomActivityId", userCustomActivit.getUserCustomActivityId());
////                map.put("order","desc");
////                map.put("sort", "time");
                map.put("page", page);
//                map.put("order", "desc");
//                map.put("sort", "createtime");
                Bundle bundle = new Bundle();
                String mes = null;
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("info", mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes != null) {
                    Message message = new Message();
                    message.what = StataCode.getmore;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

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
