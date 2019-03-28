package cn.ommiao.musicmiao.bean;

import cn.ommiao.bean.JavaBean;

public class VkeyInfo extends JavaBean {

    private int subcode;
    private String vkey;

    public int getSubcode() {
        return subcode;
    }

    public void setSubcode(int subcode) {
        this.subcode = subcode;
    }

    public String getVkey() {
        return vkey;
    }

    public void setVkey(String vkey) {
        this.vkey = vkey;
    }
}
