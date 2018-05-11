package com.example.huang.test.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huang.test.R;
import com.example.huang.test.adapt.MyPagerAdapter;
import com.example.huang.test.entity.User;

import java.util.ArrayList;

/**
 * Created by huang on 2018/4/17.
 */

public class Frag5 extends Fragment {
    Context context;
    ViewPager viewPager;
    TabLayout tableLayout;
    User user;

    public Frag5(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fifth, container, false);
        init(view);
        initViewPager();
        return view;
    }

    void init(View view) {
        viewPager = view.findViewById(R.id.viewpage);
        tableLayout = view.findViewById(R.id.tablayout);
    }

    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        //   fragments.add(new Frag1(context,null));
        fragments.add(new Frag1(context, user));
        fragments.add(new Frag4(context,user));
        //fragments.add(new Frag4(context));
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        viewPager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        //  tableLayout.addTab(tableLayout.newTab());
        tableLayout.addTab(tableLayout.newTab());
        tableLayout.addTab(tableLayout.newTab());
        tableLayout.addTab(tableLayout.newTab());
        // 使用 TabLayout 和 ViewPager 相关联
        tableLayout.setupWithViewPager(viewPager);
        // TabLayout指示器添加文本
        //  tableLayout.getTabAt(0).setText("头条");
        tableLayout.getTabAt(1).setText("地图显示");
        //  tableLayout.getTabAt(2).setText("娱乐");
        tableLayout.getTabAt(0).setText("场馆活动");
    }

}
