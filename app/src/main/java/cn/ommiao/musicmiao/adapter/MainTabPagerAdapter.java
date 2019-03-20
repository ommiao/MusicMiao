package cn.ommiao.musicmiao.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import cn.ommiao.musicmiao.ui.base.BaseFragment;

public class MainTabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> fragments;

    public MainTabPagerAdapter(FragmentManager fm, @NonNull ArrayList<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
