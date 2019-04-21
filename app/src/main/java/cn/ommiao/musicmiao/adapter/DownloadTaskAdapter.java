package cn.ommiao.musicmiao.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.bean.SongTask;
import cn.ommiao.musicmiao.utils.UIUtil;

public class DownloadTaskAdapter extends BaseQuickAdapter<SongTask, BaseViewHolder> {

    public DownloadTaskAdapter(int layoutResId, @Nullable List<SongTask> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongTask item) {
        helper.setText(R.id.tv_display_name, item.getDisplayName());
        helper.setText(R.id.tv_size_downloading, item.getDisplayDownloadingSize());
        helper.setText(R.id.tv_size_total, item.getDisplayTotalSize());
        helper.setText(R.id.tv_download_status, item.getDisplayStatus());
        ImageView progressBg = helper.getView(R.id.iv_progress);
        progressBg.setTranslationX(-(1 - item.getDownloadingRatio()) * UIUtil.getScreenWidth());
        ImageView statusView = helper.getView(R.id.iv_download_status);
        int status = item.getDownloadStatus();
        if(status == SongTask.DOWNLOAD_STATUS_DOWNLOADING){
            statusView.setImageResource(R.drawable.icon_pause);
        } else if(status == SongTask.DOWNLOAD_STATUS_COMPLETE){
            statusView.setImageResource(R.drawable.icon_complete);
        } else if(status == SongTask.DOWNLOAD_STATUS_ERROR){
            statusView.setImageResource(R.drawable.icon_error);
        } else {
            statusView.setImageResource(R.drawable.icon_download);
        }
    }

    public synchronized void updateTask(DownloadTask task){
        int pos = getIndex(task.getKey());
        if(pos == -1 || pos >= mData.size()){
            return;
        }
        SongTask songTask = mData.get(pos);
        songTask.setDownloadStatus(convertDownloadStatus(task.getState()));
        songTask.setTotalSize(task.getEntity().getFileSize());
        songTask.setDownoadingSize(task.getEntity().getCurrentProgress());
        notifyItemChanged(pos);
    }

    private int convertDownloadStatus(int state){
        switch (state) {
            case IEntity.STATE_WAIT:
                return SongTask.DOWNLOAD_STATUS_WAIT;
            default:
            case IEntity.STATE_OTHER:
            case IEntity.STATE_FAIL:
                return SongTask.DOWNLOAD_STATUS_ERROR;
            case IEntity.STATE_STOP:
                return SongTask.DOWNLOAD_STATUS_PAUSE;
            case IEntity.STATE_PRE:
            case IEntity.STATE_POST_PRE:
            case IEntity.STATE_RUNNING:
                return SongTask.DOWNLOAD_STATUS_DOWNLOADING;
            case IEntity.STATE_COMPLETE:
                return SongTask.DOWNLOAD_STATUS_COMPLETE;
        }
    }

    private synchronized int getIndex(String url){
        for (int index = 0; index < mData.size(); index++){
            if(mData.get(index).getUrl().equals(url)){
                return index;
            }
        }
        return -1;
    }
}
