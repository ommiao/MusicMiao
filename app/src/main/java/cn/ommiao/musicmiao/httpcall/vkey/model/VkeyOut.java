package cn.ommiao.musicmiao.httpcall.vkey.model;

import cn.ommiao.musicmiao.bean.VkeyData;
import cn.ommiao.network.RequestOutBase;

public class VkeyOut extends RequestOutBase<VkeyData> {

    private int cid;
    private String userip;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getUserip() {
        return userip;
    }

    public void setUserip(String userip) {
        this.userip = userip;
    }

    public String getVkey(){
        return isDataValid() ? getData().getItems().get(0).getVkey() : "BadVkey";
    }
}
