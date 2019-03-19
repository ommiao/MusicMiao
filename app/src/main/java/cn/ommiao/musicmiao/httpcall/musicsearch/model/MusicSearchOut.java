package cn.ommiao.musicmiao.httpcall.musicsearch.model;

import java.util.ArrayList;

import cn.ommiao.musicmiao.bean.SearchResult;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.network.RequestOutBase;

public class MusicSearchOut extends RequestOutBase<SearchResult> {

    public ArrayList<Song> getSongs(){
        return getData().getSong().getList();
    }

}
