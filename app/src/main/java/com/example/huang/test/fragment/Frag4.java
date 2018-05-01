package com.example.huang.test.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.huang.test.R;
import com.example.huang.test.activity.VenuesinfoActivity;
import com.example.huang.test.adapt.VenuesInfoAdapt;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.MyWindowsInfo;
import com.example.huang.test.utils.PermissionUtils;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang on 2018/3/25.
 */

public class Frag4 extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener {
    MapView mapView = null;
    AMap aMap = null;
    MyLocationStyle myLocationStyle;
    LocationManager locationManager;
    AMapLocationClient mapLocationClient;//定位发起端
    AMapLocationClientOption mapLocationClientOption;//定位参数
    LocationSource.OnLocationChangedListener mlistener;//监听器
    boolean isFirstloc = true;
    Context context;
    User user;
    MyWindowsInfo adapet;
    List<Collections> collectionses;
    Marker oldmarket;
    private String[] permissions = {PermissionUtils.PERMISSION_ACCESS_FINE_LOCATION, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE, PermissionUtils.PERMISSION_CALL_PHONE};

    public Frag4(Context context,User user) {
        this.context = context;
        this.user=user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fourth, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        collectionses = new ArrayList<>();
        init();
        GetinfoThread(1);
        UiSettings settings = aMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        JudePermission();
        adapet=new MyWindowsInfo(context);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setupMap();
        }
    }

    void initloc() {
        mapLocationClient = new AMapLocationClient(context);
        mapLocationClient.setLocationListener(this);
        mapLocationClientOption = new AMapLocationClientOption();
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mapLocationClientOption.setNeedAddress(true);//是否获取位置信息
        mapLocationClientOption.setOnceLocation(false); //是否定位一次
        mapLocationClientOption.setWifiScan(true);
        mapLocationClientOption.setMockEnable(false);
        mapLocationClientOption.setInterval(2000);
        mapLocationClient.setLocationOption(mapLocationClientOption);
        mapLocationClient.startLocation();

    }

    void setupMap() {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.radiusFillColor(Color.argb(50, 135, 206, 235));
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            double lat, lon;
            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();
                lat = aMapLocation.getLatitude();//纬度
                lon = aMapLocation.getLongitude();//经度
                aMapLocation.getCity();
                Log.d("11", aMapLocation.getCity() + aMapLocation.getLocationType() + aMapLocation.getLongitude());
                if (isFirstloc) {
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //myLocationStyle.showMyLocation(true);
                    mlistener.onLocationChanged(aMapLocation);
                    isFirstloc = false;
                }
            }
        } else {
            Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(oldmarket!=null){
            oldmarket.hideInfoWindow();

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        oldmarket=marker;
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mlistener = onLocationChangedListener;

    }

    @Override
    public void deactivate() {
        mlistener = null;
    }

    void JudePermission() {
        List<String> check = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                check.add(permissions[i]);
            }

        }
        if (!check.isEmpty()) {
            requestPermissions(permissions, 1);
        } else {
            initloc();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (AllPermit(grantResults)) {
                        initloc();
                    } else {
                        Toast.makeText(context, "权限不允许，无法定位", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    boolean AllPermit(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            return grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataGrid<Collections> collectionsDataGrid;
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            switch (msg.what) {
                case StataCode.net_success:
                    collectionsDataGrid = new DataGrid<Collections>();
                    collectionsDataGrid = gson.fromJson(bundle.getString("venuesinfo"), new TypeToken<DataGrid<Collections>>() {
                    }.getType());
                    int position = collectionses.size();
                    collectionses.addAll(collectionsDataGrid.getRows());
                    String info = collectionses.toString();
                    PaintMark();
                    Log.d("we", info);
                    break;
                case StataCode.net_faile:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    void GetinfoThread(final int pages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getVenuesinfo;
                Map map = new HashMap();
                map.put("userid", user.getUserid());
                map.put("page", pages);
                map.put("City", "徐州");
                map.put("rows", 100);
                String mes = null;
                Bundle bundle = new Bundle();
                try {
                    mes = new Dopost().dopost(url, map);
                    bundle.putString("venuesinfo", mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes != null) {
                    Message message = new Message();
                    message.what = StataCode.net_success;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    Message message = new Message();
                    message.what = StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    void PaintMark  () {
        MarkerOptions markerOptions=new MarkerOptions();
        for (int i=0;i<collectionses.size();i++){
            // Marker marker= aMap.addMarker(new MarkerOptions().position(new LatLng(34.257189,117.178916)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.parting2))).draggable(true));
            Gson gson = GsonBuilderUtil.create();
            markerOptions.position(new LatLng(collectionses.get(i).getVenues().getVenuesLatitude().doubleValue(),collectionses.get(i).getVenues().getVenuesLongitude().doubleValue()));
            markerOptions.title(collectionses.get(i).getVenues().getVenuesName());
            String info=collectionses.get(i).getVenues().getVenuesAddress()+"xbxcv"+collectionses.get(i).getVenues().getPhone()+"xbxcv"+gson.toJson(collectionses.get(i))+"xbxcv"+user.getUserid();
            markerOptions.snippet(info);
             markerOptions.draggable(true);//可拖动
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_location_on_black_24dp)));
            //  markerOptions.setFlat(true);
            aMap.setInfoWindowAdapter(adapet);
            aMap.addMarker(markerOptions);
        }

    }

}
