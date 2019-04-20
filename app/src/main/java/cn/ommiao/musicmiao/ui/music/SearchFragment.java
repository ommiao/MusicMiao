package cn.ommiao.musicmiao.ui.music;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.hanks.htextview.base.AnimationListener;
import com.hanks.htextview.base.HTextView;

import java.util.ArrayList;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.adapter.SongListAdapter;
import cn.ommiao.musicmiao.adapter.StaggeredDividerItemDecoration;
import cn.ommiao.musicmiao.bean.Song;
import cn.ommiao.musicmiao.databinding.FragmentSearchBinding;
import cn.ommiao.musicmiao.databinding.LayoutMusicListEmptyBinding;
import cn.ommiao.musicmiao.httpcall.musicsearch.MusicSearchCall;
import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchIn;
import cn.ommiao.musicmiao.httpcall.musicsearch.model.MusicSearchOut;
import cn.ommiao.musicmiao.ui.base.BaseFragment;
import cn.ommiao.musicmiao.utils.StringUtil;
import cn.ommiao.musicmiao.utils.ToastUtil;
import cn.ommiao.musicmiao.widget.SquareImageView;
import cn.ommiao.network.SimpleRequestCallback;

public class SearchFragment extends BaseFragment<FragmentSearchBinding> implements
        View.OnClickListener,
        AnimationListener,
        TextView.OnEditorActionListener,
        ViewTreeObserver.OnPreDrawListener {

    private static final long REVEAL_DURATION = 900;

    private boolean isSearchEditViewShow = false;

    private String keywords;

    private ArrayList<Song> songs = new ArrayList<>();
    private SongListAdapter adapter;
    private int page = 1;

    private LayoutMusicListEmptyBinding emptyBinding;

    @Override
    protected void initViews() {
        int sHeight = ImmersionBar.getStatusBarHeight(mActivity);
        ViewGroup.LayoutParams layoutParams = mBinding.vStatusBar.getLayoutParams();
        layoutParams.height = sHeight;
        mBinding.vStatusBar.setLayoutParams(layoutParams);
        mBinding.ivMusic.setOnClickListener(this);
        mBinding.ivSearch.setOnClickListener(this);
        mBinding.etSearch.setOnEditorActionListener(this);
        mBinding.stvTitle.setAnimationListener(this);
        adapter = new SongListAdapter(R.layout.item_music_search, songs);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        initAdapterLoadMore();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(mActivity.getResources().getInteger(R.integer.search_result_span_count), StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvMusic.setLayoutManager(layoutManager);
        mBinding.rvMusic.setAdapter(adapter);
        mBinding.rvMusic.addItemDecoration(new StaggeredDividerItemDecoration(mActivity, mActivity.getResources().getDimensionPixelSize(R.dimen.music_list_item_space)));
        View emptyView = LayoutInflater.from(mActivity).inflate(R.layout.layout_music_list_empty, null);
        emptyBinding = DataBindingUtil.bind(emptyView);
        assert emptyBinding != null;
        emptyBinding.tvTips.setOnClickListener(this);
        adapter.setEmptyView(emptyView);
        adapter.setOnItemClickListener(this::onItemClick);
        mBinding.rvMusic.getViewTreeObserver().addOnPreDrawListener(this);
    }

    private void initAdapterLoadMore() {
        adapter.setOnLoadMoreListener(() -> search(false), mBinding.rvMusic);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music:
                onCatClick();
                break;
            case R.id.iv_search:
                onSearchClick();
                break;
            case R.id.tv_tips:
                onTipsClick();
                break;
        }
    }

    private void onTipsClick() {
        if(isSearchEditViewShow){
            keywords = mBinding.etSearch.getText().toString();
            if (isDataChecked()) {
                search(true);
            }
        } else {
            onSearchClick();
        }
    }

    private void onSearching(){
        emptyBinding.tvTips.setText(getString(R.string.music_searching));
        emptyBinding.progressBar.setVisibility(View.VISIBLE);
        emptyBinding.ivSearchTips.setVisibility(View.INVISIBLE);
    }

    private void onSearchFail(){
        emptyBinding.ivSearchTips.setImageResource(R.drawable.ic_search_fail);
        emptyBinding.tvTips.setText(getString(R.string.music_search_fail));
        emptyBinding.progressBar.setVisibility(View.INVISIBLE);
        emptyBinding.ivSearchTips.setVisibility(View.VISIBLE);
    }

    private void onCatClick() {
        if (isSearchEditViewShow && StringUtil.isEmpty(mBinding.etSearch.getText().toString())) {
            closeKeyboard();
            mBinding.etSearch.setText("");
            mBinding.stvTitle.setVisibility(View.VISIBLE);
            mBinding.etSearch.setVisibility(View.GONE);
            toggleSearchEditView();
        }
    }

    public void onSearchClick() {
        if (!isSearchEditViewShow) {
            toggleSearchEditView();
        }
    }

    private void toggleSearchEditView() {
        isSearchEditViewShow = !isSearchEditViewShow;
        final int[] stateSet = {android.R.attr.state_checked * (isSearchEditViewShow ? 1 : -1)};
        mBinding.ivSearch.setImageState(stateSet, true);
        mBinding.stvTitle.animateText(isSearchEditViewShow ? getString(R.string.search_hint) : getString(R.string.search_title));
    }

    @Override
    public void onAnimationEnd(HTextView hTextView) {
        if (isSearchEditViewShow) {
            mBinding.stvTitle.setVisibility(View.INVISIBLE);
            mBinding.etSearch.setVisibility(View.VISIBLE);
            mBinding.etSearch.setFocusable(true);
            mBinding.etSearch.setFocusableInTouchMode(true);
            mBinding.etSearch.requestFocus();
            openKeyboard();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            keywords = mBinding.etSearch.getText().toString();
            if (isDataChecked()) {
                search(true);
            }
        }
        return true;
    }

    private void search(final boolean clear) {
        closeKeyboard();
        if(clear){
            songs.clear();
            onSearching();
        }
        new Thread(() -> {
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mActivity.runOnUiThread(() -> {
                page = clear ? 1 : ++page;
                MusicSearchIn in = new MusicSearchIn(keywords, page);
                newCall(new MusicSearchCall(), in, new SimpleRequestCallback<MusicSearchOut>() {
                    @Override
                    public void onSuccess(MusicSearchOut out) {
                        songs.addAll(out.getSongs());
                        if (clear){
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.notifyItemInserted(songs.size());
                        }
                        int total = out.getData().getSong().getTotalnum();
                        if (songs.size() >= total) {
                            adapter.loadMoreEnd(true);
                        } else {
                            adapter.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onError(int code, String error) {
                        if (!clear) {
                            adapter.loadMoreFail();
                        } else {
                            onSearchFail();
                        }
                    }
                });
            });
        }).start();
    }

    private boolean isDataChecked() {
        if (StringUtil.isEmpty(keywords)) {
            ToastUtil.show(R.string.search_input_keywords);
            return false;
        }
        return true;
    }

    private void openKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(mBinding.etSearch, 0);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
    }

    private void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SquareImageView albumView = view.findViewById(R.id.siv_music_album);
        Song song = songs.get(position);
        Bundle bundle = new Bundle();
        MusicDetailFragment detailFragment = new MusicDetailFragment();
        mActivity.setOnBackPressedListener(detailFragment);
        bundle.putSerializable("song", song);
        bundle.putString("tran_name", song.getMid());
        detailFragment.setArguments(bundle);
        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .addSharedElement(albumView, song.getMid())
                .addToBackStack("detail")
                .replace(R.id.fl_container, detailFragment)
                .commit();
    }

    @Override
    public boolean onPreDraw() {
        mBinding.rvMusic.getViewTreeObserver().removeOnPreDrawListener(this);
        startReveal(mBinding.ivMusic, mBinding.getRoot());
        return true;
    }

    private void startReveal(View triggerView, View animView){
        int[] tvLocation = new int[2];
        triggerView.getLocationInWindow(tvLocation);
        int tvX = tvLocation[0] + triggerView.getWidth() / 2;
        int tvY = tvLocation[1] + triggerView.getHeight() / 2;
        int[] avLocation = new int[2];
        animView.getLocationInWindow(avLocation);
        int avX = avLocation[0] + animView.getWidth() / 2;
        int avY = avLocation[1] + animView.getHeight() / 2;
        int rippleW = tvX < avX ? animView.getWidth() - tvX : tvX - avLocation[0];
        int rippleY = tvY < avY ? animView.getHeight() - tvY : tvY - avLocation[1];
        float startRadius = 0f;
        float endRadius = (float) Math.sqrt(rippleW * rippleW + rippleY * rippleY);
        Animator animator = ViewAnimationUtils.createCircularReveal(animView, tvX, tvY, startRadius, endRadius);
        animator.setDuration(REVEAL_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}
