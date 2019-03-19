package cn.ommiao.network;

import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Response;


public interface RequestCallBack<D extends RequestOutBase> {
    void onSuccess(D res, String str, Response<ResponseBody> result);
    void onError(String message, @Nullable Throwable err);
    void onCancel();
}
