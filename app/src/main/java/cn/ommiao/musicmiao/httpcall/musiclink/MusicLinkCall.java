package cn.ommiao.musicmiao.httpcall.musiclink;

import cn.ommiao.musicmiao.httpcall.musiclink.models.MusicLinkIn;
import cn.ommiao.musicmiao.httpcall.musiclink.models.MusicLinkOut;
import cn.ommiao.network.BaseRequest;

public class MusicLinkCall extends BaseRequest<MusicLinkIn, MusicLinkOut> {

    @Override
    protected String api() {
        return "musicman/qq/index.php?a={a}&b={b}";
    }

    @Override
    protected Class<MusicLinkOut> outClass() {
        return MusicLinkOut.class;
    }

    @Override
    protected String baseUrl() {
        return "http://45.40.198.106:8693/";
    }

    @Override
    protected int method() {
        return POST;
    }

    @Override
    protected String extraHandle(String res) {
        return "{\"api\":\"" + res.trim() + "\", \"code\":0}";
    }
}
