package cn.ommiao.musicmiao.ui.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.DownloadTaskAdapter;
import cn.ommiao.musicmiao.bean.SongTask;
import cn.ommiao.musicmiao.databinding.ActivityMainBinding;
import cn.ommiao.musicmiao.interfaces.DownloadActionListener;
import cn.ommiao.musicmiao.ui.base.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements DownloadActionListener {

    private SweetSheet mSweetSheet;

    private ArrayList<SongTask> tasks = new ArrayList<>();
    private DownloadTaskAdapter taskAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        Aria.download(this).register();
        initSweetSheet();
        LocalMusicFragment fragment = new LocalMusicFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, fragment)
                .commit();
    }

    private void initSweetSheet() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.layout_music_list_download_empty, null);
        taskAdapter = new DownloadTaskAdapter(R.layout.item_download_task, tasks);
        taskAdapter.setOnItemClickListener(this::onItemClick);
        taskAdapter.setEmptyView(emptyView);
        mSweetSheet = new SweetSheet(mBinding.flContainer);
        RecyclerViewDelegate<SongTask, DownloadTaskAdapter> delegate = new RecyclerViewDelegate<>(taskAdapter, true, false);
        mSweetSheet.setDelegate(delegate);
        mSweetSheet.setBackgroundEffect(new DimEffect(1));
    }



    @Override
    protected void initData() {

    }

    @Override
    public void onShowTasks() {
        mSweetSheet.show();
    }

    @Override
    public void onAddDownloadTask(SongTask task) {
        tasks.add(task);
        taskAdapter.notifyItemInserted(tasks.size() - 1);
    }

    @Override
    public void onRemoveDonwnloadTask(String taskId) {

    }

    @Override
    public boolean isTaskExists(String displayName) {
        for (SongTask task : tasks){
            if(task.getDisplayName().equals(displayName)){
                return true;
            }
        }
        return false;
    }

    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SongTask songTask = tasks.get(position);
        int status = songTask.getDownloadStatus();
        if(status == SongTask.DOWNLOAD_STATUS_WAIT || status == SongTask.DOWNLOAD_STATUS_COMPLETE){
            return;
        }
        if(status == SongTask.DOWNLOAD_STATUS_DOWNLOADING){
            Aria.download(this).load(songTask.getUrl()).stop();
        } else if(status == SongTask.DOWNLOAD_STATUS_ERROR){
            songTask.setDownloadStatus(SongTask.DOWNLOAD_STATUS_WAIT);
            Aria.download(this).load(songTask.getUrl()).start();
        } else {
            Aria.download(this).load(songTask.getUrl()).start();
        }
        taskAdapter.notifyItemChanged(position);
    }

    @Download.onWait void taskWait(DownloadTask task) {
        Logger.d("taskWait:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskStart void taskStart(DownloadTask task) {
        Logger.d("taskStart:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskRunning void taskRunning(DownloadTask task){
        Logger.d("taskRunning:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskResume void taskResume(DownloadTask task) {
        Logger.d("taskResume:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskStop void taskStop(DownloadTask task) {
        Logger.d("taskStop:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskCancel void taskCancel(DownloadTask task) {
        Logger.d("taskCancel:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskFail void taskFail(DownloadTask task) {
        Logger.d("DownloadTask:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        Logger.d("taskComplete:" + task.getEntity().toString());
        taskAdapter.updateTask(task);
    }

    @Override
    public void onBackPressed() {
        if(onBackPressedListener != null){
            if(onBackPressedListener.interceptBackAction()){
                onBackPressedListener.onBackPressed();
            } else {
                checkHasDownloadingTask();
            }
        } else {
            checkHasDownloadingTask();
        }
    }

    private void checkHasDownloadingTask() {
        if(hasDownloadingTask()){
            showFinishConfirmDialog();
        } else {
            finish();
        }
    }

    private void showFinishConfirmDialog() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content", getString(R.string.music_downloading_list_no_empty));
        customDialogFragment.setArguments(bundle);
        customDialogFragment.setOnClickActionListener(new CustomDialogFragment.OnClickActionListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        customDialogFragment.show(getSupportFragmentManager(), CustomDialogFragment.class.getSimpleName());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void clearTasksNoCompeleted() {
        Aria.download(this).stopAllTask();
        for(SongTask task : tasks){
            if(task.getDownloadStatus() != SongTask.DOWNLOAD_STATUS_COMPLETE){
                try {
                    new File(task.getLocalPath()).delete();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean hasDownloadingTask(){
        for(SongTask task : tasks){
            if(task.getDownloadStatus() != SongTask.DOWNLOAD_STATUS_COMPLETE &&
                task.getDownloadStatus() != SongTask.DOWNLOAD_STATUS_ERROR){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearTasksNoCompeleted();
    }
}
