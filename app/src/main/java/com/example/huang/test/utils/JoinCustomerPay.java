package com.example.huang.test.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.ParticipateActivitiesUser;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang on 2018/5/1.
 */

public class JoinCustomerPay {
    Context context;
    String info;
    private List<ParticipateActivitiesUser> userCustomActivities;
    private FragmentManager fragmentManager;
    public void setInfo(String info) {
        this.info = info;
    }

    public JoinCustomerPay(Context context,List<ParticipateActivitiesUser> userCustomActivities, FragmentManager fragmentManager) {
        this.context = context;
        this.userCustomActivities = userCustomActivities;
        this.fragmentManager = fragmentManager;
    }

    public void Pay(int position){
        final ParticipateActivitiesUser pv=userCustomActivities.get(position);
        User user=new User();
        user.setUserid(pv.getUserid());
        user.setUserBalance(pv.getPaymentMoney());
        Orders orders=new Orders();
        orders.setIncomeUserId(pv.getUserCustomActivity().getUserid());
        orders.setIncomeState(-1);
        orders.setMedium(StataCode.account);
        Payutils payutils=new Payutils(user,orders,context);
        payutils.Paydialog(fragmentManager);
        payutils.setInfo("密码错误，请稍后再试");
        payutils.getIstrue(new Payutils.isTrue() {
            @Override
            public void istrue(boolean flag) {
                if (flag){
                    JoineditActivityThread(pv);
                }else {

                }
            }
        });
    }
    private void JoineditActivityThread(final ParticipateActivitiesUser pv){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.editpartuseract;
                Map map=new HashMap();
                map.put("participateActivitiesIdUser",pv.getParticipateActivitiesIdUser());
                map.put("userCustomActivityId",pv.getUserCustomActivityId());
                map.put("totalnum",pv.getTotalnum());
                map.put("paymentState",1);
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
                    message.what= StataCode.joineditactivity;
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
            switch (msg.what){
                case StataCode.net_faile:
                    Toast.makeText(context, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.joineditactivity:
                    jsonInfo=gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    if(jsonInfo.isSuccess()){
                        Toast.makeText(context,info, Toast.LENGTH_SHORT).show();
                        isTrue.istrue(jsonInfo.isSuccess());
                    }else{
                        Toast.makeText(context,jsonInfo.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private isTrue isTrue;
    public interface isTrue{
        void istrue(boolean flag);
    }
    public void getIstrue(isTrue isTrue){
        this.isTrue=isTrue;
    }
}
