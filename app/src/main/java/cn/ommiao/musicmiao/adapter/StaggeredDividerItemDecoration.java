package cn.ommiao.musicmiao.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;

import cn.ommiao.musicmiao.R;

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
        int spanIndex = params.getSpanIndex();
        outRect.top = 0;
        if (spanIndex % context.getResources().getInteger(R.integer.search_result_span_count) == 0) {
            outRect.left = interval;
            outRect.right = interval;
        } else {
            outRect.right = interval;
        }
        outRect.bottom = interval;
    }

}
