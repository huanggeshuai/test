package com.example.huang.test.adapt;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.activity.CheckUserCustomer;
import com.example.huang.test.entity.UserCustomActivity;
import com.example.huang.test.utils.PayUserCustomerManager;
import com.example.huang.test.utils.TimeUtils;

import java.util.List;

/**
 * Created by huang on 2018/4/30.
 */

public class UserCustomerManageAdapt extends RecyclerView.Adapter<UserCustomerManageAdapt.ViewHolder> {
    private Context context;
    private FragmentManager fragmentManager;
    private List<UserCustomActivity> participateVenuesActivitieys;

    public UserCustomerManageAdapt( FragmentManager fragmentManager, List<UserCustomActivity> participateVenuesActivitieys) {
        this.fragmentManager = fragmentManager;
        this.participateVenuesActivitieys = participateVenuesActivitieys;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_customer_manage_item, parent, false);
        final
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUserCustomerManager payUserCustomerManager=new PayUserCustomerManager(context,participateVenuesActivitieys,fragmentManager);
                payUserCustomerManager.Pay(viewHolder.getAdapterPosition());
                payUserCustomerManager.setInfo("支付成功");
                payUserCustomerManager.getIstrue(new PayUserCustomerManager.isTrue() {
                    @Override
                    public void istrue(boolean flag) {
                        if(flag){
                            viewHolder.order.setText("已缴费");
                            viewHolder.order.setClickable(false);
                            viewHolder.order.setTextColor(Color.parseColor("#0097A7"));
                        }
                    }
                });
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (participateVenuesActivitieys.get(viewHolder.getAdapterPosition()).getAuditing() == 0) {
                    Toast.makeText(context, "该场馆还没有审核,不能查看", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, CheckUserCustomer.class);
                    intent.putExtra("participateVenuesActivitieys", participateVenuesActivitieys.get(viewHolder.getAdapterPosition()));
                    context.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserCustomActivity pv=participateVenuesActivitieys.get(position);

        holder.venuesname.setText(pv.getVenues().getVenuesName());
        holder.venuescharge.setText("场馆负责人:"+pv.getVenues().getUserCharge().getUserTruename());
        if(pv.getVenuesAllocation().getVenuesConfiguration()==1){
            holder.allocname.setText("豪华厅");
        }else {
            holder.allocname.setText("普通厅");
        }
        holder.acttime.setText(pv.getTotal()+"人");
        holder.money.setText(pv.getPaymentmoney()+"元");
        holder.createtime.setText(TimeUtils.ConvertYYMMDD(pv.getActivitiesSettingTime()));
        if(pv.getCreatetime()==null){
            holder.ordertime.setText("未审核");
        }else {
            holder.ordertime.setText(TimeUtils.ConvertYYMMDD(pv.getCreatetime()));
        }
        if(pv.getPayState()==1){
            holder.activitystate.setText("已支付");

        }else if(pv.getPayState()==0) {
            holder.activitystate.setText("未支付");
        }
        if (pv.getPayState() == 1) {
            holder.order.setText("已缴费");
            holder.order.setClickable(false);
            holder.order.setTextColor(Color.parseColor("#0097A7"));
        }else {
            holder.order.setText("未缴费");
            holder.order.setClickable(true);
            holder.order.setTextColor(Color.parseColor("#388E3C"));
        }
        if (TextUtils.isEmpty(pv.getCause())) {
            holder.finishtime.setText("无");
        } else {
            holder.finishtime.setText(pv.getCause());
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
