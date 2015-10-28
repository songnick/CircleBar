package com.github.songnick.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by qfsong on 15/10/26.
 */
public class SlideViewPager extends ViewGroup {
    public SlideViewPager(Context context) {
        super(context);
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
            getChildAt(0).layout(200, 100, 880, 1100);
        getChildAt(1).layout(980, 100, 1660, 1100);
        getChildAt(2).layout(1760, 100, 2440, 1100);
        getChildAt(1).setScaleX(0.8f);
        getChildAt(1).setScaleY(0.8f);
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(680, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(1000, MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

}
