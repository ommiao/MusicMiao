package cn.ommiao.musicmiao.bean;

import java.io.Serializable;

import cn.ommiao.bean.JavaBean;

public class Singer extends JavaBean implements Serializable {

    private int id, type;
    private String mid, name, title, title_hilight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_hilight() {
        return title_hilight;
    }

    public void setTitle_hilight(String title_hilight) {
        this.title_hilight = title_hilight;
    }
}
