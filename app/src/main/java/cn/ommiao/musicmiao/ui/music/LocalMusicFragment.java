package cn.ommiao.musicmiao.ui.music;

import android.os.Handler;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Objects;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.LocalSongListAdapter;
import cn.ommiao.musicmiao.adapter.StaggeredDividerItemDecoration;
import cn.ommiao.musicmiao.bean.LocalSong;
import cn.ommiao.musicmiao.databinding.FragmentLocalMusicBinding;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.LongClickUtil;
import cn.ommiao.musicmiao.utils.MusicUtil;
import cn.ommiao.musicmiao.utils.SpringUtil;

public class LocalMusicFragment extends BaseFragment<FragmentLocalMusicBinding> implements BaseQuickAdapter.OnItemClickListener {

    private static final long DELAY = 1500;

    private ArrayList<LocalSong> localSongs = new ArrayList<>();
    private LocalSongListAdapter adapter;

    private boolean isPlayShow = false;
    private int playingIndex = -1;

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
        adapter = new LocalSongListAdapter(R.layout.item_music_local, localSongs, this);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemClickListener(this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mActivity.getResources().getInteger(R.integer.search_result_span_count), StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvMusic.setLayoutManager(layoutManager);
        mBinding.rvMusic.setAdapter(adapter);
        mBinding.rvMusic.addItemDecoration(new StaggeredDividerItemDecoration(mActivity, mActivity.getResources().getDimensionPixelSize(R.dimen.music_list_item_space)));
        Objects.requireNonNull(mBinding.rvMusic.getItemAnimator()).setChangeDuration(0);
        mBinding.playPause.setTranslationY(getResources().getDimension(R.dimen.play_pause_animation_height));
    }

    @Override
    protected void initData() {
        ArrayList<LocalSong> songs = MusicUtil.getMusicData(mActivity);
        localSongs.addAll(songs);
        adapter.notifyDataSetChanged();
    }

    private void startMusicSearch() {
        SearchFragment fragment = new SearchFragment();
        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, fragment)
                .commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_music;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(playingIndex == position){
            return;
        }
        if(!isPlayShow){
            isPlayShow = true;
            SpringUtil.translationYAnimation(mBinding.playPause, getResources().getDimension(R.dimen.play_pause_animation_height), 0f);
        }
        localSongs.get(position).setPlaying(true);
        adapter.notifyItemChanged(position);
        if(playingIndex != -1){
            localSongs.get(playingIndex).setPlaying(false);
            adapter.notifyItemChanged(playingIndex);
        }
        playingIndex = position;
    }
}
