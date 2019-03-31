package cn.ommiao.musicmiao.httpcall.lyricsquery.model;

import android.util.Base64;

import java.util.HashMap;

import cn.ommiao.network.RequestOutBase;

public class LyricsQueryOut extends RequestOutBase {

    private static final HashMap<String, String> replaceMap = new HashMap<String, String>(){
        {
            put("&apos;", "'");
            put("&amp;", "&");
        }
    };

    private String lyric;

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getDecodeLyrics(){
        String lyrics = new String(Base64.decode(lyric.getBytes(), Base64.DEFAULT));
        for (String key : replaceMap.keySet()){
            lyrics = lyrics.replace(key, replaceMap.get(key));
        }
        return lyrics;
    }
}
