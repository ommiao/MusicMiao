package cn.ommiao.musicmiao.bean;

import org.litepal.crud.LitePalSupport;

import java.util.HashMap;
import java.util.Locale;


public class SongTask extends LitePalSupport {

    private static final String SIZE_SUFFIX = "MB";

    public static final int DOWNLOAD_STATUS_WAIT = -1;
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 0;
    public static final int DOWNLOAD_STATUS_PAUSE = 1;
    public static final int DOWNLOAD_STATUS_ERROR = 2;
    public static final int DOWNLOAD_STATUS_COMPLETE = 200;

    private String displayName, url, localPath;
    private int downloadStatus;
    private long downoadingSize, totalSize;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getDownoadingSize() {
        return downoadingSize;
    }

    public void setDownoadingSize(long downoadingSize) {
        this.downoadingSize = downoadingSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getDisplayTotalSize(){
        if(totalSize == 0){
            return "文件大小获取中";
        }
        double size = (double)totalSize / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public String getDisplayDownloadingSize(){
        double size = (double)downoadingSize / 1024.0D / 1024.0D;
        return String.format(Locale.CHINA, "%.2f", size) + SIZE_SUFFIX;
    }

    public float getDownloadingRatio(){
        return downoadingSize / (float)totalSize;
    }

    public String getDisplayStatus(){
        return statusMap.get(downloadStatus);
    }

    private static final HashMap<Integer, String> statusMap = new HashMap<Integer, String>(){
        {
            put(SongTask.DOWNLOAD_STATUS_WAIT, "等待");
            put(SongTask.DOWNLOAD_STATUS_DOWNLOADING, "下载");
            put(SongTask.DOWNLOAD_STATUS_PAUSE, "暂停");
            put(SongTask.DOWNLOAD_STATUS_ERROR, "错误");
            put(SongTask.DOWNLOAD_STATUS_COMPLETE, "完成");
        }
    };
}
