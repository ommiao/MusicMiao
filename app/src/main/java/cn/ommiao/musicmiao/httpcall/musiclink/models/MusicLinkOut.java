package cn.ommiao.musicmiao.httpcall.musiclink.models;

import cn.ommiao.network.RequestOutBase;

public class MusicLinkOut extends RequestOutBase {

    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getLink(){
        return "http://dl.stream.qqmusic.qq.com/" + api;
    }
}
