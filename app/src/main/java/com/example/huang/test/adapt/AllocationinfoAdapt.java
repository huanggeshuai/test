package com.example.huang.test.adapt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huang.test.R;
import com.example.huang.test.activity.OrderAndAllocationActivity;
import com.example.huang.test.entity.VenuesAllocation;

import java.util.List;

/**
 * Created by huang on 2018/4/19.
 */

public class AllocationinfoAdapt extends RecyclerView.Adapter<AllocationinfoAdapt.ViewHolder> {
    List<VenuesAllocation> venuesAllocations;
    private Context context;
    private Integer userid;
     public AllocationinfoAdapt(List<VenuesAllocation> venuesAllocations,Integer userid) {
        this.venuesAllocations = venuesAllocations;
         this.userid=userid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allocation_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderAndAllocationActivity.class);
                intent.putExtra("venuesallocation", venuesAllocations.get(holder.getAdapterPosition()));
                intent.putExtra("userid",userid);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VenuesAllocation venuesAllocation = venuesAllocations.get(position);
        holder.price.setText(venuesAllocation.getFeeScale().toString() + "元/小时");
        holder.price.setTextColor(Color.parseColor("#FF5722"));
        if (venuesAllocation.getVenuesConfiguration() == 1) {
            holder.alloca.setText("豪华厅");
            holder.alloca.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.alloca.setText("普通厅");
            holder.alloca.setTextColor(Color.parseColor("#8BC34A"));
        }
        // holder.alloca.setText(venuesAllocation.getVenuesConfiguration().toString());
        holder.des.setText(venuesAllocation.getRemark());
    }

    @Override
    public int getItemCount() {
        return venuesAllocations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView Card;
        TextView des, alloca, price;

        public ViewHolder(View view) {
            super(view);
            Card = view.findViewById(R.id.Card);
            des = view.findViewById(R.id.des);
            alloca = view.findViewById(R.id.alloca);
            price = view.findViewById(R.id.price);
        }
    }
}
