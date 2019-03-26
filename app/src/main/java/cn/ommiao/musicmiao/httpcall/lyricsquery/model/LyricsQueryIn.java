package cn.ommiao.musicmiao.httpcall.lyricsquery.model;

import cn.ommiao.network.RequestInBase;

public class LyricsQueryIn extends RequestInBase {

    private String songMid;

    public LyricsQueryIn(String songMid) {
        this.songMid = songMid;
    }
}
