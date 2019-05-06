package cn.ommiao.musicmiao.httpcall.moresound.songinfo;

import cn.ommiao.musicmiao.httpcall.moresound.songinfo.model.SongInfoIn;
import cn.ommiao.musicmiao.httpcall.moresound.songinfo.model.SongInfoOut;
import cn.ommiao.network.BaseRequest;

public class SongInfoCall extends BaseRequest<SongInfoIn, SongInfoOut> {

    @Override
    protected String api() {
        return "music/api.php?get_song=qq";
    }

    @Override
    protected Class<SongInfoOut> outClass() {
        return SongInfoOut.class;
    }

    @Override
    protected String baseUrl() {
        return "http://moresound.tk";
    }

    @Override
    protected int method() {
        return POST;
    }

    @Override
    protected String extraHandle(String res) {
        res = res.replace("128MP3", "mp3Nq");
        res = res.replace("320MP3", "mp3Hq");
        res = res.replace("FLAC", "flac");
        res = res.replace("APE", "ape");
        res = res.replace("msg", "message");
        return res;
    }
}
