package com.github.songnick.androidview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by sfhong on 2015/6/8.
 */
public class NoSlideViewPager extends ViewPager {
    public NoSlideViewPager(Context context) {
        super(context);
    }

    public NoSlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
