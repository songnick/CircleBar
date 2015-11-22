package com.github.songnick.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by SongNick on 15/9/10.
 */
public class NotchProgress extends View {
    public NotchProgress(Context context) {
        this(context, null);
    }

    public NotchProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotchProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(20, 20, 120, 120), 120, 300,false, paint);
    }
}
