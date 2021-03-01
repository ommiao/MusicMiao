package cn.ommiao.musicmiao.ui.music;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.LocalSongListAdapter;
import cn.ommiao.musicmiao.adapter.StaggeredDividerItemDecoration;
import cn.ommiao.musicmiao.bean.LocalSong;
import cn.ommiao.musicmiao.databinding.FragmentLocalMusicBinding;
import cn.ommiao.musicmiao.interfaces.DownloadActionListener;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.LongClickUtil;
import cn.ommiao.musicmiao.utils.MusicUtil;
import cn.ommiao.musicmiao.utils.SpringUtil;

public class LocalMusicFragment extends BaseFragment<FragmentLocalMusicBinding> implements BaseQuickAdapter.OnItemClickListener {

    private static final long DELAY = 1600;

    private ArrayList<LocalSong> localSongs = new ArrayList<>();
    private LocalSongListAdapter adapter;

    private boolean isPlayShow = false;
    private int playingIndex = -1;

    private MediaPlayer mediaPlayer;
    private Runnable progressRunnable;
    private Handler handler = new Handler();
    private String currentPath;

    private TextView tvEmptyTips;

    @Override
    protected void initViews() {
        int sHeight = ImmersionBar.getStatusBarHeight(mActivity);
        ViewGroup.LayoutParams layoutParams = mBinding.vStatusBar.getLayoutParams();
        layoutParams.height = sHeight;
        mBinding.vStatusBar.setLayoutParams(layoutParams);
        LongClickUtil.setLongClick(new Handler(), mBinding.ivMusic, DELAY, v -> {
            startMusicSearch();
            return true;
        });
        View emptyView = LayoutInflater.from(mActivity).inflate(R.layout.layout_music_list_local_empty, null);
        tvEmptyTips = emptyView.findViewById(R.id.tv_empty_tips);
        adapter = new LocalSongListAdapter(R.layout.item_music_local, localSongs, this);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemClickListener(this);
        adapter.setEmptyView(emptyView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mActivity.getResources().getInteger(R.integer.search_result_span_count), StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvMusic.setLayoutManager(layoutManager);
        mBinding.rvMusic.setAdapter(adapter);
        mBinding.rvMusic.addItemDecoration(new StaggeredDividerItemDecoration(mActivity, mActivity.getResources().getDimensionPixelSize(R.dimen.music_list_item_space)));
        Objects.requireNonNull(mBinding.rvMusic.getItemAnimator()).setChangeDuration(0);
        mBinding.playPause.setTranslationY(getResources().getDimension(R.dimen.play_pause_animation_height));
        mBinding.playPause.pause();
        mBinding.playPause.setOnClickListener(v -> onPlayPauseClick());
    }

    @Override
    protected void initData() {
        tvEmptyTips.setText(R.string.music_local_loading);
        new Thread(() -> {
            ArrayList<LocalSong> songs = MusicUtil.getMusicData(mActivity);
            localSongs.addAll(songs);
            mActivity.runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                if(localSongs.size() == 0){
                    tvEmptyTips.setText(R.string.music_local_empty_tips);
                }
            });
        }).start();
        mediaPlayer = new MediaPlayer();
    }

    private void startMusicSearch() {
        SearchFragment fragment = new SearchFragment();
        fragment.setDownloadActionListener((DownloadActionListener) mActivity);
        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, fragment)
                .commit();
        mBinding.playPause.stop();
        removeProgressRunable();
        mediaPlayer.release();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_music;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(mBinding.playPause.isTransitionStatus()){
            return;
        }
        if(playingIndex == position){
            return;
        }
        String path = localSongs.get(position).getPath();
        if(!isPlayShow){
            isPlayShow = true;
            SpringUtil.translationYAnimation(mBinding.playPause, getResources().getDimension(R.dimen.play_pause_animation_height), 0f, () -> {
                playMusic(path);
                currentPath = path;
            });
        } else {
            playMusic(path);
            currentPath = path;
        }
        localSongs.get(position).setPlaying(true);
        adapter.notifyItemChanged(position);
        if(playingIndex != -1){
            localSongs.get(playingIndex).setPlaying(false);
            adapter.notifyItemChanged(playingIndex);
        }
        playingIndex = position;
    }

    private void onPlayPauseClick() {
        if (mBinding.playPause.isPlay()) {
            pauseMusic();
        } else {
            playMusic(currentPath);
        }
    }

    private void playMusic(String path){
        if(!mBinding.playPause.isPlay()){
            mBinding.playPause.play();
        }
        if(path.equals(currentPath)){
            mediaPlayer.start();
        } else {
            try {
                removeProgressRunable();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mBinding.playPause.rollbackProgress();
                    mp.start();
                    startProgressRunable();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startProgressRunable() {
        removeProgressRunable();
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    float progress = (float) mediaPlayer.getCurrentPosition() / (float)mediaPlayer.getDuration();
                    if(progress == 1f){
                        localSongs.get(playingIndex).setPlaying(true);
                        adapter.notifyItemChanged(playingIndex);
                        playingIndex = -1;
                    }
                    mBinding.playPause.setProgress(progress);
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(progressRunnable);
    }

    private void removeProgressRunable(){
        handler.removeCallbacks(progressRunnable);
    }

    private void pauseMusic(){
        mBinding.playPause.pause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeProgressRunable();
        mediaPlayer.release();
    }
}
