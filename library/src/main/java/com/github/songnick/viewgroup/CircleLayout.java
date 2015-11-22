package com.github.songnick.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by qfsong on 15/11/21.
 */
public class CircleLayout extends ViewGroup {

    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(){

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
