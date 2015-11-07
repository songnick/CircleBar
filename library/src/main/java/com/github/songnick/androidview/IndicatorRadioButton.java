package com.github.songnick.androidview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.nick.library.R;

/**
 * Created by SongNick on 15/9/21.
 */
public class IndicatorRadioButton extends RadioButton {

	private Paint mPaint = null;

	public IndicatorRadioButton(Context context) {
		super(context);
		initPaint();
	}

	public IndicatorRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public IndicatorRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
	}

	private void initPaint(){
		mPaint = new Paint();
		mPaint.setStrokeWidth(getResources().getDimension(R.dimen.default_size));
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getMeasuredHeight();
		int indicatorHeight = getResources().getDimensionPixelSize(R.dimen.radio_button_indicator_height);
		if (isChecked()){
			mPaint.setColor(getCurrentTextColor());
		}else {
			mPaint.setColor(Color.TRANSPARENT);
		}
		canvas.drawRect(0, height - indicatorHeight, getMeasuredWidth(), height, mPaint);
	}

}
