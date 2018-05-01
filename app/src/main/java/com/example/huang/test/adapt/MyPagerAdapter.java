package com.example.huang.test.adapt;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 2018/4/17.
 */

public class MyPagerAdapter extends com.example.huang.test.adapt.FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        mFragmentList = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = mFragmentList.get(position);

        return fragment;
    }

    @Override
    public int getCount() {

        return mFragmentList.size();
    }
}