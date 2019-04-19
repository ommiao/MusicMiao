package cn.ommiao.musicmiao.utils;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class SpringUtil {

    private static final double tension = 50;
    private static final double frictiion = 5;

    private static SpringSystem mSpringSystem = SpringSystem.create();

    public static void translationYAnimation(View target, float from, float to){
        translationYAnimation(target, from, to, null);
    }

    public static void translationYAnimation(final View target, float from, float to, OnSpringAnimationEndListener listener) {
        boolean isUp = from > to;
        Spring spring = mSpringSystem.createSpring();
        spring.setCurrentValue(from);
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, frictiion));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                target.setTranslationY((float) spring.getCurrentValue());
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                if(listener != null){
                    listener.onSpringAnimationEnd();
                }
            }
        });
        spring.setEndValue(to);
    }

    public interface OnSpringAnimationEndListener{
        void onSpringAnimationEnd();
    }

}
