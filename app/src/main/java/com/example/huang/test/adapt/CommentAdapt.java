package com.example.huang.test.adapt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huang.test.R;
import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.entity.ParticipateVenuesActivitiey;

import java.util.List;

/**
 * Created by huang on 2018/4/27.
 */

public class CommentAdapt extends RecyclerView.Adapter<CommentAdapt.ViewHolder> {
    private List<ParticipateVenuesActivitiey> participateVenuesActivitieys;
    private Context context;

    public CommentAdapt(List<ParticipateVenuesActivitiey> participateVenuesActivitieys) {
        this.participateVenuesActivitieys = participateVenuesActivitieys;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParticipateVenuesActivitiey pv=participateVenuesActivitieys.get(position);
        holder.username.setText(pv.getUser().getUserTruename());
        if(TextUtils.isEmpty(pv.getEvaluate())){
            holder.score.setVisibility(View.GONE);
            holder.comment.setText("该用户没有填写评论");
        }else {
           String[] c= pv.getEvaluate().split("\\n");
            holder.score.setText("评分:"+c[0]);
            holder.comment.setText(c[1]);
        }

    }

    @Override
    public int getItemCount() {
        return participateVenuesActivitieys.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView username,score,comment;
        public ViewHolder(View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            score=itemView.findViewById(R.id.score);
            comment=itemView.findViewById(R.id.comment);
        }
    }
}
