package cn.ommiao.musicmiao.httpcall.moresound.songinfo.model;

import cn.ommiao.musicmiao.bean.moresound.Api;
import cn.ommiao.network.RequestOutBase;

public class SongInfoOut extends RequestOutBase {

    private transient int code = 0;

    private Api url;

    public Api getApi() {
        return url == null ? new Api() : url;
    }
}
