package cn.ommiao.musicmiao.bean;

import java.util.ArrayList;

import cn.ommiao.bean.JavaBean;

public class SongSet extends JavaBean {

    private int curnum, curpage, totalnum;
    private ArrayList<Song> list;

    public int getCurnum() {
        return curnum;
    }

    public void setCurnum(int curnum) {
        this.curnum = curnum;
    }

    public int getCurpage() {
        return curpage;
    }

    public void setCurpage(int curpage) {
        this.curpage = curpage;
    }

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public ArrayList<Song> getList() {
        return list;
    }

    public void setList(ArrayList<Song> list) {
        this.list = list;
    }
}
