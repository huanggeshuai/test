package com.example.huang.test.adapt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huang.test.R;
import com.example.huang.test.activity.UserParticipateCustomerActivity;
import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.entity.UserCustomActivity;
import com.example.huang.test.net.Ip;
import com.example.huang.test.utils.TimeUtils;

import java.util.List;

/**
 * Created by huang on 2018/4/30.
 */

public class UserCustomerActAdapt extends RecyclerView.Adapter<UserCustomerActAdapt.ViewHolder> {
    private List<UserCustomActivity> userCustomActivities;

    private Context context;
    private Integer userid;

    public UserCustomerActAdapt(List<UserCustomActivity> userCustomActivities, Context context, Integer userid) {
        this.userCustomActivities = userCustomActivities;
        this.context = context;
        this.userid = userid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_actitem, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserParticipateCustomerActivity.class);
                intent.putExtra("usercustomerinfo", userCustomActivities.get(holder.getAdapterPosition()));
                intent.putExtra("userid",userid);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Glide.get(context).clearMemory();
        UserCustomActivity venue = userCustomActivities.get(position);
        // venue.setImageurl(Ip.ip+venue.getImageurl());
        holder.venuesname.setText(venue.getActivityname());
        String url = Ip.ip + venue.getImageurl();
        holder.opentime.setText(TimeUtils.ConvertYYMMDD(venue.getActivitiesStartTime()));
        holder.closetime.setText(TimeUtils.ConvertYYMMDD(venue.getActivitiesStopTime()));
        holder.address.setText(venue.getVenues().getProvinces().getProvince() + venue.getVenues().getCities().getCity() + venue.getVenues().getAreas().getArea() + venue.getVenues().getVenuesAddress());
        //    new MyTask(holder.venuesimage,"https://www.baidu.com/img/bd_logo1.png").execute(url);

        Glide.with(context).load(url).into(holder.venuesimage);
    }

    @Override
    public int getItemCount() {
        return userCustomActivities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView venuesimage;
        TextView venuesname, opentime, closetime, address, distance;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.Card);
            venuesimage = view.findViewById(R.id.venuesimage);
            venuesname = view.findViewById(R.id.venuesname);
            opentime = view.findViewById(R.id.openime);
            closetime = view.findViewById(R.id.closetime);
            address = view.findViewById(R.id.address);
        }
    }
}
