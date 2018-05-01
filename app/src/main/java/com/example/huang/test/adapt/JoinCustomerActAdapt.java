package com.example.huang.test.adapt;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.entity.ParticipateActivitiesUser;
import com.example.huang.test.entity.UserCustomActivity;
import com.example.huang.test.utils.JoinCustomerPay;
import com.example.huang.test.utils.TimeUtils;

import java.util.List;

/**
 * Created by huang on 2018/5/1.
 */

public class JoinCustomerActAdapt extends RecyclerView.Adapter<JoinCustomerActAdapt.ViewHolder> {
    private Context context;
    List<ParticipateActivitiesUser> participateActivitiesUsers;
    private FragmentManager fragmentManager;

    public JoinCustomerActAdapt(List<ParticipateActivitiesUser> participateActivitiesUsers, FragmentManager fragmentManager) {
        this.participateActivitiesUsers = participateActivitiesUsers;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.joincustomer_item, parent, false);
        final
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticipateActivitiesUser pv=participateActivitiesUsers.get(viewHolder.getAdapterPosition());
                UserCustomActivity userCustomActivity=pv.getUserCustomActivity();
                if(userCustomActivity.getTotal().compareTo(userCustomActivity.getEnlistnum())==0){
                    Toast.makeText(context,"您报名的场馆满了",Toast.LENGTH_SHORT).show();
                }else if(userCustomActivity.getTotal().compareTo(userCustomActivity.getEnlistnum()+pv.getTotalnum())==-1){
                    Toast.makeText(context,"场馆人数不够",Toast.LENGTH_SHORT).show();
                }
                else {
                    JoinCustomerPay payUserCustomerManager=new JoinCustomerPay(context,participateActivitiesUsers,fragmentManager);
                    payUserCustomerManager.Pay(viewHolder.getAdapterPosition());
                    payUserCustomerManager.setInfo("支付成功");
                    payUserCustomerManager.getIstrue(new JoinCustomerPay.isTrue() {
                        @Override
                        public void istrue(boolean flag) {
                            if (flag){
                                viewHolder.order.setText("已缴费");
                                viewHolder.order.setClickable(false);
                                viewHolder.order.setTextColor(Color.parseColor("#0097A7"));
                            }
                        }
                    });
                }


            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParticipateActivitiesUser pv=participateActivitiesUsers.get(position);

        holder.venuesname.setText(pv.getUserCustomActivity().getActivityname());
        holder.venuescharge.setText("活动创办人:"+pv.getUserCustomActivity().getUser().getUserTruename());
//        if(pv.getVenuesAllocation().getVenuesConfiguration()==1){
//            holder.allocname.setText("豪华厅");
//        }else {
//            holder.allocname.setText("普通厅");
//        }
        holder.acttime.setText(pv.getTotalnum()+"人");
        holder.money.setText(pv.getPaymentMoney()+"元");
        holder.createtime.setText(TimeUtils.ConvertYYMMDD(pv.getTime()));
        if(pv.getOrdertime()==null){
            holder.ordertime.setText("无");
        }else {
            holder.ordertime.setText(TimeUtils.ConvertYYMMDD(pv.getOrdertime()));
        }
        if(pv.getPaymentState()==1){
            holder.order.setText("已缴费");
            holder.order.setClickable(false);
            holder.order.setTextColor(Color.parseColor("#0097A7"));
        }else if(pv.getPaymentState()==0) {
            holder.order.setText("未缴费");
            holder.order.setClickable(true);
            holder.order.setTextColor(Color.parseColor("#388E3C"));
        }
//        if(pv.getAuditing()==1){
//            holder.order.setText("已缴费");
//            holder.order.setClickable(false);
//            holder.order.setTextColor(Color.parseColor("#0097A7"));
//        }else {
//            holder.order.setText("未缴费");
//            holder.order.setClickable(true);
//            holder.order.setTextColor(Color.parseColor("#388E3C"));
//        }
    }

    @Override
    public int getItemCount() {
        return participateActivitiesUsers.size();
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
