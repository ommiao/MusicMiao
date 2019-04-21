package cn.ommiao.musicmiao.interfaces;

import cn.ommiao.musicmiao.bean.SongTask;

public interface DownloadActionListener{
    void onShowTasks();
    void onAddDownloadTask(SongTask task);
    void onRemoveDonwnloadTask(String taskId);
    boolean isTaskExists(String url);
}
