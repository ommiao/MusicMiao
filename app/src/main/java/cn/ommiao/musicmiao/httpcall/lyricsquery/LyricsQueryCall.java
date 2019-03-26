package cn.ommiao.musicmiao.httpcall.lyricsquery;

import java.util.HashMap;

import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryIn;
import cn.ommiao.musicmiao.httpcall.lyricsquery.model.LyricsQueryOut;
import cn.ommiao.network.BaseRequest;

public class LyricsQueryCall extends BaseRequest<LyricsQueryIn, LyricsQueryOut> {
    @Override
    protected String api() {
        return "lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1494070301711&songmid={songMid}&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8%C2%ACice=0&platform=yqq&needNewCode=0";
    }

    @Override
    protected Class<LyricsQueryOut> outClass() {
        return LyricsQueryOut.class;
    }

    @Override
    protected String extraHandle(String res) {
        return res.replace("MusicJsonCallback_lrc(", "").replace(")", "");
    }

    @Override
    protected HashMap<String, String> headers() {
        return new HashMap<String, String>(){
            {
                put("Referer", "https://y.qq.com/portal/player.html");
            }
        };
    }
}
