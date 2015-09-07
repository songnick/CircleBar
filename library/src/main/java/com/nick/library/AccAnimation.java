package com.nick.library;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by qfsong on 15/9/7.
 */
public class AccAnimation extends Animation{

    private AccAnimationListener mListener = null;

    public interface AccAnimationListener {
        void applyTans(float interpolatedTime);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (mListener != null)
            mListener.applyTans(interpolatedTime);
    }

    public void setAccAnimationListener(AccAnimationListener listener){
        mListener = listener;
    }
}
