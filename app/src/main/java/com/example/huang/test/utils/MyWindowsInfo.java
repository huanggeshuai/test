package com.example.huang.test.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.example.huang.test.R;
import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.entity.Collections;
import com.google.gson.Gson;

/**
 * Created by huang on 2017/11/16.
 */

public class MyWindowsInfo implements AMap.InfoWindowAdapter,View.OnClickListener {
    TextView Ttitle,address,phone;
    ImageView daohang,callphone;
    String title;
    String add,pho;
    Context context;
    Collections collections;
    RelativeLayout win;
    Integer userid;
    private LatLng latLng;
    public MyWindowsInfo(Context context) {
        this.context = context;
    }



    @Override
    public View getInfoWindow(Marker marker) {
        initdate(marker);
        View view=init();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    void initdate(Marker marker){
        latLng=marker.getPosition();
        title=marker.getTitle();
        add=marker.getSnippet().split("xbxcv")[0];
        pho=marker.getSnippet().split("xbxcv")[1];
        Gson gson = GsonBuilderUtil.create();
        collections=gson.fromJson(marker.getSnippet().split("xbxcv")[2],Collections.class);
        userid=Integer.parseInt(marker.getSnippet().split("xbxcv")[3]);
    }
    private View init(){
        View view= LayoutInflater.from(context).inflate(R.layout.infowindows,null);
       win=view.findViewById(R.id.win);
        win.setOnClickListener(this);
        Ttitle = view.findViewById(R.id.title);
        Ttitle.setText(title);
        address = view.findViewById(R.id.address);
        address.setText(add);
        phone=view.findViewById(R.id.phone);
        phone.setText(pho);
        daohang=view.findViewById(R.id.daohang);
        callphone=view.findViewById(R.id.callphone);
        daohang.setOnClickListener(this);
        callphone.setOnClickListener(this);
        return view;

    }
    @Override

    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.daohang: //
                //Toast.makeText(context,"dianji",Toast.LENGTH_SHORT).show();
                 if(istrue()){
                     openGaode();
                 }else {
                     downGaode();
                 }
                break;
            case R.id.callphone:
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+pho));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                  break;
            case R.id.win:
                intent = new Intent(context, VenuesinfoActivity.class);
            intent.putExtra("venyesinfo", collections);
            intent.putExtra("userid",userid);
            context.startActivity(intent);
                break;
        }
    }
    public boolean istrue(){
        return IsInstalled.isAvilible(context, "com.autonavi.minimap");
    }

    void openGaode() {
        Uri uri = Uri.parse("androidamap://navi?sourceApplication=CloudPatient&lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&dev=1&style=2");
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    void downGaode() {
        Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
