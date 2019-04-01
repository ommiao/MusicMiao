package cn.ommiao.musicmiao.ui.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.orhanobut.logger.Logger;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.ActivityMainBinding;
import cn.ommiao.musicmiao.ui.base.BaseActivity;
import cn.ommiao.musicmiao.utils.ToastUtil;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements CustomDialogFragment.OnClickActionListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, searchFragment)
                .commit();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermission();
        }
    }

    private void requestPermission() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_download_permission_tips));
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(this);
        customDialogFragment.show(getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onLeftClick() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ToastUtil.show("读写存储权限已被禁止并不再询问，请手动打开读写存储权限！");
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Logger.d("Permission Granted.");
        } else{
            ToastUtil.show("读写存储权限被拒绝！");
        }
    }
}
