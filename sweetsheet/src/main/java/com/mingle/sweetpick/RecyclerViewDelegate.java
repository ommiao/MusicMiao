package com.mingle.sweetpick;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mingle.adapter.MenuRVAdapter;
import com.mingle.entity.MenuEntity;

import com.mingle.sweetsheet.R;
import com.mingle.widget.CRImageView;
import com.mingle.widget.FreeGrowUpParentRelativeLayout;
import com.mingle.widget.SweetView;

import java.util.List;
import java.util.Objects;

/**
 * @author zzz40500
 * @version 1.0
 * @date 2015/8/5.
 * @github: https://github.com/zzz40500
 */
public class RecyclerViewDelegate<D, A extends BaseQuickAdapter<D, BaseViewHolder>> extends Delegate  {

    private SweetView mSweetView;
    private RecyclerView mRV;
    private A mAdapter;
    private CRImageView sliderIm;
    private FreeGrowUpParentRelativeLayout mFreeGrowUpParentRelativeLayout;
    private boolean mIsDragEnable, autoClose;
    private int mContentViewHeight;

    public RecyclerViewDelegate(A adapter, boolean dragEnable, boolean autoClose) {
        mAdapter = adapter;
        mIsDragEnable=dragEnable;
        this.autoClose = autoClose;
    }

    public RecyclerViewDelegate(A adapter, boolean dragEnable, boolean autoClose, int contentViewHeight) {
        mAdapter = adapter;
        mContentViewHeight = contentViewHeight;
        mIsDragEnable = dragEnable;
        this.autoClose = autoClose;
    }

    @Override
    protected View createView() {

        View rootView = LayoutInflater.from(mParentVG.getContext())
                .inflate(R.layout.layout_rv_sweet, null, false);

        mSweetView = rootView.findViewById(R.id.sv);
        mFreeGrowUpParentRelativeLayout = rootView.findViewById(R.id.freeGrowUpParentF);
        mRV = rootView.findViewById(R.id.rv);
        Objects.requireNonNull(mRV.getItemAnimator()).setChangeDuration(0);
        sliderIm = rootView.findViewById(R.id.sliderIM);
        mRV.setLayoutManager(new LinearLayoutManager(mParentVG.getContext(), LinearLayoutManager.VERTICAL, false));
        mSweetView.setAnimationListener(new AnimationImp());
        if(mContentViewHeight > 0){
            mFreeGrowUpParentRelativeLayout.setContentHeight(mContentViewHeight);
        }

        return rootView;
    }

    public RecyclerViewDelegate setContentHeight(int height){

        if(height >0 && mFreeGrowUpParentRelativeLayout != null){
            mFreeGrowUpParentRelativeLayout.setContentHeight(height);
        }else{
            mContentViewHeight=height;
        }

        return this;

    }

    @Override
    protected void setMenuList(List<MenuEntity> list) {
        mRV.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mOnMenuItemClickListener != null) {
                    if (mOnMenuItemClickListener.onItemClick(position)) {
                        if(autoClose){
                            delayedDismiss();
                        }
                    }
                }
            }
        });
        mRV.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRV.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                mFreeGrowUpParentRelativeLayout.setClipChildren(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRV.setOnTouchListener(null);

                mFreeGrowUpParentRelativeLayout.setClipChildren(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    protected void show() {
        super.show();
        ViewGroup.LayoutParams lp =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if(mRootView.getParent()!= null){
            mParentVG.removeView(mRootView);
        }

        mParentVG.addView(mRootView, lp);
        mSweetView.show();
    }

    @Override
    protected void dismiss() {
        super.dismiss();

    }


    class AnimationImp implements SweetView.AnimationListener {

        @Override
        public void onStart() {
            mFreeGrowUpParentRelativeLayout.reset();
            mStatus = SweetSheet.Status.SHOWING;
            sliderIm.setVisibility(View.INVISIBLE);
            mRV.setVisibility(View.GONE);
        }

        @Override
        public void onEnd() {


            if(  mStatus==SweetSheet.Status.SHOWING) {

                mStatus = SweetSheet.Status.SHOW;

                if(mIsDragEnable) {
                    sliderIm.setVisibility(View.VISIBLE);
                    sliderIm.circularReveal(sliderIm.getWidth() / 2, sliderIm.getHeight() / 2, 0, sliderIm.getWidth());
                }
            }

        }

        @Override
        public void onContentShow() {
            mRV.setVisibility(View.VISIBLE);
            mRV.setAdapter(mAdapter);
            mRV.scheduleLayoutAnimation();
        }
    }
}
