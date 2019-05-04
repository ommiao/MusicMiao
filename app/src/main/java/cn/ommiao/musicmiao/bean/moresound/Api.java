package cn.ommiao.musicmiao.bean.moresound;

import cn.ommiao.bean.JavaBean;

public class Api extends JavaBean {

    private String mp3Nq, mp3Hq, flac, ape;

    public String getMp3Nq() {
        return mp3Nq;
    }

    public void setMp3Nq(String mp3Nq) {
        this.mp3Nq = mp3Nq;
    }

    public String getMp3Hq() {
        return mp3Hq;
    }

    public void setMp3Hq(String mp3Hq) {
        this.mp3Hq = mp3Hq;
    }

    public String getFlac() {
        return flac;
    }

    public void setFlac(String flac) {
        this.flac = flac;
    }

    public String getApe() {
        return ape;
    }

    public void setApe(String ape) {
        this.ape = ape;
    }
}
