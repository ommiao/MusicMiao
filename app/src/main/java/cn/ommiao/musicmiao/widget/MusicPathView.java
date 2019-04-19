package cn.ommiao.musicmiao.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.widget.playpause.SimpleAnimatorListener;

public class MusicPathView extends View {

    private static final long DURATION = 800;

    private int width = 200, height = 200;
    private Path pathStart = new Path(), pathEnd = new Path();
    private PathMeasure pathMeasureStart = new PathMeasure(), pathMeasureEnd = new PathMeasure();
    private Path processPathStart = new Path(), processPathEnd = new Path();
    private Paint paint = new Paint();
    private ValueAnimator animatorStart, animatorEnd;
    private float processStart = 0, processEnd = 0;
    private float startRatio = 0, endRatio = 0;

    private Path pathBackStart = new Path(), pathBackEnd = new Path();
    private PathMeasure pathBackMeasureStart = new PathMeasure(), pathBackMeasureEnd = new PathMeasure();
    private Path processPathBackStart = new Path(), processPathBackEnd = new Path();
    private Paint paintBack = new Paint();
    private ValueAnimator animatorBackStart, animatorBackEnd;
    private float processBackStart = 0, processBackEnd = 0;
    private float startBackRatio = 0, endBackRatio = 0;
    private boolean isAnimating = false;

    public MusicPathView(Context context) {
        this(context, null);
    }

    public MusicPathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MusicPathView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPathAndPaint();
        initAnimation();
    }

    private void initPathAndPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(25);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLocal));

        pathStart.moveTo(155, 32);
        pathStart.lineTo(102, 22);

        pathEnd.moveTo(96, 14);
        pathEnd.lineTo(126, 100);
        pathEnd.arcTo(80 - 50, 120 - 50, 80 + 50, 120 + 50, -30, 360, false);

        pathMeasureStart.setPath(pathStart, false);
        pathMeasureEnd.setPath(pathEnd, false);

        float startLength = pathMeasureStart.getLength();
        float endLength = pathMeasureEnd.getLength();
        float allLength = startLength + endLength;
        startRatio = startLength / allLength;
        endRatio = endLength / allLength;



        paintBack.setAntiAlias(true);
        paintBack.setStyle(Paint.Style.STROKE);
        paintBack.setStrokeCap(Paint.Cap.BUTT);
        paintBack.setStrokeJoin(Paint.Join.BEVEL);
        paintBack.setStrokeWidth(25);
        paintBack.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        pathBackStart.moveTo(102, 22);
        pathBackStart.lineTo(155, 32);

        pathBackEnd.moveTo(126, 100);
        pathBackEnd.arcTo(80 - 50, 120 - 50, 80 + 50, 120 + 50, 330, -360, true);
        pathBackEnd.lineTo(96, 14);

        pathBackMeasureStart.setPath(pathBackStart, false);
        pathBackMeasureEnd.setPath(pathBackEnd, false);

        float startBackLength = pathBackMeasureStart.getLength();
        float endBackLength = pathBackMeasureEnd.getLength();
        float allBackLength = startBackLength + endBackLength;
        startBackRatio = startBackLength / allBackLength;
        endBackRatio = endBackLength / allBackLength;

    }

    private void initAnimation(){

        animatorBackStart = ValueAnimator.ofFloat(0, 1);
        animatorBackStart.setDuration((long) (DURATION * startBackRatio));
        animatorBackStart.setInterpolator(new DecelerateInterpolator());
        animatorBackStart.addUpdateListener(animation -> {
            processBackStart = (float) animation.getAnimatedValue();
            invalidate();
        });
        animatorBackStart.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                if(onAnimationEndListener != null){
                    onAnimationEndListener.onAnimationEnd();
                }
            }
        });

        animatorBackEnd = ValueAnimator.ofFloat(0, 1);
        animatorBackEnd.setDuration((long) (DURATION * endBackRatio));
        animatorBackEnd.setInterpolator(new AccelerateInterpolator());
        animatorBackEnd.addUpdateListener(animation -> {
            processBackEnd = (float) animation.getAnimatedValue();
            invalidate();
        });
        animatorBackEnd.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorBackStart.start();
            }
        });

        animatorEnd = ValueAnimator.ofFloat(0, 1);
        animatorEnd.setDuration((long) (DURATION * endRatio));
        animatorEnd.setInterpolator(new DecelerateInterpolator());
        animatorEnd.addUpdateListener(animation -> {
            processEnd = (float) animation.getAnimatedValue();
            invalidate();
        });
        animatorEnd.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorBackEnd.start();
            }
        });

        animatorStart = ValueAnimator.ofFloat(0, 1);
        animatorStart.setDuration((long) (DURATION * startRatio));
        animatorStart.setInterpolator(new AccelerateInterpolator());
        animatorStart.addUpdateListener(animation -> {
            processStart = (float) animation.getAnimatedValue();
            invalidate();
        });
        animatorStart.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorEnd.start();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        processPathStart.reset();
        processPathEnd.reset();
        pathMeasureStart.getSegment(0, pathMeasureStart.getLength() * processStart, processPathStart, true);
        pathMeasureEnd.getSegment(0, pathMeasureEnd.getLength() * processEnd, processPathEnd, true);
        canvas.drawPath(processPathStart, paint);
        canvas.drawPath(processPathEnd, paint);

        processPathBackStart.reset();
        processPathBackEnd.reset();
        pathBackMeasureStart.getSegment(0, pathBackMeasureStart.getLength() * processBackStart, processPathBackStart, true);
        pathBackMeasureEnd.getSegment(0, pathBackMeasureEnd.getLength() * processBackEnd, processPathBackEnd, true);
        canvas.drawPath(processPathBackStart, paintBack);
        canvas.drawPath(processPathBackEnd, paintBack);
    }

    public void startAnimation(){
        if(isAnimating){
            return;
        }
        processStart = 0;
        processEnd = 0;
        processBackStart = 0;
        processBackEnd = 0;
        animatorStart.start();
        isAnimating = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onAnimationEndListener = null;
    }

    private OnAnimationEndListener onAnimationEndListener;

    public void setOnAnimationEndListener(OnAnimationEndListener onAnimationEndListener) {
        this.onAnimationEndListener = onAnimationEndListener;
    }

    public interface OnAnimationEndListener{
        void onAnimationEnd();
    }
}
