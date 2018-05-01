package com.example.huang.test.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.nanchen.bankcardutil.BankInfoUtil;
import com.nanchen.bankcardutil.ContentWithSpaceEditText;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huang on 2018/4/23.
 */

public class AddBankDialog extends DialogFragment {
    Context context;
    User user;
    ContentWithSpaceEditText banknumber;
    Button submit,cancel;
    TextInputLayout banklayout;
    TextView info;
    private isTrue isTrue;
    private BankInfoUtil mInfoUtil;

    public AddBankDialog(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.addbankdialog,container,false);
        init(view);
        return view;
    }
    void init(View view){
        banklayout=view.findViewById(R.id.banklayout);
        banknumber=view.findViewById(R.id.banknumber);
        submit=view.findViewById(R.id.submit);
        cancel=view.findViewById(R.id.cancel);
        info=view.findViewById(R.id.info);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(info.getText().toString())){
                    banklayout.setError("填写相关信息");
                }
                else {
                    banklayout.setError(null);
                    AddBankThrea();
                }
            }
        });
       banknumber.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
              // String cardNum = mTvAccountNo.getText().toString().;
               if(VerifyFormat.checkBankCard(banknumber.getText().toString().replace(" ", ""))){
                   mInfoUtil = new BankInfoUtil(banknumber.getText().toString().replace(" ", ""));
                   info.setText("银行:"+mInfoUtil.getBankName()+"\n"+"银行id:"
                           +mInfoUtil.getBankId()+"\n"+"银行卡类型:"+mInfoUtil.getCardType());
                   banklayout.setError(null);
               }else {
                   banklayout.setError("请输入正确的银行卡");
               }

           }
       });
    }
    void AddBankThrea(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.addBank;
                Map map = new HashMap();
                map.put("cause", info.getText().toString());
                map.put("userBankNumber",banknumber.getText().toString().replace(" ", ""));
                map.put("userId",user.getUserid());
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
                    Toast.makeText(context, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case StataCode.net_success:
                    jsonInfo = gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    isTrue.istrue(jsonInfo.isSuccess());
                    break;
            }
        }


    };
    public interface isTrue{
        void istrue(boolean flag);
    }
    public void getIstrue(isTrue isTrue){
        this.isTrue=isTrue;
    }
}
