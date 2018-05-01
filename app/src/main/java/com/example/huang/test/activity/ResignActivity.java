package com.example.huang.test.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.huang.test.utils.VerifyFormat;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResignActivity extends AppCompatActivity {
    TextInputLayout userLayout;
    TextInputLayout passwordLayout;
    TextInputLayout phoneLayout;
    EditText username,password,phone;
    Button resign;
    boolean emailonly=false,phoneonly=false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = new Gson();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.resign_email:
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if (jsonInfo.isSuccess()) {
                        emailonly = true;
                        userLayout.setError(null);
                    } else {
                        emailonly = false;
                        userLayout.setError(jsonInfo.getMsg());
                    }
                    break;
                case StataCode.resign_phone:
                    bundle = msg.getData();
                    gson = new Gson();
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if (jsonInfo.isSuccess()) {
                        phoneonly = true;
                        phoneLayout.setError(null);
                    } else {
                        phoneonly = false;
                        phoneLayout.setError(jsonInfo.getMsg());
                    }
                    break;
                case StataCode.resign_success:
                    bundle = msg.getData();
                    gson = new Gson();
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if (jsonInfo.isSuccess()) {
                        Intent intent = new Intent();
                        intent.putExtra("useremail", username.getText().toString());
                        intent.putExtra("userpass", password.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(ResignActivity.this, jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(ResignActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resign);
        initview();
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(phoneonly&&emailonly){
                     if(TextUtils.isEmpty(password.getText())){
                         passwordLayout.setError("密码不能为空");
                     }else {
                                 ResignThread();
                     }
               }else {
                   Toast.makeText(ResignActivity.this,"请按照提示修改",Toast.LENGTH_SHORT).show();
               }
            }
        });
      username.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void afterTextChanged(Editable editable) {
             if(VerifyFormat.isEmail(editable.toString())){
                 username.setError(null);
                  EmailOnlyThread(editable);
             }else {
                 username.setError("不是正确的邮箱");
             }
          }
      });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(VerifyFormat.isphonenum(editable.toString())){
                    phone.setError(null);
                    PhoneOnlyThread(editable);
                }else {
                    phone.setError("不是正确的手机号码");
                }
            }
        });

    }

    void initview(){
        userLayout=(TextInputLayout)findViewById(R.id.layoutuser);
        passwordLayout=(TextInputLayout)findViewById(R.id.layoutpass);
        phoneLayout=(TextInputLayout)findViewById(R.id.layoutphone);
        username=(EditText) findViewById(R.id.useremail);
        password=(EditText) findViewById(R.id.userpassword);
        phone=(EditText) findViewById(R.id.phone);
        resign=(Button) findViewById(R.id.resign);
       // View mytoolview= LayoutInflater.from(this).inflate(R.layout.resign_actionbar,null);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
       //  actionBar.setCustomView(mytoolview);
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("注册");
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

    void ResignThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.resign;
                Map map=new HashMap();
                map.put("userEmail",username.getText().toString());
                map.put("userPassword",password.getText().toString());
                map.put("phone",phone.getText().toString());
                map.put("userDrive", Build.BOARD+Build.MODEL);
                map.put("userAuthority", 3);
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
                    message.what= StataCode.resign_success;
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

    void EmailOnlyThread(final Object o){
      new Thread(new Runnable() {
          @Override
          public void run() {
              String url= Controller.emailonly;
              Map map=new HashMap();
              map.put("userEmail",o);
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
                  message.what= StataCode.resign_email;
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

    void PhoneOnlyThread(final Object o){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.phoneonly;
                Map map=new HashMap();
                map.put("phone",o);
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
                    message.what= StataCode.resign_phone;
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
