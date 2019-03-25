package cn.ommiao.musicmiao.widget.playpause;

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

    private static final float LOADING_ANGLE = 90;
    private static final float START_LOADING_ANGLE = 270;

    private static final long TRANSITION_LOADING_DURATION = 500;
    private static final long LOADING_PER_ROUND = 2000;
    private static final long TRANSITIOM_STOP_PROGRESS = 800;

    private static final long PLAY_PAUSE_ANIMATION_DURATION = 300;

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

    private PlayStatus playStatus = PlayStatus.NONE; //初始播放状态

    private enum PlayStatus{
        NONE, START_LOADING, LOADING, STOP_LOADING, PROGRESS, STOP_PROGRESS, PAUSE
    }

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

    public void setCircleAlpha(int alpha) {
        circleAlpha = alpha;
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

    private float startAngle = 270, sweepAngle = 0;

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
        canvas.drawArc(5, 5, mWidth - 5, mHeight - 5, startAngle, sweepAngle, false, mProgressPaint);
    }

    private void startLoading(){
        playStatus = PlayStatus.START_LOADING;
        ValueAnimator startLoadingAnimator = ValueAnimator.ofFloat(0, LOADING_ANGLE);
        startLoadingAnimator.setDuration(TRANSITION_LOADING_DURATION);
        startLoadingAnimator.setInterpolator(new LinearInterpolator());
        startLoadingAnimator.addUpdateListener(animation -> {
            sweepAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        startLoadingAnimator.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                repeatLoading();
            }
        });
        startLoadingAnimator.start();
    }

    private ValueAnimator loadingAnimator;

    private void repeatLoading() {
        playStatus = PlayStatus.LOADING;
        loadingAnimator = ValueAnimator.ofFloat(START_LOADING_ANGLE, START_LOADING_ANGLE + 360);
        loadingAnimator.setDuration(LOADING_PER_ROUND);
        loadingAnimator.setRepeatCount(999999);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.addUpdateListener(animation -> {
            startAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        loadingAnimator.start();
    }

    private void stopLoading(){
        playStatus = PlayStatus.STOP_LOADING;
        loadingAnimator.cancel();
        ValueAnimator stopLoadingAnimator;
        float stopDistance;
        if(startAngle + LOADING_ANGLE > START_LOADING_ANGLE + 360){
            stopDistance = START_LOADING_ANGLE + 360 * 2;
        } else {
            stopDistance = START_LOADING_ANGLE + 360;
        }
        stopLoadingAnimator = ValueAnimator.ofFloat(startAngle, stopDistance);
        stopLoadingAnimator.setDuration(LOADING_PER_ROUND * (long) ((long)stopDistance - startAngle) / 360L);
        stopLoadingAnimator.setInterpolator(new LinearInterpolator());
        stopLoadingAnimator.addUpdateListener(animation -> {
            startAngle = (float) animation.getAnimatedValue();
            if(startAngle + sweepAngle > stopDistance){
                sweepAngle = stopDistance - startAngle;
            } else {
                sweepAngle = LOADING_ANGLE;
            }
            sweepAngle += getProgressAngle();
            invalidate();
        });
        stopLoadingAnimator.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                startAngle = START_LOADING_ANGLE;
                playStatus = getProgressAngle() > 0 ? PlayStatus.PROGRESS : PlayStatus.NONE;
            }
        });
        stopLoadingAnimator.start();
    }

    private float getProgressAngle(){
        return 360 * progress;
    }

    private boolean mIsPlay;

    public boolean isPlay() {
        return mIsPlay;
    }

    public void play() {
        if(isTransitionStatus()){
            return;
        }
        pauseToPlay();
        if(playStatus == PlayStatus.PAUSE){
            playStatus = PlayStatus.PROGRESS;
        } else if(playStatus == PlayStatus.NONE){
            startLoading();
        }
    }

    public void pause() {
        if(isTransitionStatus()){
            return;
        }
        playToPause();
        if(playStatus == PlayStatus.PROGRESS){
            playStatus = PlayStatus.PAUSE;
        } else if(playStatus == PlayStatus.LOADING){
            stopLoading();
        }
    }

    private float progress = 0f;
    public void setProgress(float progress){
        if(playStatus == PlayStatus.NONE){
            return;
        }
        this.progress = progress;
        if(isTransitionStatus()){
            return;
        }
        //playStatus = PlayStatus.PROGRESS;
        if(playStatus == PlayStatus.LOADING){
            stopLoading();
        } else if(playStatus == PlayStatus.PROGRESS){
            sweepAngle = getProgressAngle();
            invalidate();
            if(getProgressAngle() >= 360){
                stopProgress();
            }
        }
    }

    private void stopProgress() {
        playStatus = PlayStatus.STOP_PROGRESS;
        ValueAnimator stopProgressAnimator = ValueAnimator.ofFloat(sweepAngle, 0);
        stopProgressAnimator.setDuration(TRANSITIOM_STOP_PROGRESS);
        stopProgressAnimator.setInterpolator(new LinearInterpolator());
        stopProgressAnimator.addUpdateListener(animation -> {
            sweepAngle = (float) animation.getAnimatedValue();
            invalidate();
        });
        stopProgressAnimator.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                progress = 0f;
                playStatus = PlayStatus.NONE;
                playToPause();
            }
        });
        stopProgressAnimator.start();
    }

    private void playToPause(){
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
    }

    private void pauseToPlay(){
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
    }

    /**
     * 判断是否为过渡状态，过渡状态不接受状态切换命令
     * @return
     */
    private boolean isTransitionStatus(){
        return playStatus == PlayStatus.START_LOADING ||
                playStatus == PlayStatus.STOP_LOADING ||
                playStatus == PlayStatus.STOP_PROGRESS;
    }

}
