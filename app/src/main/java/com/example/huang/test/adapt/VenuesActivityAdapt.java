package com.example.huang.test.adapt;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.activity.OrderAndAllocationActivity;
import com.example.huang.test.activity.VenuesCommentActivity;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.ParticipateVenuesActivitiey;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.PayAndCancel;
import com.example.huang.test.utils.Payutils;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang on 2018/4/25.
 */

public class VenuesActivityAdapt extends RecyclerView.Adapter<VenuesActivityAdapt.ViewHolder> {
   private Context context;
    private FragmentManager fragmentManager;
    private List<ParticipateVenuesActivitiey> participateVenuesActivitieys;

    public VenuesActivityAdapt(List<ParticipateVenuesActivitiey> participateVenuesActivitieys,FragmentManager fragmentManager) {
        this.participateVenuesActivitieys = participateVenuesActivitieys;
        this.fragmentManager=fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.venuesactivityitem, parent, false);
        final
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayAndCancel payAndCancel=new PayAndCancel(context,participateVenuesActivitieys,fragmentManager);
                payAndCancel.Pay(viewHolder.getAdapterPosition());
                payAndCancel.setInfo("支付成功");
                payAndCancel.getIstrue(new PayAndCancel.isTrue() {
                    @Override
                    public void istrue(boolean flag) {
                        if(flag){
                            viewHolder.order.setText("已支付");
                            viewHolder.order.setClickable(false);
                            viewHolder.order.setTextColor(Color.parseColor("#0097A7"));
                        }else {
                            viewHolder.order.setText("点击支付");
                            viewHolder.order.setClickable(true);
                            viewHolder.order.setTextColor(Color.parseColor("#388E3C"));
                        }
                    }
                });
            }
        });
        viewHolder.refuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayAndCancel payAndCancel=new PayAndCancel(context,participateVenuesActivitieys,fragmentManager);
                payAndCancel.Cancel(viewHolder.getAdapterPosition());
                payAndCancel.setInfo("成功取消活动");
                payAndCancel.getIstrue(new PayAndCancel.isTrue() {
                    @Override
                    public void istrue(boolean flag) {
                        if(flag){
                            viewHolder.activitystate.setText("活动取消");
                            viewHolder.refuns.setClickable(false);
                            viewHolder.refuns.setText("您已取消活动");
                            viewHolder.refuns.setBackgroundColor(Color.parseColor("#FF5722"));
                        }else {
                        }
                    }
                });
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(participateVenuesActivitieys.get(viewHolder.getAdapterPosition()).getPaymentState()==0){
                    Toast.makeText(context,"您还没有支付,不能评论",Toast.LENGTH_SHORT).show();
                }else if(participateVenuesActivitieys.get(viewHolder.getAdapterPosition()).getUserState()==0){
                    Toast.makeText(context,"您还没有参加活动,不能评论",Toast.LENGTH_SHORT).show();
                }else if(participateVenuesActivitieys.get(viewHolder.getAdapterPosition()).getUserState()==-1){
                    Toast.makeText(context,"您取消活动,不能评论",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent=new Intent(context, VenuesCommentActivity.class);
                    intent.putExtra("pr",participateVenuesActivitieys.get(viewHolder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
     ParticipateVenuesActivitiey pv=participateVenuesActivitieys.get(position);

        holder.venuesname.setText(pv.getVenues().getVenuesName());
        holder.venuescharge.setText("场馆负责人:"+pv.getVenues().getUserCharge().getUserTruename());
        if(pv.getVenuesAllocation().getVenuesConfiguration()==1){
            holder.allocname.setText("豪华厅");
        }else {
            holder.allocname.setText("普通厅");
        }
        holder.acttime.setText(pv.getEnlistNum()+"小时");
        holder.money.setText(pv.getPaymentMoney()+"元");
        holder.createtime.setText(TimeUtils.ConvertYYMMDD(pv.getCreatetime()));
        if(pv.getOrdertime()==null){
            holder.ordertime.setText("无");
        }else {
            holder.ordertime.setText(TimeUtils.ConvertYYMMDD(pv.getOrdertime()));
        }
        if(pv.getFinishtime()==null){
            holder.finishtime.setText("无");
        }else {
            holder.finishtime.setText(TimeUtils.ConvertYYMMDD(pv.getFinishtime()));
        }

        if(pv.getUserState()==1){
            holder.activitystate.setText("您已参加过活动");
            holder.refuns.setText("活动结束");
            holder.refuns.setClickable(false);
            holder.refuns.setBackgroundColor(Color.parseColor("#F57C00"));
        }else if(pv.getUserState()==0) {
            holder.activitystate.setText("还没有参加活动");
            holder.refuns.setText("点击取消");
            holder.refuns.setClickable(true);
            holder.refuns.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else if(pv.getUserState()==-1){
            holder.activitystate.setText("活动取消");
            holder.refuns.setClickable(false);
            holder.refuns.setText("您已取消活动");
            holder.refuns.setBackgroundColor(Color.parseColor("#FF5722"));
        }
        if(pv.getPaymentState()==1){
            holder.order.setText("已支付");
            holder.order.setClickable(false);
            holder.order.setTextColor(Color.parseColor("#0097A7"));
        }else {
            holder.order.setText("点击支付");
            holder.order.setClickable(true);
            holder.order.setTextColor(Color.parseColor("#388E3C"));
        }
    }

    @Override
    public int getItemCount() {
        return participateVenuesActivitieys.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView venuesname,venuescharge,allocname,
                acttime,money,createtime,ordertime,
                finishtime,activitystate,order,refuns;
        public ViewHolder(View view) {
            super(view);
            cardView=view.findViewById(R.id.Card);
            venuesname=view.findViewById(R.id.venuesname);
            venuescharge=view.findViewById(R.id.venuescharge);
            allocname=view.findViewById(R.id.allocname);
            acttime=view.findViewById(R.id.acttime);
            money=view.findViewById(R.id.money);
            createtime=view.findViewById(R.id.createtime);
            ordertime=view.findViewById(R.id.ordertime);
            finishtime=view.findViewById(R.id.finishtime);
            activitystate=view.findViewById(R.id.activitystate);
            order=view.findViewById(R.id.order);
            refuns=view.findViewById(R.id.refuns);
        }
    }








}
