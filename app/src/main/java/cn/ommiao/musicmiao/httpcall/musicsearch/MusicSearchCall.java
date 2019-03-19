package cn.ommiao.musicmiao.httpcall.musicsearch;

import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchIn;
import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchOut;
import cn.ommiao.network.BaseRequest;

public class MusicSearchCall extends BaseRequest<MusicSearchIn, MusicSearchOut> {

    @Override
    protected String api() {
        return "soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p={page}&n={count}&w={keywords}&jsonpCallback=searchCallbacksong2020&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
    }

    @Override
    protected Class<MusicSearchOut> outClass() {
        return MusicSearchOut.class;
    }

}
