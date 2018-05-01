package com.example.huang.test.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.ParticipateVenuesActivitiey;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class VenuesCommentActivity extends AppCompatActivity {
    MaterialRatingBar service,device,audit;
    Button submit;
    EditText com;
    float s,d,a,t;
    ParticipateVenuesActivitiey pr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_comment);
        init();
    }
    void init(){
        com=(EditText)findViewById(R.id.com);
        pr=(ParticipateVenuesActivitiey)getIntent().getSerializableExtra("pr");
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("评论");
        submit=(Button) findViewById(R.id.submit);
        service=(MaterialRatingBar) findViewById(R.id.service);
        device=(MaterialRatingBar) findViewById(R.id.device);
        audit=(MaterialRatingBar) findViewById(R.id.audit);
        service.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                s=rating;
            }
        });
        device.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                 d=rating;
            }
        });
        audit.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                a=rating;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommentThread();
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
    void CommentThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.comment;
                Map map = new HashMap();
                map.put("participateVenuesActivieyId",pr.getParticipateVenuesActivieyId());
                map.put("evaluate", (a+d+s)/3+"\n"+com.getText().toString());

                String mes = null;
                Bundle bundle = new Bundle();
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.net_faile:
                    Toast.makeText(VenuesCommentActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.net_success:
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        Toast.makeText(VenuesCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();

                    }

                    break;
            }
        }


    };
}
