package com.example.huang.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.VerifyFormat;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
     TextInputLayout userLayout;
     TextInputLayout passwordLayout;
     EditText username,password;
     Button login;
    TextView forgetpassword,resign;
SharedPreferences.Editor editor;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StataCode.net_success:
                    Bundle bundle = msg.getData();

                    // Gson gson = new GsonBuilder().registerTypeAdapter(java.util.Date.class,)
                    Gson gson = GsonBuilderUtil.create();
                    JsonInfo jsonInfo = gson.fromJson(bundle.getString("logininfo"), JsonInfo.class);
                    if (jsonInfo.isSuccess()) {
                        editor.putString("username", username.getText().toString());
                        editor.putString("password", password.getText().toString());
                        editor.apply();
                        Log.d(getApplication().getClass().getName(), jsonInfo.getMsg().toString());
                        User user = gson.fromJson(jsonInfo.getMsg(), User.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(LoginActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        editor=getSharedPreferences("logininfo",MODE_PRIVATE).edit();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          if(isnoEmpty()&&isEmail()){
             LoginThread();
          }
            }
        });
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ResignActivity.class);
                startActivityForResult(intent,StataCode.resign_result);
               // finish();
            }
        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgetPAsswordActivity.class);
                startActivityForResult(intent,StataCode.forget_result);
            }
        });
        SharedPreferences pref=getSharedPreferences("logininfo",MODE_PRIVATE);
        String uname=pref.getString("username","");
        String pass=pref.getString("password","");
        if(TextUtils.isEmpty(uname)&&TextUtils.isEmpty(pass)) {

        }else {
            username.setText(uname);
            password.setText(pass);
            login.performClick();
        }
    }

    void initview(){
        userLayout=(TextInputLayout)findViewById(R.id.layoutuser);
        passwordLayout=(TextInputLayout)findViewById(R.id.layoutpass);
        username=(EditText) findViewById(R.id.useremail);
        password=(EditText) findViewById(R.id.userpassword);
        login=(Button) findViewById(R.id.login);
        forgetpassword=(TextView) findViewById(R.id.forgetpassword);
        resign=(TextView) findViewById(R.id.resign);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            actionBar.setTitle("登入");

        }

    }

    boolean isnoEmpty(){
       if(TextUtils.isEmpty(username.getText())){
           userLayout.setError("email不能为空");
           return false;
       }else  userLayout.setError(null);
       if(TextUtils.isEmpty(password.getText())){
           passwordLayout.setError("密码不能为空");
           return false;
       }else passwordLayout.setError(null);
       return true;
   }

    boolean isEmail(){
       if(VerifyFormat.isEmail(username.getText().toString()))
       {
           userLayout.setError(null);
           return true;
       }

       else{
           userLayout.setError("email格式不正确");
           return false;
       }
   }

    void LoginThread(){
      new Thread(new Runnable() {
          @Override
          public void run() {
              String  url= Controller.login;
              Map map=new HashMap();
              map.put("userEmail",username.getText().toString());
              map.put("userPassword",password.getText().toString());
              String  mes=null;
              Bundle bundle=new Bundle();
              try {
               mes=new Dopost().dopost(url,map);
                 bundle.putString("logininfo",mes);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case StataCode.resign_result:
                if(resultCode==RESULT_OK){
                    String uname=data.getStringExtra("useremail");
                    String pawd=data.getStringExtra("userpass");
                    username.setText(uname);
                    password.setText(pawd);
                     login.performClick();
                }
        }
    }
}
