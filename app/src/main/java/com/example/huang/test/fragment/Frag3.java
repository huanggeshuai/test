package com.example.huang.test.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.huang.test.R;
import com.example.huang.test.adapt.UserCustomerActAdapt;
import com.example.huang.test.adapt.VenuesInfoAdapt;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.UserCustomActivity;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.PermissionUtils;
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
import yanzhikai.textpath.SyncTextPathView;

/**
 * Created by huang on 2018/3/25.
 */

public class Frag3 extends Fragment {
    MyLocationStyle myLocationStyle;
    LocationManager locationManager;
    AMapLocationClient mapLocationClient;//定位发起端
    AMapLocationClientOption mapLocationClientOption;//定位参数
    LocationSource.OnLocationChangedListener mlistener;//监听器

    User user;
    Context context;
    RecyclerView venuesinfo;
    UserCustomerActAdapt userCustomerActAdapt;
    int startpage = 1;
    int currentpage;
    SmartRefreshLayout infofresh;
    List<UserCustomActivity> userCustomActivities;
    int page = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataGrid<UserCustomActivity> userCustomActivityDataGrid;
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            switch (msg.what) {
                case StataCode.net_success:
                    userCustomActivityDataGrid = new DataGrid<UserCustomActivity>();
                    userCustomActivityDataGrid = gson.fromJson(bundle.getString("venuesinfo"), new TypeToken<DataGrid<UserCustomActivity>>() {
                    }.getType());
                    int position = userCustomActivities.size();
                    userCustomActivities.addAll(userCustomActivityDataGrid.getRows());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    venuesinfo.setLayoutManager(layoutManager);
                    userCustomerActAdapt = new UserCustomerActAdapt(userCustomActivities,context,user.getUserid());
                    venuesinfo.setAdapter(userCustomerActAdapt);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.net_faile:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.getmore:
                    userCustomActivityDataGrid = null;

                    userCustomActivityDataGrid = gson.fromJson(bundle.getString("venuesinfo"), new TypeToken<DataGrid<UserCustomActivity>>() {
                    }.getType());
                    //   int position=collectionses.size();
                    // collectionses.clear();
                    if (userCustomActivityDataGrid.getRows().size() == 0) {
                        Toast.makeText(context, "没有数据了，不要在拉了", Toast.LENGTH_SHORT).show();
                        page = currentpage;
                    }
                    userCustomActivities.addAll(userCustomActivityDataGrid.getRows());
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
            }
        }
    };

    public Frag3(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.thired, container, false);
        userCustomActivities = new ArrayList<>();
        venuesinfo = view.findViewById(R.id.infovenues);
        infofresh = view.findViewById(R.id.infofresh);
        infofresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                userCustomActivities.clear();
                GetinfoThread(startpage);
                page = 1;
            }
        });
        infofresh.setEnableAutoLoadMore(false);
        infofresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                // infofresh.setEnableLoadMore(true);
                currentpage = page;
                page = page + 1;
                GetMoreThread(page);

            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                GetinfoThread(startpage);
            }
        }, 500);


        return view;

    }

    void GetinfoThread(final int pages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getUserCustomeract;
                Map map = new HashMap();
                map.put("page", pages);
                map.put("rows", 10);
                String mes = null;
                Bundle bundle = new Bundle();
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("venuesinfo", mes);
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

    void GetMoreThread(final int pages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getUserCustomeract;
                Map map = new HashMap();

                map.put("page", pages);

                map.put("rows", 10);
                String mes = null;
                Bundle bundle = new Bundle();
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("venuesinfo", mes);
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





}