package com.github.songnick;

import android.animation.TypeEvaluator;
import android.util.Log;

/**
 * Created by qfsong on 15/9/7.
 */
public class AccTypeEvaluator implements TypeEvaluator<Float>{

    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        Log.d("", " current fraction == " + fraction);
        return fraction * (endValue - startValue);
    }

}
