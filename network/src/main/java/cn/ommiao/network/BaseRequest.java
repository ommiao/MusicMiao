package cn.ommiao.network;


import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public abstract class BaseRequest<IN extends RequestInBase, OUT extends RequestOutBase> {

    protected static final int POST = 0;
    protected static final int GET = 1;

    private static final String DEFAULT_BASE_URL = "http://c.y.qq.com/";

    private Retrofit.Builder builder;
    private Retrofit retrofit;
    private Call<ResponseBody> call;
    protected String url;
    private RequestCallBack<OUT> callBackHolder;//this is meant to prevent from being recycled.
    private boolean holdCallback = false;
    private IN param;

    private WeakReference<RequestCallBack<OUT>> callBackWeakReference;

    public BaseRequest<IN, OUT> params(final IN param) {
        initRetrofit();
        this.param = param;
        if (retrofit == null) {
            retrofit = builder.build();
        }
        final BaseInterface interf = retrofit.create(BaseInterface.class);
        HashMap<String, String> params = param.toHashMap();
        if(method() == POST){
            call = interf.postCall(headers(), getRealApi(api(), params), getRequestBody(param.body()));
        } else {
            call = interf.getCall(headers(), getRealApi(api(), params));
        }
        url = call.request().url().toString();
        RequestBody body = call.request().body();
        Logger.d("--->" + url + " | params : " + params.toString());
        return this;
    }

    private RequestBody getRequestBody(HashMap<String, String> hashMap) {
        StringBuilder data = new StringBuilder();
        if (hashMap != null && hashMap.size() > 0) {
            Iterator iter = hashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                data.append(key).append("=").append(val).append("&");
            }
        }

        String jso = "";

        if(data.length() > 0){
            jso = data.substring(0, data.length() - 1);
        }

        return RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), jso);
    }

    private void initRetrofit() {
        String baseUrl = baseUrl() == null ? DEFAULT_BASE_URL : baseUrl();
        builder = new Retrofit.Builder().baseUrl(baseUrl).callFactory(Client.CLIENT);
        retrofit = builder.build();
    }

    protected String baseUrl(){
        return null;
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
                    res = extraHandle(res);
                    OUT out = OUT.fromJson(res, outClass());
                    if (out != null) {
                        int errorCode = out.getCode();
                        switch (errorCode) {
                            case ErrorCodes.SUCCESS:
                                requestCallBack.onSuccess(out, res, response);
                                break;
                            default:
                                requestCallBack.onError(errorCode, out.getMessage(), null);
                                break;
                        }
                    } else {
                        requestCallBack.onError(-9997, "通信异常", null);
                    }

                } catch (Exception e) {
                    Logger.e("Exception", e);
                    requestCallBack.onError(-9998, "通信异常", e);
                }
            } else {
                requestCallBack.onError(-9999, "网络错误 " + response.code(), null);
            }
        }
    }

    protected String extraHandle(String res) {
        return res;
    }

    private void dealError(Call<ResponseBody> call, Throwable throwable) {
        RequestCallBack<OUT> requestCallBack = callBackWeakReference.get();
        if (requestCallBack != null) {
            requestCallBack.onError(-9999, "网络错误！", throwable);
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
        return GET;
    }

    protected HashMap<String, String> headers(){
        return new HashMap<>();
    }

    protected abstract String api();

    protected abstract Class<OUT> outClass();

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
                @HeaderMap HashMap<String, String> headers,
                @Url String url,
                @Body RequestBody body
        );

        @GET
        Call<ResponseBody> getCall(
                @HeaderMap HashMap<String, String> headers,
                @Url String url
        );
    }

}
