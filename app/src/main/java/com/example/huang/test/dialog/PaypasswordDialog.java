package com.example.huang.test.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.huang.test.utils.MD5Util;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by huang on 2018/4/22.
 */

public class PaypasswordDialog extends DialogFragment {
    User user;
    Context context;
    TextView title;
    TextInputLayout  priorlayoutpasspass,layoutpass,layoutpassconfirm;
    EditText priorpaypassword,password,passwordcomfirm;
    Button submit;
    Integer flag;
    SharedPreferences.Editor editor;
    String pass;
    boolean issuccess=false;
    private isTrue isTrue;
    public PaypasswordDialog(Context context, User user, Integer flag) {
        this.context = context;
        this.user=user;
        this.flag=flag;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.paypassword,container,false);
        init(view);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==StataCode.first_paypassword){
                   // if(priorpaypassword.getText().toString().equals())
                    if (isEqual()){
                        UserThead();
                    }
                }else {
                   GetUserinfo();
                }

            }
        });
        return view;
    }
    void init(View view){
        title=view.findViewById(R.id.title);
        editor=context.getSharedPreferences("paypass",MODE_PRIVATE).edit();
        priorlayoutpasspass=view.findViewById(R.id.priorlayoutpasspass);
        layoutpass=view.findViewById(R.id.layoutpass);
        layoutpassconfirm=view.findViewById(R.id.layoutpassconfirm);
        priorpaypassword=view.findViewById(R.id.priorpaypassword);
        password=view.findViewById(R.id.password);
        passwordcomfirm=view.findViewById(R.id.passwordcomfirm);
        submit=view.findViewById(R.id.submit);
        title=view.findViewById(R.id.title);
        if(flag==StataCode.first_paypassword){
            priorlayoutpasspass.setVisibility(View.GONE);
            priorpaypassword.setVisibility(View.GONE);

        }else {
            title.setVisibility(View.GONE);
        }
        SharedPreferences pref=context.getSharedPreferences("paypass",MODE_PRIVATE);
         pass=pref.getString("paypassword","");
    }
    boolean isEqual(){

     if(password.getText().toString().equals(passwordcomfirm.getText().toString())){
         layoutpassconfirm.setError(null);
         return true;
     }else
         layoutpassconfirm.setError("前后密码不一致");
         return false;
    }
    boolean isEqualFirst(){

        if(priorpaypassword.getText().toString().equals(pass)){
            priorlayoutpasspass.setError(null);
            return true;
        }else
            priorlayoutpasspass.setError("前后密码不一致");
        return false;
    }
    void UserThead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.fininshuserinfo;
                Map map = new HashMap();
                map.put("userPaypassword", passwordcomfirm.getText().toString());
                map.put("userid",user.getUserid());
                String mes = null;
                Bundle bundle = new Bundle();
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("userinfo", mes);
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
        }
        ).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.net_faile:
                    Toast.makeText(context, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case StataCode.net_success:
                    jsonInfo = gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        editor.putString("paypassword",passwordcomfirm.getText().toString());
                        editor.apply();
                    }
                    isTrue.istrue(jsonInfo.isSuccess());
                    break;
                case StataCode.getUserinfo:
                    jsonInfo=gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user=gson.fromJson(jsonInfo.getMsg(),User.class);
                    if(MD5Util.MD5(priorpaypassword.getText().toString()).equals(user.getUserPaypassword())){
                        priorlayoutpasspass.setError(null);
                        issuccess=true;
                        if (isEqual()&&issuccess){
                            UserThead();
                        }
                    }else {
                        priorlayoutpasspass.setError("支付密码错误");
                        issuccess=false;
                    }
                    // Toast.makeText(OrderAndAllocationActivity.this, TotalPrice+"", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public interface isTrue{
        void istrue(boolean flag);
    }
    public void istrue(isTrue isTrue){
        this.isTrue=isTrue;
    }

    void GetUserinfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getUserinfobyid;
                Map map=new HashMap();
                map.put("userid", user.getUserid());
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
}
