package com.example.huang.test.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.huang.test.R;
import com.example.huang.test.adapt.VenuesInfoAdapt;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.User;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.PermissionUtils;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yanzhikai.textpath.AsyncTextPathView;
import yanzhikai.textpath.SyncTextPathView;

/**
 * Created by huang on 2018/1/25.
 */

public class Frag1 extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener {
    MyLocationStyle myLocationStyle;
    LocationManager locationManager;
    AMapLocationClient mapLocationClient;//定位发起端
    AMapLocationClientOption mapLocationClientOption;//定位参数
    LocationSource.OnLocationChangedListener mlistener;//监听器

    User user;
    Context context;
    RecyclerView venuesinfo;
    VenuesInfoAdapt venuesInfoAdapt;
    int startpage = 1;
    int currentpage;
    SmartRefreshLayout infofresh;
    SyncTextPathView stpv_2017;
    AsyncTextPathView atpv1;
    List<Collections> collectionses;
    int page = 1;
    String city;
    double lat, lon;
    LatLng latLng;
    private boolean isLoadMore = true;
    private String[] permissions = {PermissionUtils.PERMISSION_ACCESS_FINE_LOCATION, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE, PermissionUtils.PERMISSION_CALL_PHONE};
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
                    Log.d("we", info);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    venuesinfo.setLayoutManager(layoutManager);
                    //  if(latLng!=null){
                    venuesInfoAdapt = new VenuesInfoAdapt(collectionses, latLng,user.getUserid());
                    venuesinfo.setAdapter(venuesInfoAdapt);
                    // }else {
                    //   Toast.makeText(context,"无法获取定位信息",Toast.LENGTH_SHORT).show();
                    // }


                    //  venuesInfoAdapt.notifyItemInserted(position);
                    // venuesInfoAdapt.addData(collectionses);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.net_faile:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
                case StataCode.getmore:
                    collectionsDataGrid = null;

                    collectionsDataGrid = gson.fromJson(bundle.getString("venuesinfo"), new TypeToken<DataGrid<Collections>>() {
                    }.getType());
                    //   int position=collectionses.size();
                    // collectionses.clear();
                    if (collectionsDataGrid.getRows().size() == 0) {
                        Toast.makeText(context, "没有数据了，不要在拉了", Toast.LENGTH_SHORT).show();
                        page = currentpage;
                    }
                    collectionses.addAll(collectionsDataGrid.getRows());
                    //venuesInfoAdapt.addData(collectionses);
                    info = collectionses.toString();
                    Log.d("we", info);
                    infofresh.finishRefresh();
                    infofresh.finishLoadMore();
                    break;
            }
        }
    };

    public Frag1(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.first, container, false);
        collectionses = new ArrayList<>();
        venuesinfo = view.findViewById(R.id.infovenues);
        infofresh = view.findViewById(R.id.infofresh);
        JudePermission();
        infofresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                collectionses.clear();
                GetinfoThread(startpage);
                page = 1;
            }
        });
        infofresh.setEnableAutoLoadMore(false);
        infofresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                // infofresh.setEnableLoadMore(true);
                currentpage = page;
                page = page + 1;
                GetMoreThread(page);

            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                GetinfoThread(startpage);
            }
        }, 500);


        return view;

    }

    void GetinfoThread(final int pages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getVenuesinfo;
                Map map = new HashMap();
                map.put("userid", user.getUserid());
                map.put("page", pages);
                map.put("City", city);
                map.put("rows", 10);
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

    void GetMoreThread(final int pages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getVenuesinfo;
                Map map = new HashMap();
                map.put("userid", user.getUserid());
                map.put("page", pages);
                map.put("City", city);
                map.put("rows", 10);
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
                    message.what = StataCode.getmore;
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {

            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();
                lat = aMapLocation.getLatitude();//纬度
                lon = aMapLocation.getLongitude();//经度
                latLng = new LatLng(lat, lon);
                city = aMapLocation.getCity();
                Log.d("11", aMapLocation.getCity() + aMapLocation.getLocationType() + aMapLocation.getLongitude());

                // aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                //myLocationStyle.showMyLocation(true);
                mlistener.onLocationChanged(aMapLocation);
                // isFirstloc=false;

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

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
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

}

//    Button button;
//    TextView textView;
//    FingerprintManager fingerprintManager;
//    KeyguardManager keyguardManager;
//        button = (Button) view.findViewById(R.id.fig);
//        textView = (TextView) view.findViewById(R.id.text);
//        fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
//        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(isFinger()){
//                    textView.setText("开始测试指纹");
//                    startListening(null);
//                }
//            }
//        });
//        atpv1 = view.findViewById(R.id.atpv_1);
//        stpv_2017 = view.findViewById(R.id.stpv_2017);
//
//        //从无到显示
//        atpv1.startAnimation(0,1);
//        //从显示到消失
//        stpv_2017.startAnimation(1,0);
//        return view;
//    public boolean isFinger() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return false;
//        }
//        if (!fingerprintManager.isHardwareDetected()) {
//            Toast.makeText(context, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!keyguardManager.isKeyguardSecure()) {
//            Toast.makeText(context, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!fingerprintManager.hasEnrolledFingerprints()) {
//            Toast.makeText(context, "没有录入指纹", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    CancellationSignal mCancellationSignal = new CancellationSignal();
//    //回调方法
//    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
//        @Override
//        public void onAuthenticationError(int errorCode, CharSequence errString) {
//            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//            Toast.makeText(context, errString, Toast.LENGTH_SHORT).show();
//            //  showAuthenticationScreen();
//        }
//        @Override
//        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//
//            Toast.makeText(context, helpString, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//            textView.setText("指纹正确");
//            Toast.makeText(context, "指纹识别成功", Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onAuthenticationFailed() {
//            textView.setText("指纹错误");
//            Toast.makeText(context, "指纹识别失败", Toast.LENGTH_SHORT).show();
//        }
//    };
//    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
//        //android studio 上，没有这个会报错
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
//
//    }
//
//    private void showAuthenticationScreen() {
//        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent("finger", "测试指纹识别");
//        if (intent != null) {
//            startActivityForResult(intent, 0);
//        }
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0) {
//            // Challenge completed, proceed with using cipher
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(context, "识别成功", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "识别失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void Log(String tag, String msg) {
//        Log.d(tag, msg);
//    }
//

