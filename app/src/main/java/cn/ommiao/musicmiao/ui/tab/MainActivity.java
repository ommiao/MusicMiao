package cn.ommiao.musicmiao.ui.tab;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.MainTabPagerAdapter;
import cn.ommiao.musicmiao.databinding.ActivityMainBinding;
import cn.ommiao.musicmiao.ui.BaseActivity;
import cn.ommiao.musicmiao.ui.BaseFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private ArrayList<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        fragments.add(new SearchFragment());
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
}
