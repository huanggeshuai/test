package com.example.huang.test.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huang.test.R;
import com.example.huang.test.dialog.EditName;
import com.example.huang.test.entity.JsonInfo;
import com.example.huang.test.entity.User;
import com.example.huang.test.fragment.Frag3;
import com.example.huang.test.fragment.Frag5;
import com.example.huang.test.net.Controller;
import com.example.huang.test.utils.Dopost;
import com.example.huang.test.utils.GsonBuilderUtil;
import com.example.huang.test.utils.StataCode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    User user;
    FrameLayout fragment;
    View view;
    private Fragment[] fragments;
    private int lastShowFragment = 0;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener clic
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_news:
                    if (lastShowFragment != 0) {
                        switchFrament(lastShowFragment, 0);
                        lastShowFragment = 0;
                    }
                    return true;
                case R.id.item_lib:
                    if (lastShowFragment != 1) {
                        switchFrament(lastShowFragment, 1);
                        lastShowFragment = 1;
                    }

                    return true;
//                case R.id.item_find:
//                    if (lastShowFragment != 2) {
//                        switchFrament(lastShowFragment, 2);
//                        lastShowFragment = 2;
//                    }
//                    return true;  //break;
//                case R.id.item_more:
//                    if (lastShowFragment != 3) {
//                        switchFrament(lastShowFragment, 3);
//                        lastShowFragment = 3;
//                    }
//                    return true;
            }
            return false;

        }

    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Gson gson = GsonBuilderUtil.create();
            JsonInfo jsonInfo = new JsonInfo();
            switch (msg.what) {
                case StataCode.net_faile:
                    Toast.makeText(MainActivity.this, "网络超时，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case StataCode.getUserinfo:
                    jsonInfo = gson.fromJson(bundle.getString("userinfo"), JsonInfo.class);
                    user = gson.fromJson(jsonInfo.getMsg(), User.class);
                    TextView username = view.findViewById(R.id.username);
                    TextView email = view.findViewById(R.id.emil);
                    username.setText(user.getUserNickname());
                    email.setText(user.getUserEmail());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (User) getIntent().getSerializableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.draw);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        fragment = (FrameLayout) findViewById(R.id.frame);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bott_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(clic);
        initFragments();
        navigationView.setNavigationItemSelectedListener(this);
        view= navigationView.inflateHeaderView(R.layout.nav_header);
        //  mythread.start();
        //  Log.d("3423",mythread.getMess());
        if(TextUtils.isEmpty(user.getUserTruename())){
            dialog();
        }else {
            TextView username= view.findViewById(R.id.username);
            TextView email= view.findViewById(R.id.emil);
            username.setText(user.getUserNickname());
            email.setText(user.getUserEmail());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private void initFragments() {

        Frag5 one = new Frag5(getApplicationContext(), user);
        Frag3 one2 = new Frag3(getApplicationContext(),user);
      //  Frag3 one3 = new Frag3(getApplicationContext());
       // Frag4 one4 = new Frag4(getApplicationContext(),user);
        fragments = new Fragment[]{one, one2};
        lastShowFragment = 0;
        getFragmentManager().beginTransaction().add(R.id.frame, one).show(one).commit();
    }

    public void switchFrament(int lastIndex, int index) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.hide(fragments[lastIndex]);

        if (!fragments[index].isAdded()) {

            transaction.add(R.id.frame, fragments[index]);
        }

        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                Intent intent=new Intent(MainActivity.this,AccountActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_pay:
                intent=new Intent(MainActivity.this,PayActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_venuesactivity:
                intent=new Intent(MainActivity.this,VenuesActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_usercustomact:
                intent=new Intent(MainActivity.this,UserCustomActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_usercustomactmanager:
                intent=new Intent(MainActivity.this,UserCustomerManagerActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_joincustomeract:
                intent=new Intent(MainActivity.this,UserjoinCustomerActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                drawerLayout.closeDrawers();
                break;
            case R.id.switch_account:
//                intent=new Intent(MainActivity.this,UserjoinCustomerActivity.class);
//                intent.putExtra("user",user);
//                startActivity(intent);
//                drawerLayout.closeDrawers();
                SharedPreferences.Editor editor = getSharedPreferences("logininfo", MODE_PRIVATE).edit();
                editor.clear().commit();
                SharedPreferences.Editor editorpass = getSharedPreferences("finger", MODE_PRIVATE).edit();
                editorpass.clear().commit();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    void dialog() {
        final EditName name=new EditName(getApplicationContext(),user.getUserid());
        name.show(getFragmentManager(),"");
        name.setCancelable(false);
        name.getIstrue(new EditName.isTrue() {
            @Override
            public void istrue(boolean flag) {
                if(flag){
               GetUserinfo();
                    name.dismiss();
                }
            }
        });
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