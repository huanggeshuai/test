package com.example.huang.test.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.entity.Collections;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.VenuesAllocation;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.example.huang.test.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huang on 2018/4/28.
 */

public class VenuesDialog extends DialogFragment {
    Context context;
    Spinner venuesaollection,venues;
    Button submit,cancel;
    private List<Collections> collectionses;
    Myadapt myadapt;
    private MyAllocationadapt allocationadapt;
    private Integer venuesid,allocationid=-1;
    private  BigDecimal money;
    private  int time;
    private String venueaname,allocation,opentime,closetime;
    private TextView info;
    List<VenuesAllocation> venuesAllocations;
    public VenuesDialog(Context context) {
        this.context = context;
    }
    private GetVenuesData venuesData;
    private Integer userid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.venues_dialog,container,false);
         init(view);
        collectionses=new ArrayList<>();
        venuesAllocations=new ArrayList<>();
        GetinfoThread();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             if(allocationid==-1){
                 Toast.makeText(context,"请选择场馆",Toast.LENGTH_SHORT).show();
             }else
              venuesData.get(userid,allocationid,venuesid,time,money,venueaname,allocation,opentime,closetime);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    public void getVenuesData(GetVenuesData venuesData) {
        this.venuesData = venuesData;
    }

    void init(View view){
        venues=(Spinner) view.findViewById(R.id.venues);
        venuesaollection=(Spinner) view.findViewById(R.id.venuesaollection);
        submit=view.findViewById(R.id.submit);
        cancel=view.findViewById(R.id.cancel);
        info=view.findViewById(R.id.info);
    }

    void GetinfoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Controller.getVenuesinfo;
                Map map = new HashMap();
                map.put("userid", 25);
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


    void AllocationinfoThread(final Integer id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getAllocationinfo;
                Map map=new HashMap();
                map.put("venuesid",id);
                Bundle bundle=new Bundle();
                String mes=null;
                try {
                    mes=new Dopost().dopost(url,map);
                    bundle.putString("info",mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes!=null){
                    Message message=new Message();
                    message.what= StataCode.getMoreallocation;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else {
                    Message message=new Message();
                    message.what= StataCode.net_faile;
                    //  message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        }).start();
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
                    final int position = collectionses.size();
                    collectionses.addAll(collectionsDataGrid.getRows());
                    myadapt=new Myadapt(collectionses);
                    venues.setAdapter(myadapt);
                    venues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                           venuesid=collectionses.get(i).getVenuesid();
                            venueaname=collectionses.get(i).getVenues().getVenuesName();
                           AllocationinfoThread( collectionses.get(i).getVenuesid());
                            opentime=collectionses.get(i).getVenues().getOpenTime();
                            closetime=collectionses.get(i).getVenues().getCloseTime();
                            userid=collectionses.get(i).getVenues().getVenuesUserCharge();
                            try {
                                time= TimeUtils.TimeDifference(collectionses.get(i).getVenues().getOpenTime(),collectionses.get(i).getVenues().getCloseTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    break;
                case StataCode.net_faile:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.getMoreallocation:
                    DataGrid<VenuesAllocation> venuesAllocationDataGrid;
                    venuesAllocationDataGrid = gson.fromJson(bundle.getString("info"), new TypeToken<DataGrid<VenuesAllocation>>() {
                    }.getType());
                    venuesAllocations.clear();
                    allocationid=-1;

                    venuesAllocations.addAll(venuesAllocationDataGrid.getRows());
                    if(venuesAllocations.size()==0){
                        venuesaollection.setVisibility(View.GONE);
                        info.setVisibility(View.VISIBLE);
                    }else{
                        venuesaollection.setVisibility(View.VISIBLE);
                        info.setVisibility(View.GONE);
                }
                    allocationadapt=new MyAllocationadapt(venuesAllocations);
                    venuesaollection.setAdapter(allocationadapt);
                    venuesaollection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            money=venuesAllocations.get(i).getFeeScale();
                            allocationid=venuesAllocations.get(i).getVenuesAllocationId();
                            if (venuesAllocations.get(i).getVenuesConfiguration() == 1) {
                                allocation="豪华厅";

                            } else {
                                allocation="普通厅";
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    break;
            }
        }
    };
    public interface GetVenuesData{
        void get(int userid,int allocationid,
                 int venuesid,
                 int time,
                 BigDecimal money,
                 String venuesname,String alloactionname,
                 String opentime,String closetime
                 );
    }
}


    class  Myadapt extends BaseAdapter {
        List<Collections> a;

        public Myadapt(List<Collections> a) {
            this.a = a;
        }

        @Override
        public int getCount() {
            return a.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_tiem, null);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(a.get(position).getVenues().getVenuesName());
            TextView detail = (TextView) view.findViewById(R.id.detail);
            detail.setVisibility(View.GONE);
            return view;

        }

    }


class  MyAllocationadapt extends BaseAdapter {
    List<VenuesAllocation> a;

    public MyAllocationadapt(List<VenuesAllocation> a) {
        this.a = a;
    }

    @Override
    public int getCount() {
        return a.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_tiem, null);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView detail = (TextView) view.findViewById(R.id.detail);
            if (a.get(position).getVenuesConfiguration() == 1) {
                name.setText("豪华厅");

            } else {
                name.setText("普通厅");

            }
            detail.setText(a.get(position).getFeeScale()+"元/小时");
            //detail.setVisibility(View.GONE);
        return view;

    }

}

