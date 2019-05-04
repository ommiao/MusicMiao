package cn.ommiao.musicmiao.httpcall.moresound.songinfo.model;

import java.util.HashMap;

import cn.ommiao.network.RequestInBase;

public class SongInfoIn extends RequestInBase {

    private transient String mid;

    public SongInfoIn(String mid) {
        this.mid = mid;
    }

    @Override
    protected HashMap<String, String> body() {
        return new HashMap<String, String>(){
            {
                put("mid", mid + "/0");
            }
        };
    }
}
