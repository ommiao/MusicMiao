package cn.ommiao.musicmiao.ui.music;

import android.os.Handler;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.LocalSongListAdapter;
import cn.ommiao.musicmiao.adapter.StaggeredDividerItemDecoration;
import cn.ommiao.musicmiao.bean.LocalSong;
import cn.ommiao.musicmiao.databinding.FragmentLocalMusicBinding;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.LongClickUtil;
import cn.ommiao.musicmiao.utils.MusicUtil;

public class LocalMusicFragment extends BaseFragment<FragmentLocalMusicBinding> {

    private static final long DELAY = 1500;

    private ArrayList<LocalSong> localSongs = new ArrayList<>();
    private LocalSongListAdapter adapter;

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
        adapter = new LocalSongListAdapter(R.layout.item_music_search, localSongs);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mActivity.getResources().getInteger(R.integer.search_result_span_count), StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvMusic.setLayoutManager(layoutManager);
        mBinding.rvMusic.setAdapter(adapter);
        mBinding.rvMusic.addItemDecoration(new StaggeredDividerItemDecoration(mActivity, mActivity.getResources().getDimensionPixelSize(R.dimen.music_list_item_space)));
    }

    @Override
    protected void initData() {
        ArrayList<LocalSong> songs = MusicUtil.getMusicData(mActivity);
        for(LocalSong song : songs){
            Logger.d(song.toHashMap());
        }
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
}
