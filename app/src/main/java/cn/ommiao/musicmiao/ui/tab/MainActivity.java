package cn.ommiao.musicmiao.ui.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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

public class MainActivity extends BaseActivity<ActivityMainBinding> implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, SearchFragment.StartDetailFragmentListener {

    private ArrayList<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setStartDetailFragmentListener(this);
        fragments.add(searchFragment);
        fragments.add(new DownloadFragment());
        mBinding.viewPager.setAdapter(new MainTabPagerAdapter(getSupportFragmentManager(), fragments));
        mBinding.viewPager.addOnPageChangeListener(this);
        mBinding.bottomBar.setOnNavigationItemSelectedListener(this);
        mBinding.bottomBar.setItemIconSizeRes(R.dimen.tab_icon);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Menu menu = mBinding.bottomBar.getMenu();
        int id = menu.getItem(i).getItemId();
        mBinding.bottomBar.setSelectedItemId(id);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mBinding.viewPager.setCurrentItem(menuItem.getOrder());
        return true;
    }

    @Override
    public void startDetailFragment(View view, Song song) {
        Bundle bundle = new Bundle();
        MusicDetailFragment detailFragment = new MusicDetailFragment();
        bundle.putString("url", song.getAlbumImageUrl());
        bundle.putString("tran_name", Objects.requireNonNull(ViewCompat.getTransitionName(view)));
        detailFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(view, Objects.requireNonNull(ViewCompat.getTransitionName(view)))
                .addToBackStack("detail")
                .replace(R.id.content, detailFragment)
                .commit();
    }
}
