package cn.ommiao.musicmiao.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyNestedScrollView extends NestedScrollView {

    private boolean interceptUp = true;
    private boolean interceptDown = true;

    public MyNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInterceptUp(boolean interceptUp) {
        this.interceptUp = interceptUp;
    }

    public void setInterceptDown(boolean interceptDown) {
        this.interceptDown = interceptDown;
    }

    private int startY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dY = (int) (ev.getY() - startY);
                if(dY > 0){ //下滑
                    return interceptDown;
                } else { //上滑
                    return interceptUp;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
