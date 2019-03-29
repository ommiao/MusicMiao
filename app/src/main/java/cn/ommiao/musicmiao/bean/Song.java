package cn.ommiao.musicmiao.bean;

import java.io.Serializable;
import java.util.ArrayList;

import cn.ommiao.bean.JavaBean;
import cn.ommiao.musicmiao.utils.StringUtil;

public class Song extends JavaBean implements Serializable {

    private int id;
    private String mid;
    private String name, title, title_hilight;
    private Album album;
    private SongFile file;
    private ArrayList<Singer> singer;

    private String mp3NqLink, mp3HqLink, flacLink, apeLink;

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
        return  !StringUtil.isEmpty(getAlbum().getTitle()) ?
                "https://y.gtimg.cn/music/photo_new/T002R300x300M000" + getAlbum().getMid() + ".jpg" :
                "http://134.175.41.67/static/ic_music_s.jpg";
    }

    public String getMp3NqLink() {
        return mp3NqLink;
    }

    public void setMp3NqLink(String mp3NqLink) {
        this.mp3NqLink = mp3NqLink;
    }

    public String getMp3HqLink() {
        return mp3HqLink;
    }

    public void setMp3HqLink(String mp3HqLink) {
        this.mp3HqLink = mp3HqLink;
    }

    public String getFlacLink() {
        return flacLink;
    }

    public void setFlacLink(String flacLink) {
        this.flacLink = flacLink;
    }

    public String getApeLink() {
        return apeLink;
    }

    public void setApeLink(String apeLink) {
        this.apeLink = apeLink;
    }
}
