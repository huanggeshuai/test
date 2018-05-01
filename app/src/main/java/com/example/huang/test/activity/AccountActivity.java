package com.example.huang.test.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.adapt.BankAdapt;
import com.example.huang.test.dialog.AddBankDialog;
import com.example.huang.test.dialog.FingeridDialog;
import com.example.huang.test.dialog.PaypasswordDialog;
import com.example.huang.test.entity.DataGrid;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.User;
import com.example.huang.test.entity.UserBank;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.RaiseNumberAnimTextView;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    RecyclerView bank;
    TextView tips;
    RaiseNumberAnimTextView balance;
    User user;
    JellyToggleButton fingerbutton;
    List<UserBank> userBanks;
    BankAdapt bankAdapt;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        init();
        if(TextUtils.isEmpty(user.getUserPaypassword())){
            showPayPasswordDialog(StataCode.first_paypassword);
        }
        pref =getSharedPreferences("finger",MODE_PRIVATE);
        fingerbutton.setChecked(pref.getBoolean("isfinger",false));
        fingerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fingerbutton.isChecked()){
                    final FingeridDialog fingeridDialog=new FingeridDialog(getApplicationContext());
                    fingeridDialog.show(getFragmentManager(),"");
                    fingeridDialog.setCancelable(false);
                    fingeridDialog.istrue(new FingeridDialog.isTrue() {
                        @Override
                        public void istrue(boolean flag) {
                            if(flag){
                                editor.putBoolean("isfinger",true);
                                editor.apply();
                                Toast.makeText(AccountActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                                fingeridDialog.dismiss();
                            }else {
                                editor.putBoolean("isfinger",false);
                                editor.apply();
                                Toast.makeText(AccountActivity.this,"请稍后再试",Toast.LENGTH_SHORT).show();
                                fingeridDialog.dismiss();
                                fingerbutton.setChecked(false);
                            }
                        }
                    });
                    //Toast.makeText(AccountActivity.this,"你开启了",Toast.LENGTH_SHORT).show();
                }else {
                    editor.putBoolean("isfinger",false);
                    editor.apply();
                  //  Toast.makeText(AccountActivity.this,"你关闭了",Toast.LENGTH_SHORT).show();
                }
            }
        });
        GetUserinfo();
    }
    void init(){
        balance=(RaiseNumberAnimTextView) findViewById(R.id.balance);
        editor=getSharedPreferences("finger",MODE_PRIVATE).edit();
        tips=(TextView) findViewById(R.id.tips);
        fingerbutton=(JellyToggleButton) findViewById(R.id.fingerswitch);
        user=(User) getIntent().getSerializableExtra("user");
        bank=(RecyclerView) findViewById(R.id.bank);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }
        actionBar.setTitle("账户管理");
        userBanks=new ArrayList<>();
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        bank.setLayoutManager(layoutManager);
        getBankInfo();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accountmenu,menu);
        return true;
    }
    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.addbanck:
                    showAddBankDialog();
                    break;
                case R.id.changepassword:
                    showPayPasswordDialog(StataCode.change_paypassword);
                    break;
                case R.id.charge:
                    Intent intent=new Intent(AccountActivity.this,RechargeActivity.class);
                    intent.putExtra("userbank", (Serializable) userBanks);
                    intent.putExtra("user",user);
                    startActivityForResult(intent,StataCode.accountmoney);
                    break;
                case android.R.id.home:
                    finish();
                    break;
                default:
            }
            return true;
    }
  void showPayPasswordDialog(Integer flag){
      final PaypasswordDialog paypasswordDialog=new PaypasswordDialog(getApplicationContext(),user,flag);
      paypasswordDialog.show(getFragmentManager(),"");
      paypasswordDialog.setCancelable(false);
      paypasswordDialog.istrue(new PaypasswordDialog.isTrue() {
          @Override
          public void istrue(boolean flag) {
              if(flag){
                  Toast.makeText(AccountActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                  paypasswordDialog.dismiss();
              }
              else {
                  Toast.makeText(AccountActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
              }
          }
      });
  }
  void showAddBankDialog(){
      final AddBankDialog addBankDialog=new AddBankDialog(getApplicationContext(),user);
      addBankDialog.show(getFragmentManager(),"");
      addBankDialog.setCancelable(false);
      addBankDialog.getIstrue(new AddBankDialog.isTrue() {
          @Override
          public void istrue(boolean flag) {
              if(flag){
                  Toast.makeText(AccountActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                  addBankDialog.dismiss();
                  userBanks.clear();
                  getBankInfo();
              }else {
                  Toast.makeText(AccountActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
              }
          }
      });
  }
  void getBankInfo(){
      new Thread(new Runnable() {
          @Override
          public void run() {
              String url = Controller.getBankinfo;
              Map map = new HashMap();
              map.put("userId",user.getUserid());
              String mes = null;
              Bundle bundle = new Bundle();
              try {
                  mes = new Dopost().dopost(url, map);
                  bundle.putString("bankinfo", mes);
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            DataGrid<UserBank> dataGrid = new DataGrid();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.net_faile:
                    Toast.makeText(AccountActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();

                    break;
                case StataCode.net_success:
                   dataGrid=gson.fromJson(bundle.getString("bankinfo"), new TypeToken<DataGrid<UserBank>>() {
                   }.getType());
                    userBanks.addAll(dataGrid.getRows());
                    if(userBanks.size()>0){
                        tips.setVisibility(View.GONE);
                        bank.setVisibility(View.VISIBLE);
                    }else {
                        tips.setVisibility(View.VISIBLE);
                        bank.setVisibility(View.GONE);
                    }
                     bankAdapt=new BankAdapt(userBanks);
                    bank.setAdapter(bankAdapt);
                    break;
                case StataCode.getUserinfo:
                    jsonInfo=gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user=gson.fromJson(jsonInfo.getMsg(),User.class);
                    balance.setNumberWithAnim(user.getUserBalance().floatValue());
                    // Toast.makeText(AccountActivity.this, TotalPrice+"", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case StataCode.accountmoney:
                if(resultCode==RESULT_OK){
                    GetUserinfo();
                }
                break;
        }
    }
    void GetUserinfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url= Controller.getUserinfobyid;
                Map map=new HashMap();
                map.put("userid", user.getUserid());
                Bundle bundle=new Bundle();
                String mes=null;
                try {
                    mes=new Dopost().dopost(url,map);
                    bundle.putString("userinfo",mes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mes!=null){
                    Message message=new Message();
                    message.what= StataCode.getUserinfo;
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
}
