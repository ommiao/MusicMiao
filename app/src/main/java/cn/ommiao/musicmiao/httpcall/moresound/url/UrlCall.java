package cn.ommiao.musicmiao.httpcall.moresound.url;


import java.util.HashMap;

import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlIn;
import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlOut;
import cn.ommiao.network.BaseRequest;

public class UrlCall extends BaseRequest<UrlIn, UrlOut> {

    private String C0ntentLength;

    public UrlCall(String c0ntentLength) {
        C0ntentLength = c0ntentLength;
    }

    @Override
    protected String api() {
        return "music/{param}";
    }

    @Override
    protected Class<UrlOut> outClass() {
        return UrlOut.class;
    }

    @Override
    protected String baseUrl() {
        return "http://moresound.tk";
    }

    @Override
    protected HashMap<String, String> headers() {
        return new HashMap<String, String>(){
            {
                put("C0ntent-Length", C0ntentLength);
            }
        };
    }

}
