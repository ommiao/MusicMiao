package cn.ommiao.musicmiao.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewUtil {

    private static WebViewUtil instance;

    private Context context;
    private WebView webView;

    public static void init(Context context){
        instance = new WebViewUtil(context);
    }

    public static WebViewUtil getInstance(){
        return instance;
    }

    private WebViewUtil(Context context){
        this.context = context;
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(){
        webView = new WebView(context);
        WebSettings settings = webView.getSettings();
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);
        // 设置支持缩放
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://moresound.tk/music/#");
    }

    public void getWebViewParam(String paramName, final WebViewUtilCallback callback){
        webView.evaluateJavascript("javascript:" + paramName, value -> {
            if(callback != null){
                callback.onParamValueReceived(value);
            }
        });
    }

    public interface WebViewUtilCallback{
        void onParamValueReceived(String value);
    }

}
