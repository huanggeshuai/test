package com.example.huang.test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.huang.test.utils.VerifyFormat;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ForgetPAsswordActivity extends AppCompatActivity {
    EditText useremail,verifycode;
    Button SendVerifyCode,next;
    TextInputLayout layoutuser,verify;
    String code;

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
                        code = jsonInfo.getMsg();
                        Toast.makeText(ForgetPAsswordActivity.this, "邮件已发送到您的账户中,请查收", Toast.LENGTH_SHORT).show();
                        layoutuser.setError(null);
                    } else {
                        layoutuser.setError(jsonInfo.getMsg());
                    }
                    break;
                case StataCode.net_faile:
                    Toast.makeText(ForgetPAsswordActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
        SendVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VerifyFormat.isEmail(useremail.getText().toString())){
                    mytime mc=new mytime(10000,1000);
                    mc.start();
                    layoutuser.setError(null);
                    SendVerifuCodeThread();
                }else {
                    layoutuser.setError("请输入正确的邮箱");
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code==null||TextUtils.isEmpty(verifycode.getText())){
                    verify.setError("请输入验证码");
                } else if(code.equals(verifycode.getText().toString())){
                    Intent intent=new Intent(ForgetPAsswordActivity.this,ChangePasswordActivity.class);
                    intent.putExtra("email",useremail.getText().toString());
                    startActivityForResult(intent,StataCode.changepassword_result);
                    verify.setError(null);
                }else {
                    verify.setError("验证码错误，请重新输入");
                }
            }
        });
    }

    void init(){
        useremail=(EditText) findViewById(R.id.useremail);
        verifycode=(EditText) findViewById(R.id.verifycode);
        SendVerifyCode=(Button) findViewById(R.id.sendVerifyCode);
        next=(Button) findViewById(R.id.next);
        layoutuser=(TextInputLayout) findViewById(R.id.layoutuser);
        verify=(TextInputLayout) findViewById(R.id.layoutpass);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("找回密码");
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

    void SendVerifuCodeThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.findemailpassword;
                Map map=new HashMap();
                map.put("tomail",useremail.getText().toString());
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
                    message.what= StataCode.SendVerifyCode;
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
            case StataCode.changepassword_result:
                if(resultCode==RESULT_OK){
                   boolean flag=data.getBooleanExtra("success",false);
                    if(flag){
                        finish();
                    }
                }
        }
    }

    class mytime extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public mytime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SendVerifyCode.setText((millisUntilFinished / 1000) + "s重新发送");
            SendVerifyCode.setEnabled(false);
            SendVerifyCode.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onFinish() {
            SendVerifyCode.setEnabled(true);
            SendVerifyCode.setBackgroundColor(Color.parseColor("#C9DC1622"));
            SendVerifyCode.setText("重新发送");
        }
    }
}
