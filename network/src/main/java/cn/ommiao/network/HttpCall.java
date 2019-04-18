package cn.ommiao.network;

public interface HttpCall {

    <OUT extends RequestOutBase> RequestCallBack<OUT> arrangeCallback(final String url, final RequestInBase in, final RequestCallBack<OUT> callBack);

    <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, IN in, RequestCallBack<OUT> callBack);

    <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, IN in, RequestCallBack<OUT> callBack);

    <IN extends RequestInBase, OUT extends RequestOutBase> void newCall(BaseRequest<IN, OUT> request, boolean showLoading, String msg, IN in, RequestCallBack<OUT> callBack);

}
