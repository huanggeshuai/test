package com.example.huang.test.adapt;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huang.test.R;
import com.example.huang.test.entity.UserBank;
import com.example.huang.test.utils.TimeUtils;

import java.util.List;

/**
 * Created by huang on 2018/4/22.
 */

public class BankAdapt extends RecyclerView.Adapter<BankAdapt.Viewholder> {
    private Context context;
    private List<UserBank> userBanks;

    public BankAdapt(List<UserBank> userBanks) {
        this.userBanks = userBanks;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_item, parent, false);
        Viewholder viewholder=new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
         UserBank userBank=userBanks.get(position);
        holder.banktime.setText(TimeUtils.ConvertYYMMDD(userBank.getAddtime()));
        holder.banknumber.setText(userBank.getUserBankNumber());
        holder.cause.setText(userBank.getCause());
        holder.layout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return userBanks.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView banknumber,cause,banktime;
        LinearLayout layout;
        public Viewholder(View view) {
            super(view);
            cardView=view.findViewById(R.id.Card);
            banknumber=view.findViewById(R.id.banknumber);
            cause=view.findViewById(R.id.cause);
            banktime=view.findViewById(R.id.banktime);
            layout=view.findViewById(R.id.line2);
        }
    }
}
