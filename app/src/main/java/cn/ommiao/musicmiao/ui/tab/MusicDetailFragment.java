package cn.ommiao.musicmiao.ui.tab;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.TransitionInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.databinding.FragmentMusicDetailBinding;
import cn.ommiao.musicmiao.ui.base.BaseFragment;

public class MusicDetailFragment extends BaseFragment<FragmentMusicDetailBinding> {

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
        mBinding.ivAlbum.setTransitionName(tran_name);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_music_detail;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(mBinding.ivAlbum);
    }
}
