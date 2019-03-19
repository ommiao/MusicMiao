package cn.ommiao.musicmiao.bean;

import cn.ommiao.bean.JavaBean;

public class SongFile extends JavaBean {

    private String media_mid, strMediaMid;
    private int size_128, size_320, size_ape, size_flac;

    public String getMedia_mid() {
        return media_mid;
    }

    public void setMedia_mid(String media_mid) {
        this.media_mid = media_mid;
    }

    public String getStrMediaMid() {
        return strMediaMid;
    }

    public void setStrMediaMid(String strMediaMid) {
        this.strMediaMid = strMediaMid;
    }

    public int getSize_128() {
        return size_128;
    }

    public void setSize_128(int size_128) {
        this.size_128 = size_128;
    }

    public int getSize_320() {
        return size_320;
    }

    public void setSize_320(int size_320) {
        this.size_320 = size_320;
    }

    public int getSize_ape() {
        return size_ape;
    }

    public void setSize_ape(int size_ape) {
        this.size_ape = size_ape;
    }

    public int getSize_flac() {
        return size_flac;
    }

    public void setSize_flac(int size_flac) {
        this.size_flac = size_flac;
    }
}
