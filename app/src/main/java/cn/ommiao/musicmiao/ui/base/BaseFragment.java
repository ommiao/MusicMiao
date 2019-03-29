package cn.ommiao.musicmiao.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.musicmiao.interfaces.OnBackPressedListener;
import cn.ommiao.network.BaseRequest;
import cn.ommiao.network.RequestCallBack;
import cn.ommiao.network.RequestInBase;
import cn.ommiao.network.RequestOutBase;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment implements OnBackPressedListener {

    protected BaseActivity mActivity;
    protected T mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        immersionBar();
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    protected void initData() {

    }

    protected void immersionBar() {
        ImmersionBar.with(this).init();
    }

    protected abstract void initViews();

    protected abstract @LayoutRes int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    protected <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, IN in, RequestCallBack<OUT> callBack) {
        mActivity.newCall(request, in, callBack);
    }

    protected <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, IN in, RequestCallBack<OUT> callBack) {
        mActivity.newCall(request, showLoading, in, callBack);
    }

    protected  <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, String msg, IN in, RequestCallBack<OUT> callBack) {
        mActivity.newCall(request, showLoading, msg, in, callBack);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean interceptBackAction() {
        return false;
    }
}
