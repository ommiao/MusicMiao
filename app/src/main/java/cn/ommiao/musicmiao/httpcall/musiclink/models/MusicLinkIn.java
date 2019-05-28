package cn.ommiao.musicmiao.httpcall.musiclink.models;

import java.util.HashMap;

import cn.ommiao.musicmiao.utils.OtherUtil;
import cn.ommiao.network.RequestInBase;

public class MusicLinkIn extends RequestInBase {

    private String a, b, mid, media_mid;

    public MusicLinkIn(String b, String mid, String media_mid) {
        this.a = mid;
        this.b = b;
        this.mid = mid;
        this.media_mid = media_mid;
    }

    @Override
    protected HashMap<String, String> body() {
        HashMap<String, String> body = new HashMap<>();
        body.put("a", mid.substring(0, mid.length() - 1));
        body.put("b", b.substring(10));
        body.put("d", "20");
        body.put("e", String.valueOf((int)(System.currentTimeMillis() / 1000L)));
        body.put("c", OtherUtil.getSecret(
                body.get("a") + body.get("d") + body.get("e")
        ));
        return body;
    }
}
