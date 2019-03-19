package cn.ommiao.network;

import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class SimpleRequestCallback<D extends RequestOutBase> implements RequestCallBack<D> {

    @Override
    public void onSuccess(D res, String str, Response<ResponseBody> result) {
        onSuccess(res);
    }

    @Override
    public void onError(String message, @Nullable Throwable err) {
        onError(message);
    }

    @Override
    public void onCancel() {

    }

    public abstract void onSuccess(D out);

    public abstract void onError(String error);

}
