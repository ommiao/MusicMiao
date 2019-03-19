package cn.ommiao.musicmiao.bean;

import java.util.ArrayList;

import cn.ommiao.bean.JavaBean;

public class Song extends JavaBean {

    private int id;
    private String mid;
    private String name, title, title_hilight;
    private Album album;
    private SongFile file;
    private ArrayList<Singer> singer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public SongFile getFile() {
        return file;
    }

    public void setFile(SongFile file) {
        this.file = file;
    }

    public ArrayList<Singer> getSinger() {
        return singer;
    }

    public void setSinger(ArrayList<Singer> singer) {
        this.singer = singer;
    }

    public String getOneSinger(){
        return getSinger().get(0).getTitle();
    }

    public String getAlbumImageUrl(){
        String builder = "https://y.gtimg.cn/music/photo_new/T002R300x300M000" +
                getAlbum().getMid() +
                ".jpg";
        return builder;
    }
}
