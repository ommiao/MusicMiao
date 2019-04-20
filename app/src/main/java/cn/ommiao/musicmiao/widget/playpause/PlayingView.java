package cn.ommiao.musicmiao.widget.playpause;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.ommiao.musicmiao.R;
import cn.ommiao.musicmiao.utils.UIUtil;

public class PlayingView extends View {

    //画笔
    private Paint paint;

    //跳动指针的集合
    private List<Pointer> pointers;

    //跳动指针的数量
    private int pointerNum;

    //逻辑坐标 原点
    private float basePointX;
    private float basePointY;

    //指针间的间隙  默认5dp
    private float pointerPadding;

    //每个指针的宽度 默认3dp
    private float pointerWidth;

    //指针的颜色
    private int pointerColor;

    //控制开始/停止
    private boolean isPlaying = false;

    //子线程
    private Thread myThread;

    //指针波动速率
    private int pointerSpeed;


    public PlayingView(Context context) {
        this(context, null);
    }

    public PlayingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayingView);
        pointerColor = ta.getColor(R.styleable.PlayingView_pointerColor, Color.WHITE);
        pointerNum = ta.getInt(R.styleable.PlayingView_pointerNum, 4);//指针的数量，默认为4
        pointerWidth = UIUtil.dp2px(ta.getFloat(R.styleable.PlayingView_pointerWidth, 5f));//指针的宽度，默认5dp
        pointerSpeed = ta.getInt(R.styleable.PlayingView_pointerDrawDelay, 40);
        ta.recycle();
        init();
    }

    /**
     * 初始化画笔与指针的集合
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(pointerColor);
        pointers = new ArrayList<>();
        for (int i = 0; i < pointerNum; i++) {
            //创建指针对象，利用0~1的随机数 乘以 可绘制区域的高度。作为每个指针的初始高度。
            pointers.add(new Pointer(0f));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //获取逻辑原点的，也就是画布左下角的坐标。这里减去了paddingBottom的距离
        basePointY = getHeight() - getPaddingBottom();
        //计算每个指针之间的间隔  总宽度 - 左右两边的padding - 所有指针占去的宽度  然后再除以间隔的数量
        pointerPadding = (getWidth() - getPaddingLeft() - getPaddingRight() - pointerWidth * pointerNum) / (pointerNum - 1);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将x坐标移动到逻辑原点，也就是左下角
        basePointX = 0f + getPaddingLeft();
        //循环绘制每一个指针。
        for (int i = 0; i < pointers.size(); i++) {
            //绘制指针，也就是绘制矩形
            canvas.drawRect(basePointX,
                    basePointY - pointers.get(i).getHeight(),
                    basePointX + pointerWidth,
                    basePointY,
                    paint);
            basePointX += (pointerPadding + pointerWidth);
        }
    }

    /**
     * 开始播放
     */
    public void start() {
        if (!isPlaying) {
            if (myThread == null) {//开启子线程
                myThread = new Thread(new MyRunnable());
                myThread.start();
            }
            isPlaying = true;//控制子线程中的循环
        }
    }

    /**
     * 停止子线程，并刷新画布
     */
    public void stop() {
        isPlaying = false;
        invalidate();
    }

    /**
     * 处理子线程发出来的指令，然后刷新布局
     */
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };

    /**
     * 子线程，循环改变每个指针的高度
     */
    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            if(isPlaying){
                for (float i = 0; i < Integer.MAX_VALUE; i += 0.1) {//创建一个死循环，每循环一次i+0.1
                    try {
                        for (int j = 0; j < pointers.size(); j++) { //循环改变每个指针高度
                            float rate = (float) Math.abs(Math.sin(i + j));//利用正弦有规律的获取0~1的数。
                            pointers.get(j).setHeight((basePointY - getPaddingTop()) * rate); //rate 乘以 可绘制高度，来改变每个指针的高度
                        }
                        Thread.sleep(pointerSpeed);//休眠一下下，可自行调节
                        myHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 指针对象
     */
    public class Pointer {
        private float height;

        private Pointer(float height) {
            this.height = height;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }
    }
}

