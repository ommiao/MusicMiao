package cn.ommiao.musicmiao.ui.tab;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.ActivityMainBinding;
import cn.ommiao.musicmiao.ui.base.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, searchFragment)
                .commit();
    }

    @Override
    protected void initData() {

    }

}
