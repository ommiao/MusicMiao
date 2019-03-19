package cn.ommiao.musicmiao.bean;

import cn.ommiao.bean.JavaBean;

public class SearchResult extends JavaBean {

    private SongSet song;

    public SongSet getSong() {
        return song;
    }

    public void setSong(SongSet song) {
        this.song = song;
    }
}
