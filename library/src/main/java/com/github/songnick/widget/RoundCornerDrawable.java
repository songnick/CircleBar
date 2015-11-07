package com.github.songnick.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by SongNick on 15/11/7.
 */
public class RoundCornerDrawable extends Drawable{

    private int mWidth = 0;
    private int mHeight = 0;
    private float mCorner = 0.0f;

    public RoundCornerDrawable(){

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(0, 0, getIntrinsicWidth(), getIntrinsicHeight()), getIntrinsicHeight()/2,getIntrinsicHeight()/2, null);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

}
