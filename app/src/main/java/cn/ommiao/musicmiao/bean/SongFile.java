package cn.ommiao.musicmiao.bean;

import java.io.Serializable;
import java.util.Locale;

import cn.ommiao.bean.JavaBean;

public class SongFile extends JavaBean implements Serializable {

    private static final String SIZE_SUFFIX = "MB";

    private String media_mid, strMediaMid;
    private int size_128, size_320, size_ape, size_flac;

    private boolean hasLocalNqMp3 = false, hasLocalHqMp3 = false, hasLocalFlac = false, hasLocalApe = false;

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

    public boolean hasNqMp3(){
        return size_128 > 0;
    }

    public String getNqMp3Size(){
        double size = (double)size_128 / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public boolean hasHqMp3(){
        return size_320 > 0;
    }

    public String getHqMp3Size(){
        double size = (double)size_320 / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public boolean hasFlac(){
        return size_flac > 0;
    }

    public String getFlacSize(){
        double size = (double)size_flac / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public boolean hasApe(){
        return size_ape > 0;
    }

    public String getApeSize(){
        double size = (double)size_ape / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public boolean isHasLocalNqMp3() {
        return hasLocalNqMp3;
    }

    public void setHasLocalNqMp3(boolean hasLocalNqMp3) {
        this.hasLocalNqMp3 = hasLocalNqMp3;
    }

    public boolean isHasLocalHqMp3() {
        return hasLocalHqMp3;
    }

    public void setHasLocalHqMp3(boolean hasLocalHqMp3) {
        this.hasLocalHqMp3 = hasLocalHqMp3;
    }

    public boolean isHasLocalFlac() {
        return hasLocalFlac;
    }

    public void setHasLocalFlac(boolean hasLocalFlac) {
        this.hasLocalFlac = hasLocalFlac;
    }

    public boolean isHasLocalApe() {
        return hasLocalApe;
    }

    public void setHasLocalApe(boolean hasLocalApe) {
        this.hasLocalApe = hasLocalApe;
    }
}
