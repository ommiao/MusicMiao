package cn.ommiao.musicmiao.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

public class StaggeredDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private int interval;

    public StaggeredDividerItemDecoration(Context context, int interval) {
        this.context = context;
        this.interval = interval;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        // 获取item在span中的下标
        int spanIndex = params.getSpanIndex();
        int interval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                this.interval, context.getResources().getDisplayMetrics());
        outRect.top = 0;
        // 中间间隔
        if (spanIndex % 2 == 0) {
            outRect.left = interval;
            outRect.right = interval;
        } else {
            // item为奇数位，设置其左间隔为5dp
            outRect.right = interval;
        }
        // 下方间隔
        outRect.bottom = interval;
    }

}
