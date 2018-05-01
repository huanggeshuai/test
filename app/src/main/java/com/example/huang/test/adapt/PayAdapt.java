package com.example.huang.test.adapt;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huang.test.R;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.utils.StataCode;

import java.util.List;

/**
 * Created by huang on 2018/4/25.
 */

public class PayAdapt extends RecyclerView.Adapter<PayAdapt.ViewHolder> {
   private Context context;
    private List<Orders> orderses;

    public PayAdapt(List<Orders> orderses) {
        this.orderses = orderses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payitem,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Orders orders=orderses.get(position);
        switch (orders.getMedium()){
            case StataCode.account:
                holder.medium.setText("积分");
                break;
            case StataCode.card:
                holder.medium.setText("银行卡");
                break;
            case StataCode.platform:
                holder.medium.setText("掌上保龄球平台");
                break;
        }
        holder.time.setText(orders.getTime());
        holder.incomusername.setText(orders.getIncomeUser().getUserTruename());
        holder.detail.setText(orders.getCause());
        holder.username.setText(orders.getUser().getUserTruename());
        if(orders.getIncomeState()==1){
            holder.money.setText("+"+orders.getMoney());
            holder.money.setTextColor(Color.parseColor("#D32F2F"));
        }else {
            holder.money.setText("-"+orders.getMoney());
            holder.money.setTextColor(Color.parseColor("#212121"));
        }
    }

    @Override
    public int getItemCount() {
        return orderses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView Card;
        TextView detail,time,username,money,incomusername,medium;
        public ViewHolder(View view) {
            super(view);
            Card =view.findViewById(R.id.Card);
            detail=view.findViewById(R.id.detail);
            time=view.findViewById(R.id.time);
            username=view.findViewById(R.id.username);
            money=view.findViewById(R.id.money);
            incomusername=view.findViewById(R.id.incomeusername);
            medium=view.findViewById(R.id.medium);
        }
    }
}
