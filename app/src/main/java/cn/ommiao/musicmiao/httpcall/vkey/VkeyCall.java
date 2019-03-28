package cn.ommiao.musicmiao.httpcall.vkey;

import java.util.HashMap;

import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyIn;
import cn.ommiao.musicmiao.httpcall.vkey.model.VkeyOut;
import cn.ommiao.network.BaseRequest;

public class VkeyCall extends BaseRequest<VkeyIn, VkeyOut> {
    @Override
    protected String api() {
        return "base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=556936094&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=0&songmid=003a1tne1nSz1Y&filename=C400003a1tne1nSz1Y.m4a&guid=6422449780";
    }

    @Override
    protected Class<VkeyOut> outClass() {
        return VkeyOut.class;
    }

    @Override
    protected HashMap<String, String> headers() {
        return new HashMap<String, String>(){
            {
                put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; MI 4LTE MIUI/8.4.26)");
            }
        };
    }
}
