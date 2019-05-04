package cn.ommiao.musicmiao.httpcall.moresound.url;


import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlIn;
import cn.ommiao.musicmiao.httpcall.moresound.url.model.UrlOut;
import cn.ommiao.network.BaseRequest;

public class UrlCall extends BaseRequest<UrlIn, UrlOut> {

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

}
