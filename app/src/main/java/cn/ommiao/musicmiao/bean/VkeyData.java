package cn.ommiao.musicmiao.bean;

import java.util.ArrayList;

import cn.ommiao.bean.JavaBean;

public class VkeyData extends JavaBean {

    private int expiration;
    private ArrayList<VkeyInfo> items;

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public ArrayList<VkeyInfo> getItems() {
        return items;
    }

    public void setItems(ArrayList<VkeyInfo> items) {
        this.items = items;
    }
}
