package com.example.huang.test.dialog;

import android.app.DialogFragment;
import android.content.Context;
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
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.activity.OrderAndAllocationActivity;
import com.example.huang.test.adapt.CommentAdapt;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.ParticipateVenuesActivitiey;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.MD5Util;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by huang on 2018/4/23.
 */

public class PasswoedPayDialog extends DialogFragment {
    TextInputLayout layoutpassconfirm;
    EditText passwordcomfirm;
    Button submit,cancel;
    Context context;
    String pass;
    private isTrue isTrue;
    User user;

    public PasswoedPayDialog(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.passwoedpaydialog,container,false);
        init(view);
        SharedPreferences pref=context.getSharedPreferences("paypass",MODE_PRIVATE);
        pass=pref.getString("paypassword","");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUserinfo();
            }
        });
        return view;
    }
    void init(View view){
        layoutpassconfirm=view.findViewById(R.id.layoutpassconfirm);
        passwordcomfirm=view.findViewById(R.id.passwordcomfirm);
        submit=view.findViewById(R.id.submit);
        cancel=view.findViewById(R.id.cancel);
    }
    public interface isTrue{
        void istrue(boolean flag);
    }
    public void getIstrue(isTrue isTrue){
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
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            DataGrid<ParticipateVenuesActivitiey> dataGrid;
            switch (msg.what){
                case StataCode.net_faile:
                    Toast.makeText(context, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.getUserinfo:
                    jsonInfo=gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user=gson.fromJson(jsonInfo.getMsg(),User.class);
                    if(MD5Util.MD5(passwordcomfirm.getText().toString()).equals(user.getUserPaypassword())){
                        layoutpassconfirm.setError(null);
                        if (isTrue!=null)
                            isTrue.istrue(true);
                    }else {
                        layoutpassconfirm.setError("支付密码错误");
                        if (isTrue!=null)
                            isTrue.istrue(false);
                    }
                    // Toast.makeText(OrderAndAllocationActivity.this, TotalPrice+"", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };
}
