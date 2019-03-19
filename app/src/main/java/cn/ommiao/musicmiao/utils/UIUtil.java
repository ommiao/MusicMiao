package cn.ommiao.musicmiao.utils;

import android.util.TypedValue;

import cn.ommiao.musicmiao.core.App;

public class UIUtil {

    public static int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, App.getContext().getResources().getDisplayMetrics());
    }

}
