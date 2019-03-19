package cn.ommiao.musicmiao.httpcall.musicsearch.model;

import cn.ommiao.network.RequestInBase;

public class MusicSearchIn extends RequestInBase {

    private String keywords;
    private int page, count;

    public MusicSearchIn(String keywords, int page) {
        this.keywords = keywords;
        this.page = page;
        this.count = 20;
    }

    public MusicSearchIn(String keywords, int page, int count) {
        this.keywords = keywords;
        this.page = page;
        this.count = count;
    }

}
