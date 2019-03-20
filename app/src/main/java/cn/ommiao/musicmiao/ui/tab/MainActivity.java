package cn.ommiao.musicmiao.ui.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.Fade;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.MainTabPagerAdapter;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.databinding.ActivityMainBinding;
import cn.ommiao.musicmiao.ui.base.BaseActivity;
import cn.ommiao.musicmiao.ui.base.BaseFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements BottomNavigationView.OnNavigationItemSelectedListener, SearchFragment.StartDetailFragmentListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setStartDetailFragmentListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, searchFragment)
                .commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return true;
    }

    @Override
    public void startDetailFragment(View view, Song song) {

    }
}
