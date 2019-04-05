package cn.ommiao.musicmiao.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

import java.util.HashMap;

import cn.ommiao.musicmiao.interfaces.OnBackPressedListener;
import cn.ommiao.network.BaseRequest;
import cn.ommiao.network.RequestCallBack;
import cn.ommiao.network.RequestInBase;
import cn.ommiao.network.RequestOutBase;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected T mBinding;

    private static final long DEFAULT_TIP_DURATION = 2000;
    public static final boolean LOADING = true;
    public static final boolean NO_LOADING = false;

    private HashMap<RequestInBase, RequestCallBack<? extends RequestOutBase>> callBacks = new HashMap<>();
    private HashMap<String, BaseRequest<? extends RequestInBase, ? extends RequestOutBase>> requests = new HashMap<>();

    private OnBackPressedListener onBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        immersionBar();
        initViews();
        initData();
    }

    protected abstract @LayoutRes int getLayoutId();

    protected void immersionBar(){
        ImmersionBar.with(this).init();
    }

    protected abstract void initViews();

    protected abstract void initData();

    @Override
    public void onBackPressed() {
        if(onBackPressedListener != null){
            if(onBackPressedListener.interceptBackAction()){
                onBackPressedListener.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callBacks.clear();
        requests.clear();
        ImmersionBar.with(this).destroy();
    }

    protected void showLoading(String msg){

    }

    protected void hideLoading(){

    }

    private <OUT extends RequestOutBase> RequestCallBack<OUT> arrangeCallback(final String url, final RequestInBase in, final RequestCallBack<OUT> callBack) {
        RequestCallBack<OUT> temp = new RequestCallBack<OUT>() {
            @Override
            public void onSuccess(OUT result, String str, Response<ResponseBody> res) {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onSuccess(result, str, res);
                hideLoading();
            }

            @Override
            public void onError(int code, String message, @Nullable Throwable err) {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onError(code,  message, err);
                hideLoading();
            }

            @Override
            public void onCancel() {
                callBacks.remove(in);
                requests.remove(url);
                callBack.onCancel();
                hideLoading();
            }
        };
        callBacks.put(in, temp);
        return temp;
    }

    protected <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, IN in, RequestCallBack<OUT> callBack) {
        newCall(request, NO_LOADING, null, in, callBack);
    }

    protected <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, IN in, RequestCallBack<OUT> callBack) {
        newCall(request, showLoading, null, in, callBack);
    }

    protected  <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, String msg, IN in, RequestCallBack<OUT> callBack) {
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
}
