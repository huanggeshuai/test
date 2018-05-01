package com.example.huang.test.adapt;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.huang.test.R;
import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.Venues;
import com.example.huang.test.net.Ip;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by huang on 2018/4/14.
 */

public class VenuesInfoAdapt extends RecyclerView.Adapter<VenuesInfoAdapt.ViewHolder> {
    private List<Collections> collectionses;

    private Context context;
    private LatLng latLng;
    private Integer userid;
    public VenuesInfoAdapt(List<Collections> collectionses, LatLng latLng,Integer userid) {
        this.collectionses = collectionses;
        this.latLng = latLng;
        this.userid=userid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venuesinfo_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VenuesinfoActivity.class);
                intent.putExtra("venyesinfo", collectionses.get(holder.getAdapterPosition()));
                intent.putExtra("userid",userid);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Glide.get(context).clearMemory();
        Venues venue = collectionses.get(position).getVenues();
        // venue.setImageurl(Ip.ip+venue.getImageurl());
        holder.venuesname.setText(venue.getVenuesName());
        String url = Ip.ip + venue.getImageurl();
        holder.opentime.setText(venue.getOpenTime());
        holder.closetime.setText(venue.getCloseTime());
        holder.address.setText(venue.getProvinces().getProvince() + venue.getCities().getCity() + venue.getAreas().getArea() + venue.getVenuesAddress());
        //    new MyTask(holder.venuesimage,"https://www.baidu.com/img/bd_logo1.png").execute(url);

        double dis = AMapUtils.calculateLineDistance(latLng, new LatLng(venue.getVenuesLatitude().doubleValue(), venue.getVenuesLongitude().doubleValue())) / 1000;
        DecimalFormat df = new DecimalFormat("######0.00");
        holder.distance.setText(df.format(dis) + "千米");


        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                Log.d("123", "onException: " + e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);
                holder.venuesimage.setImageResource(R.mipmap.ic_launcher);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.e("123", "model:" + model + " isFirstResource: " + isFirstResource);
                return false;
            }
        };
        Glide.with(context).load(url).listener(requestListener).into(holder.venuesimage);
    }

    @Override
    public int getItemCount() {
        return collectionses.size();
    }

    public void addData(List<Collections> collection) {
//      在list中添加数据，并通知条目加入一条
        int position = collectionses.size();
        collectionses.addAll(collection);
        //添加动画

        notifyItemInserted(position);
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
            distance = view.findViewById(R.id.distence);
        }
    }


}
