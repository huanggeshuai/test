package com.example.huang.test.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.adapt.AllocationinfoAdapt;
import com.example.huang.test.dialog.FingeridDialog;
import com.example.huang.test.dialog.PasswoedPayDialog;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.VenuesAllocation;
import com.example.huang.test.net.Controller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by huang on 2018/4/24.
 */

public class Payutils {
   private User user;
   private Orders orders;
   private SharedPreferences pref;
  private   Context context;
  private isTrue isTrue;
    private String info;
    public Payutils(User user, Orders orders, Context context) {
        this.user = user;
        this.orders = orders;
        this.context = context;
    }

    public void Paydialog(FragmentManager fragmentManager){
        pref =context.getSharedPreferences("finger",MODE_PRIVATE);
        if(pref.getBoolean("isfinger",false)){
            showfingerDialog(fragmentManager);
        }else {
            showpaypassDialog(fragmentManager);
        }
    }
   void showfingerDialog(FragmentManager fragmentManager){
       final FingeridDialog fingeridDialog=new FingeridDialog(context);
       fingeridDialog.setCancelable(false);
       fingeridDialog.show(fragmentManager,"");
       fingeridDialog.istrue(new FingeridDialog.isTrue() {
           @Override
           public void istrue(boolean flag) {
               if (flag){
                   //Toast.makeText(context,"密码正确",Toast.LENGTH_SHORT).show();
                   RechargeThread();
                   fingeridDialog.dismiss();
               }else {
                   Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
               }
           }
       });
   }
   void showpaypassDialog( FragmentManager fragmentManager){
       final PasswoedPayDialog passwoedPayDialog=new PasswoedPayDialog(context,user);
       passwoedPayDialog.setCancelable(false);
       passwoedPayDialog.show(fragmentManager,"");
       passwoedPayDialog.getIstrue(new PasswoedPayDialog.isTrue() {
           @Override
           public void istrue(boolean flag) {
               if (flag){
                   //Toast.makeText(context,"密码正确",Toast.LENGTH_SHORT).show();
                   RechargeThread();
                   passwoedPayDialog.dismiss();
               }else {
                   passwoedPayDialog.dismiss();
                   Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
               }
           }
       });
   }
   void RechargeThread(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               String url= Controller.pay;
               Map map=new HashMap();
               map.put("userid",user.getUserid());
               map.put("userBalance",user.getUserBalance());
               map.put("incomeState",orders.getIncomeState());
               map.put("medium",orders.getMedium());
               map.put("incomeUserId",orders.getIncomeUserId());
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataGrid<VenuesAllocation> venuesAllocationDataGrid;
            Bundle bundle = msg.getData();
            JsonInfo jsonInfo = new JsonInfo();
            Gson gson = GsonBuilderUtil.create();
            switch (msg.what) {
                case StataCode.net_success:
                    jsonInfo = gson.fromJson(bundle.getString("info"), JsonInfo.class);
                    isTrue.istrue(jsonInfo.isSuccess());
                    break;
                case StataCode.net_faile:
                    Toast.makeText(context, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public interface isTrue{
        void istrue(boolean flag);
    }
    public void getIstrue(isTrue isTrue){
        this.isTrue=isTrue;
    }
}
