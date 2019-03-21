package cn.ommiao.musicmiao.ui.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.TransitionInflater;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.FragmentMusicDetailBinding;
import cn.ommiao.musicmiao.ui.base.BaseFragment;

public class MusicDetailFragment extends BaseFragment<FragmentMusicDetailBinding> implements View.OnClickListener{

    private String url, tran_name;

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).titleBar(mBinding.toolbar).init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        assert bundle != null;
        url = bundle.getString("url");
        tran_name = bundle.getString("tran_name");
        mBinding.playPause.pause();
        mBinding.playPause.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.ivAlbum.setTransitionName(tran_name);
        Picasso.with(getContext())
                .load(url)
                .noFade()
                .placeholder(R.drawable.ic_music_s)
                .error(R.drawable.ic_music_s)
                .into(mBinding.ivAlbum, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_pause:
                if(mBinding.playPause.isPlay()){
                    mBinding.playPause.pause();
                } else {
                    mBinding.playPause.play();
                }
                break;
        }
    }
}
