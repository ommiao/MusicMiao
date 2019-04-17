package cn.ommiao.musicmiao.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.orhanobut.logger.Logger;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.ActivitySplashBinding;
import cn.ommiao.musicmiao.ui.base.BaseActivity;
import cn.ommiao.musicmiao.ui.music.CustomDialogFragment;
import cn.ommiao.musicmiao.ui.music.MainActivity;
import cn.ommiao.musicmiao.utils.ToastUtil;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> implements CustomDialogFragment.OnClickActionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        if(ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermission();
        } else {
            startMainActivity();
        }
    }

    @Override
    protected void initData() {

    }

    private void requestPermission() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_download_permission_tips));
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(this);
        customDialogFragment.show(getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onLeftClick() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        if(ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ToastUtil.show("读写存储权限已被禁止并不再询问，请手动打开读写存储权限！");
        }
    }

    @Override
    public void onRightClick() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Logger.d("Permission Granted.");
            startMainActivity();
        } else{
            ToastUtil.show("读写存储权限被拒绝！");
        }
    }
}
