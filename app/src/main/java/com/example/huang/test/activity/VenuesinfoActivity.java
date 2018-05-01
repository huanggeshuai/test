package com.example.huang.test.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huang.test.R;
import com.example.huang.test.adapt.AllocationinfoAdapt;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.VenuesAllocation;
import com.example.huang.test.net.Controller;
import com.example.huang.test.net.Ip;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenuesinfoActivity extends AppCompatActivity {
    LikeButton collection;
    Collections collections;
    ImageView imageurl;
    TextView view,incharge,phone,address,saletime,tip,info;
    RecyclerView allocinfo;
    List<VenuesAllocation> venuesAllocations;
    int collectionsflag;
    Integer userid;

    AllocationinfoAdapt allocationAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataGrid<VenuesAllocation> venuesAllocationDataGrid;
            Bundle bundle = msg.getData();
            JsonInfo jsonInfo = new JsonInfo();
            Gson gson = GsonBuilderUtil.create();
            switch (msg.what) {
                case StataCode.getMoreallocation:
                    venuesAllocationDataGrid = gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<VenuesAllocation>>() {
                    }.getType());
                    venuesAllocations.addAll(venuesAllocationDataGrid.getRows());
                    if (venuesAllocations.size() > 0) {
                        allocinfo.setVisibility(View.VISIBLE);
                        tip.setVisibility(View.GONE);
                    } else {
                        allocinfo.setVisibility(View.GONE);
                        tip.setVisibility(View.VISIBLE);
                    }
                    allocationAdapter = new AllocationinfoAdapt(venuesAllocations,userid);
                    allocinfo.setAdapter(allocationAdapter);
                    allocinfo.setNestedScrollingEnabled(false);
                    break;
                case StataCode.addcollection:
                    if (collectionsflag == 1) {
                        Toast.makeText(VenuesinfoActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VenuesinfoActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(VenuesinfoActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.getcollection:
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    collections = gson.fromJson(jsonInfo.getMsg(), Collections.class);
                    init();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venuesinfo);
        collections=(Collections) getIntent().getSerializableExtra("venyesinfo");
        userid=(Integer) getIntent().getIntExtra("userid",0);
        GetCollectionThread();
      //  init();
    }

    void init(){
        collection=(LikeButton) findViewById(R.id.collection);
        venuesAllocations=new ArrayList<>();
        incharge=(TextView) findViewById(R.id.incharge);
        phone=(TextView) findViewById(R.id.phone);
        info=(TextView) findViewById(R.id.info);
        saletime=(TextView) findViewById(R.id.saletime);
        address=(TextView) findViewById(R.id.address);
        allocinfo=(RecyclerView) findViewById(R.id.allocinfo);
        tip=(TextView) findViewById(R.id.tip);
        incharge.setText(collections.getVenues().getUserCharge().getUserTruename());
        phone.setText(collections.getVenues().getPhone());
        address.setText(collections.getVenues().getProvinces().getProvince()+collections.getVenues().getAreas().getArea()+collections.getVenues().getCities().getCity()+collections.getVenues().getVenuesAddress());
        saletime.setText(TimeUtils.ConvertHHMMSS(collections.getVenues().getOpenTime())+"-"+TimeUtils.ConvertHHMMSS(collections.getVenues().getCloseTime()));
        imageurl=(ImageView) findViewById(R.id.imageurl);
        info.setText("\t\t\t\t"+collections.getVenues().getBrief());
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.coll_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        //  actionBar.setCustomView(mytoolview);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        collapsingToolbarLayout.setTitle(collections.getVenues().getVenuesName());

        if(collections.getState()==0){
            collection.setLiked(false);
        }else {
            collection.setLiked(true);
        }
        Glide.with(VenuesinfoActivity.this).load(Ip.ip+collections.getVenues().getImageurl()).into(imageurl);
        collection.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                collectionsflag=1;
                CollectionThread(collectionsflag);
                //     Toast.makeText(VenuesinfoActivity.this,"喜欢",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                collectionsflag=0;
                CollectionThread(collectionsflag);
                //    Toast.makeText(VenuesinfoActivity.this,"不喜欢",Toast.LENGTH_SHORT).show();

            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        allocinfo.setLayoutManager(layoutManager);
        AllocationinfoThread();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    void CollectionThread(final int collection){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.Collection;
                Map map=new HashMap();
                map.put("venuesid",collections.getVenuesid());
                map.put("userid",collections.getUserid());
                map.put("collectionId",collections.getCollectionId());
                map.put("state",collection);
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
                    message.what= StataCode.addcollection;
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

    void GetCollectionThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getCollection;
                Map map=new HashMap();
                map.put("venuesid",collections.getVenuesid());
                map.put("userid",collections.getUserid());
                map.put("collectionId",collections.getCollectionId());
              //  map.put("state",collection);
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
                    message.what= StataCode.getcollection;
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
    void AllocationinfoThread(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               String url= Controller.getAllocationinfo;
               Map map=new HashMap();
               map.put("venuesid",collections.getVenuesid());
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
                   message.what= StataCode.getMoreallocation;
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
