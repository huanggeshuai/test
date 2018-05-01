package com.example.huang.test.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.activity.OrderAndAllocationActivity;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huang on 2018/4/22.
 */

public class EditName extends DialogFragment {
    EditText userNickname, userTruename;
    TextInputLayout userTruenamelayout, userNicknamelayout;
    Context context;
    Button cancel, submit;
    Integer userid;
    private isTrue isTrue;
    public EditName(Context context,Integer userid) {
        this.context = context;
        this.userid=userid;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editnamedialog, container, false);
        userNickname = view.findViewById(R.id.userNickname);
        userTruename = view.findViewById(R.id.userTruename);
        userNicknamelayout = view.findViewById(R.id.userNicknamelayout);
        userTruenamelayout = view.findViewById(R.id.userTruenamelayout);
        cancel=view.findViewById(R.id.cancel);
        submit=view.findViewById(R.id.submit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(userNickname.getText())||TextUtils.isEmpty(userTruename.getText())){
                    userTruenamelayout.setError("不能为空");
                }else {
                    UserThead();
                    userTruenamelayout.setError(null);
                }

               // dismiss();
            }
        });
        return view;
    }

    void UserThead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.fininshuserinfo;
                Map map = new HashMap();
                map.put("userNickname", userNickname.getText().toString());
                map.put("userTruename", userTruename.getText().toString());
                map.put("userid",userid);
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

    @Override
    public void onResume() {

        super.onResume();
//        Window dialogWindow = getDialog().getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
//        lp.x = 0;
//        lp.y = 0;
//        lp.width = lp.MATCH_PARENT;
//        lp.height = 600;
//        lp.alpha = 0.7f;
//        dialogWindow.setAttributes(lp);
    }

}