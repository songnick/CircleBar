package com.github.songnick;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by qfsong on 15/9/7.
 */
public class LinearAnimation extends Animation{

    private LinearAnimationListener mListener = null;

    public interface LinearAnimationListener {
        void applyTans(float interpolatedTime);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (mListener != null)
            mListener.applyTans(interpolatedTime);
    }

    public void setLinearAnimationListener(LinearAnimationListener listener){
        mListener = listener;
    }
}
