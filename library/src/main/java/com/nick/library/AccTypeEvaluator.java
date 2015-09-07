package com.nick.library;

import android.animation.TypeEvaluator;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.Interpolator;

/**
 * Created by qfsong on 15/9/7.
 */
public class AccTypeEvaluator implements TypeEvaluator<Float>{

    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        Log.d("", " current fraction == " + fraction);
        return fraction;
    }

}
