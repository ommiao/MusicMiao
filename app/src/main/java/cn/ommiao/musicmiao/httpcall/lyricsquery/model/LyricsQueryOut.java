package cn.ommiao.musicmiao.httpcall.lyricsquery.model;

import android.util.Base64;

import cn.ommiao.network.RequestOutBase;

public class LyricsQueryOut extends RequestOutBase {

    private String lyric;

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getDecodeLyrics(){
        return new String(Base64.decode(lyric.getBytes(), Base64.DEFAULT));
    }
}
