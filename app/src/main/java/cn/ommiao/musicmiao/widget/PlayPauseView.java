package cn.ommiao.musicmiao.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import cn.ommiao.musicmiao.R;


public class PlayPauseView extends FrameLayout {


    private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

    private final PlayPauseDrawable mDrawable;
    private final Paint mPaint = new Paint();
    private int mDrawableColor;
    public boolean isDrawCircle;
    public int circleAlpha;
    private final Paint mProgressPaint = new Paint();

    private AnimatorSet mAnimatorSet;
    private int mBackgroundColor;
    private int mWidth;
    private int mHeight;

    private LoadingStatus status = LoadingStatus.NO_STRAT;
    private boolean shouldStopLoading = false;

    public enum LoadingStatus{
        NO_STRAT, START_LOADING, LOADING, STOP_LOADING, PROGRESS, STOP_PROGRESS, PAUSE
    }

    private ValueAnimator loadingAnimator;

    public PlayPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseView);
        isDrawCircle = typedArray.getBoolean(R.styleable.PlayPauseView_isCircleDraw, true);
        circleAlpha = typedArray.getInt(R.styleable.PlayPauseView_circleAlpha, 255);
        mBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary);
        mDrawableColor = Color.WHITE;
        typedArray.recycle();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(circleAlpha);
        mPaint.setColor(mBackgroundColor);
        mDrawable = new PlayPauseDrawable(context, mDrawableColor);
        mDrawable.setCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;
        setOutlineProvider(new ViewOutlineProvider() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        setClipToOutline(true);
    }

    public void setCircleColor(@ColorInt int color) {
        mBackgroundColor = color;
        invalidate();
    }

    public void setDrawableColor(@ColorInt int color) {
        mDrawableColor = color;
        mDrawable.setDrawableColor(color);
        invalidate();
    }

    public void setCircleAlpah(int alpah) {
        circleAlpha = alpah;
        invalidate();
    }

    private int getCircleColor() {
        return mBackgroundColor;
    }

    public int getDrawableColor() {
        return mDrawableColor;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private float angle = 270, sweepAngle = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackgroundColor);
        final float radius = Math.min(mWidth, mHeight) / 2f;
        if (isDrawCircle) {
            mPaint.setColor(mBackgroundColor);
            mPaint.setAlpha(circleAlpha);
            canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
        }
        mDrawable.draw(canvas);
        mProgressPaint.setColor(Color.WHITE);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(10f);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(5, 5, mWidth - 5, mHeight - 5, angle, sweepAngle, false, mProgressPaint);
    }

    private void startLoading() {
        if(status != LoadingStatus.NO_STRAT){
            return;
        }
        status = LoadingStatus.START_LOADING;
        angle = 270;
        sweepAngle = 0;
        ValueAnimator startLoadingAnimator = ValueAnimator.ofFloat(angle, 270 + 360);
        startLoadingAnimator.setDuration(2000L * (long) (270 + 360 - angle) / 360L);
        startLoadingAnimator.setInterpolator(new LinearInterpolator());
        startLoadingAnimator.addUpdateListener(animation -> {
            angle = (float) animation.getAnimatedValue();
            if (angle -270 < 90) {
                sweepAngle = angle - 270;
            }
            invalidate();
        });
        startLoadingAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(shouldStopLoading){
                    stopLoading();
                } else {
                    loading();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        startLoadingAnimator.start();
    }

    private void loading(){
        status = LoadingStatus.LOADING;
        loadingAnimator = ValueAnimator.ofFloat(270, 270 + 360);
        loadingAnimator.setDuration(2000);
        loadingAnimator.setRepeatCount(100000);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.addUpdateListener(animation -> {
            angle = (float) animation.getAnimatedValue();
            invalidate();
        });
        loadingAnimator.start();
    }


    private void stopLoading() {
        if(status != LoadingStatus.LOADING){
            return;
        }
        if(status == LoadingStatus.START_LOADING){
            shouldStopLoading = true;
            return;
        }
        status = LoadingStatus.STOP_LOADING;
        loadingAnimator.cancel();
        ValueAnimator exitLoadingAnimator = ValueAnimator.ofFloat(angle, 270 + 360);
        exitLoadingAnimator.setDuration(2000L * (long) (270 + 360 - angle) / 360L);
        exitLoadingAnimator.setInterpolator(new LinearInterpolator());
        exitLoadingAnimator.addUpdateListener(animation -> {
            angle = (float) animation.getAnimatedValue();
            if (270 + 360 - angle < 90) {
                sweepAngle = 270 + 360 - angle;
            }
            sweepAngle += getProgressAngle();
            invalidate();
        });
        exitLoadingAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                angle = 270;
                sweepAngle = getProgressAngle();
                if(shouldShowProgress){
                    setProgress(progress);
                    shouldShowProgress = false;
                    status = LoadingStatus.PROGRESS;
                } else {
                    status = LoadingStatus.NO_STRAT;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        exitLoadingAnimator.start();
    }

    private boolean shouldShowProgress = false;
    private float progress = 0f;

    public void setProgress(float progress){
        this.progress = progress;
        if(status == LoadingStatus.START_LOADING){
            shouldShowProgress = true;
            stopLoading();
        } else if(status == LoadingStatus.STOP_LOADING){
            shouldShowProgress = true;
        } else if(status == LoadingStatus.LOADING){
            shouldShowProgress = true;
            stopLoading();
        } else {
            status = LoadingStatus.PROGRESS;
            angle = 270;
            sweepAngle = getProgressAngle();
            invalidate();
            if(sweepAngle >= 270 + 360){
                if(isPlay()){
                    pause();
                }
                stopProgress();
            }
        }
    }

    private void stopProgress() {
        if(status != LoadingStatus.PROGRESS){
            return;
        }
        status = LoadingStatus.STOP_PROGRESS;
        ValueAnimator stopProgressAnimator = ValueAnimator.ofFloat(270 + 360, 270);
        stopProgressAnimator.setDuration(800L);
        stopProgressAnimator.setInterpolator(new LinearInterpolator());
        stopProgressAnimator.addUpdateListener(animation -> {
            angle = 270;
            sweepAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        stopProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                angle = 270;
                sweepAngle = 0;
                status = LoadingStatus.NO_STRAT;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        stopProgressAnimator.start();
    }

    private float getProgressAngle(){
        return 270 + progress * 360;
    }

    private boolean mIsPlay;

    public boolean isPlay() {
        return mIsPlay;
    }

    //此时为待暂停标识
    public void play() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet = new AnimatorSet();
        mIsPlay = true;
        mDrawable.setmIsPlay(mIsPlay);
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        pausePlayAnim.start();
        if(status != LoadingStatus.PAUSE){
            startLoading();
        }
    }

    //此时为待播放标识
    public void pause() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        mIsPlay = false;
        mDrawable.setmIsPlay(mIsPlay);
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        pausePlayAnim.start();
        if(status != LoadingStatus.PROGRESS){
            stopLoading();
        }
    }

}
