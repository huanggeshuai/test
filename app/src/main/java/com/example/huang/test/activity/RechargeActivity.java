package com.example.huang.test.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.adapt.DemoAdapter;

import com.example.huang.test.entity.ItemModel;
import com.example.huang.test.entity.Orders;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.UserBank;
import com.example.huang.test.utils.Payutils;
import com.example.huang.test.utils.StataCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class RechargeActivity extends AppCompatActivity   {
    private List<UserBank> userBanks;
    private RecyclerView recyclerView;
    private DemoAdapter adapter;
    private Button recharge;
    private String val;
    private Spinner bankspinner;
    private TextView rechargemoney;
    int flag=-1;
    User fromatuser;
    private String[] mVals = new String[]
            {"3", "10", "20", "50", "100","200","500","1000"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
         initview();
    }
    private void initview() {
        rechargemoney=(TextView) findViewById(R.id.rechargemoney);
        fromatuser=(User) getIntent().getSerializableExtra("user");
        userBanks=(List<UserBank>) getIntent().getSerializableExtra("userbank");
        if(userBanks.size()==0){
            Toast.makeText(RechargeActivity.this,"请先绑定银行卡",Toast.LENGTH_SHORT).show();
            finish();
        }
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("充值");
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recharge = (Button) findViewById(R.id.recharge);
        bankspinner=(Spinner) findViewById(R.id.bankspinner);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter = new DemoAdapter());
        adapter.replaceAll(getData());
        bankspinner.setAdapter(new Myadapt(userBanks));
        adapter.GetPosition(new DemoAdapter.GetPosition() {
            @Override
            public void getPosition(int postion) {
                Log.e("e",postion+"");
                flag=postion;
                if(flag!=-1){
                    rechargemoney.setText(mVals[flag]);
                }else {
                    if(TextUtils.isEmpty(val)){
                        rechargemoney.setText("");
                    }else {
                        rechargemoney.setText(val);
                    }
                }
            }
            @Override
            public void getValue(String vale) {
                Log.e("e",vale+"");
                val=vale;
                if(TextUtils.isEmpty(val)){
                    if(flag==-1)
                    rechargemoney.setText("");
                    else {
                        rechargemoney.setText(mVals[flag]);
                    }
                }else {
                    rechargemoney.setText(vale);
                }
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user=new User();
                if(TextUtils.isEmpty(rechargemoney.getText())){
                    user.setUserBalance(new BigDecimal(0));
                }
                else {
                    user.setUserBalance(new BigDecimal(rechargemoney.getText().toString()));
                }
                user.setUserid(fromatuser.getUserid());
                Orders orders=new Orders();
                orders.setIncomeState(1);
                orders.setMedium(StataCode.card);
                orders.setIncomeUserId(3);
                Payutils payutils=new Payutils(user,orders,getApplicationContext());
                payutils.Paydialog(getFragmentManager());
                payutils.getIstrue(new Payutils.isTrue() {
                    @Override
                    public void istrue(boolean flag) {
                        if(flag){
                            Toast.makeText(RechargeActivity.this,"充值成功",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            intent.putExtra("success",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else {
                            Toast.makeText(RechargeActivity.this,"充值失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    public ArrayList<ItemModel> getData() {
        ArrayList<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < mVals.length; i++) {
            String count = mVals[i] + "元";
            list.add(new ItemModel(ItemModel.ONE, count));
        }
        list.add(new ItemModel(ItemModel.TWO, null));

        return list;
    }

    class  Myadapt extends BaseAdapter {
       List<UserBank> a;

        public Myadapt(List<UserBank> a) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_item, null);
            TextView banknumber,cause,banktime;
            LinearLayout linearLayout;
            linearLayout=view.findViewById(R.id.line1);
            linearLayout.setVisibility(View.GONE);
            banknumber=view.findViewById(R.id.banknumber);
            cause=view.findViewById(R.id.cause);
            banktime=view.findViewById(R.id.banktime);
            banktime.setVisibility(View.GONE);
            UserBank userBank=a.get(position);
          //  banktime.setText(TimeUtils.ConvertYYMMDD(userBank.getAddtime()));
            banknumber.setText(userBank.getUserBankNumber());
            cause.setText(userBank.getCause());
            return view;
        }
    }

}
