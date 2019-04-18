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

import java.util.HashMap;

import cn.ommiao.musicmiao.interfaces.OnBackPressedListener;
import cn.ommiao.network.BaseRequest;
import cn.ommiao.network.HttpCall;
import cn.ommiao.network.RequestCallBack;
import cn.ommiao.network.RequestInBase;
import cn.ommiao.network.RequestOutBase;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment implements OnBackPressedListener, HttpCall {

    public static final boolean LOADING = true;
    public static final boolean NO_LOADING = false;

    private HashMap<RequestInBase, RequestCallBack<? extends RequestOutBase>> callBacks = new HashMap<>();
    private HashMap<String, BaseRequest<? extends RequestInBase, ? extends RequestOutBase>> requests = new HashMap<>();

    protected BaseActivity mActivity;
    protected T mBinding;

    private boolean needInitData = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mBinding != null){
            needInitData = false;
        } else {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
            immersionBar();
            initViews();
            needInitData = true;
        }
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(needInitData){
            initData();
        }
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
        clearRequest();
        ImmersionBar.with(this).destroy();
    }

    private void clearRequest() {
        callBacks.clear();
        for(BaseRequest request : requests.values()){
            request.cancel();
        }
    }

    @Override
    public <OUT extends RequestOutBase> RequestCallBack<OUT> arrangeCallback(final String url, final RequestInBase in, final RequestCallBack<OUT> callBack) {
        RequestCallBack<OUT> temp = new RequestCallBack<OUT>() {
            @Override
            public void onSuccess(OUT result, String str, Response<ResponseBody> res) {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onSuccess(result, str, res);
            }

            @Override
            public void onError(int code, String message, @Nullable Throwable err) {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onError(code,  message, err);
            }

            @Override
            public void onCancel() {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onCancel();
            }
        };
        callBacks.put(in, temp);
        return temp;
    }

    @Override
    public <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, IN in, RequestCallBack<OUT> callBack) {
        newCall(request, NO_LOADING, null, in, callBack);
    }

    @Override
    public <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, IN in, RequestCallBack<OUT> callBack) {
        newCall(request, showLoading, null, in, callBack);
    }

    @Override
    public <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, String msg, IN in, RequestCallBack<OUT> callBack) {
        if(showLoading){
            showLoading(msg);
        }
        request.params(in).build(arrangeCallback(request.getUrl(), in, callBack));
        BaseRequest old = requests.put(request.getUrl(), request.call());
        if (old != null) {
            RequestCallBack oldCallback = callBacks.remove(old.getParam());
            if (oldCallback != null) {
                //won't receive http callback.
                old.clearCallback();
                //send cancel callback.
                oldCallback.onCancel();
            }
        }
    }

    private void showLoading(String msg) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean interceptBackAction() {
        return false;
    }
}
