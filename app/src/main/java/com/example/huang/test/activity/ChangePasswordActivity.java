package com.example.huang.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import yanzhikai.textpath.AsyncTextPathView;

public class ChangePasswordActivity extends AppCompatActivity {
    AsyncTextPathView title;
    TextInputLayout layoutpass, layoutpassconfirm;
    EditText passwordcomfirm, password;
    Button submit;
    String email;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = new Gson();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.SendVerifyCode:
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if (jsonInfo.isSuccess()) {
                        Toast.makeText(ChangePasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("success", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        layoutpassconfirm.setError(jsonInfo.getMsg());
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(ChangePasswordActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        email = getIntent().getStringExtra("email");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(passwordcomfirm.getText())) {
                    layoutpassconfirm.setError("请先填写密码");
                } else {
                    if (password.getText().toString().equals(passwordcomfirm.getText().toString())) {
                        layoutpassconfirm.setError(null);
                        ChangePasswordThread();
                    } else {
                        layoutpassconfirm.setError("两次输入的密码不一致");
                    }
                }
            }
        });
    }

    void init() {
        title = (AsyncTextPathView) findViewById(R.id.title);
        layoutpass = (TextInputLayout) findViewById(R.id.layoutpass);
        layoutpassconfirm = (TextInputLayout) findViewById(R.id.layoutpassconfirm);
        password = (EditText) findViewById(R.id.password);
        passwordcomfirm = (EditText) findViewById(R.id.passwordcomfirm);
        submit = (Button) findViewById(R.id.submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("修改密码");
        title.startAnimation(0, 1);
    }

    void ChangePasswordThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.changepassword;
                Map map = new HashMap();
                map.put("userEmail", email);
                map.put("userPassword", password.getText().toString());
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
                    message.what = StataCode.SendVerifyCode;
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
