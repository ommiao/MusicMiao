package cn.ommiao.network;


import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public abstract class BaseRequest<IN extends RequestInBase, OUT extends RequestOutBase> {

    protected static final int POST = 0;
    protected static final int GET = 1;

    public static final String BASE_URL = "http://c.y.qq.com/";

    private final Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).callFactory(Client.CLIENT);
    private Retrofit retrofit = builder.build();
    private Call<ResponseBody> call;
    protected String url;
    private RequestCallBack<OUT> callBackHolder;//this is meant to prevent from being recycled.
    private boolean holdCallback = false;
    private IN param;

    private WeakReference<RequestCallBack<OUT>> callBackWeakReference;

    public BaseRequest<IN, OUT> params(final IN param) {
        this.param = param;
        if (retrofit == null) {
            retrofit = builder.build();
        }
        final BaseInterface interf = retrofit.create(BaseInterface.class);
        HashMap<String, String> params = param.toHashMap();
        if(method() == POST){
            call = interf.postCall(getRealApi(api(), params));
        } else {
            call = interf.getCall(getRealApi(api(), params));
        }
        url = call.request().url().toString();
        Logger.d("--->" + url + " | params : " + params.toString());
        return this;
    }

    private String getRealApi(String pattern, HashMap<String, String> originParams) {
        String realApi = pattern;
        for (String key : originParams.keySet()){
            String k = "{" + key + "}";
            realApi = realApi.replace(k, originParams.get(key));
        }
        return realApi;
    }


    public BaseRequest<IN, OUT> build(RequestCallBack<OUT> callBack) {
        if (callBack == null) {
            return this;
        }
        callBackWeakReference = new WeakReference<>(callBack);
        if (holdCallback) {
            callBackHolder = callBack;
        }
        return this;
    }

    public BaseRequest<IN, OUT> call() {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                dealResponse(false, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dealError(call, t);
            }
        });
        return this;
    }

    public BaseRequest<IN, OUT> callSync() {
        try {
            Response<ResponseBody> responseBody = call.execute();
            dealResponse(true, responseBody);
        } catch (IOException e) {
            dealError(call, e);
        }
        return this;
    }

    private void dealResponse(boolean sync, Response<ResponseBody> response) {
        RequestCallBack<OUT> requestCallBack = callBackWeakReference.get();
        if (requestCallBack != null) {
            ResponseBody body = response.body();
            Logger.d("<---" + url + " | code : " + response.code());
            if (response.isSuccessful() && body != null) {
                try {
                    String res = body.string();
                    Logger.d("<---" + url + " \nresponse : " + res);
                    OUT out = OUT.fromJson(res, outClass());
                    if (out != null) {
                        int errorCode = out.getCode();
                        switch (errorCode) {
                            case ErrorCodes.SUCCESS:
                                requestCallBack.onSuccess(out, res, response);
                                break;
                            default:
                                requestCallBack.onError(out.getMessage(), null);
                                break;
                        }
                    } else {
                        requestCallBack.onError("通信异常", null);
                    }

                } catch (Exception e) {
                    Logger.e("Exception", e);
                    requestCallBack.onError("通信异常", e);
                }
            } else {
                requestCallBack.onError("网络错误 " + response.code(), null);
            }
        }
    }

    private void dealError(Call<ResponseBody> call, Throwable throwable) {
        RequestCallBack<OUT> requestCallBack = callBackWeakReference.get();
        if (requestCallBack != null) {
            requestCallBack.onError("网络错误！", throwable);
        }
    }

    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            callBackWeakReference.clear();
        }
    }

    public boolean canceled() {
        return call.isCanceled();
    }

    public void clearCallback() {
        this.callBackWeakReference.clear();
    }

    public IN getParam() {
        return param;
    }

    protected int method(){
        return POST;
    }

    protected abstract String api();

    protected abstract Class<OUT> outClass();

    /**
     *
     */
    public BaseRequest<IN, OUT> holdCallback() {
        holdCallback = true;
        return this;
    }

    public boolean match(BaseRequest another) {
        return url != null && url.equals(another.getUrl());
    }

    public String getUrl() {
        return url;
    }

    interface BaseInterface {

        @POST
        Call<ResponseBody> postCall(
                @Url String url
        );

        @GET
        Call<ResponseBody> getCall(
                @Url String url
        );
    }

}
