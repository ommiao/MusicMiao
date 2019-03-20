package cn.ommiao.musicmiao.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
        int spanCount = context.getResources().getInteger(R.integer.search_result_span_count);
        int childPosition = parent.getChildAdapterPosition(view);
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = params.getSpanIndex();
        //顶部上方边距
        if(childPosition < spanCount){
            outRect.top = interval;
        } else {
            outRect.top = 0;
        }
        if (spanIndex % spanCount == 0) {
            outRect.left = interval;
            outRect.right = interval / 2;
        } else {
            outRect.left = interval / 2;
            outRect.right = interval;
        }
        outRect.bottom = interval;
    }

}
