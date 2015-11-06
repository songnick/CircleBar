package com.github.songnick.androidview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nick.library.R;

/**
 * Created by qfsong on 15/11/6.
 */
public class SlidProgressbar extends RelativeLayout {

    private static final int FIRST_BAR_ID = 0x011 + 11;
    private static final int THUMB_ID = 0x011 + 22;

    private LinearLayout mFirstBar = null;
    private RelativeLayout.LayoutParams mFirstBarLp = null;

    private TextView mThumb = null;
    private RelativeLayout.LayoutParams mThumbLp = null;
    private float mThumbOriginalLeft = 0;
    private float mHitDowX = 0;

    private LinearLayout mSecondBar = null;
    private RelativeLayout.LayoutParams mSecondBarLp = null;

    public SlidProgressbar(Context context) {
        this(context, null);
    }

    public SlidProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initViewTouchEvent();
    }

    private void init(Context context){
        mFirstBar = new LinearLayout(context);
        mFirstBar.setBackgroundResource(R.drawable.firtbar_bkg);
        int height = context.getResources().getDimensionPixelSize(R.dimen.bars_height);
        mFirstBarLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mFirstBarLp.addRule(CENTER_IN_PARENT);
        addView(mFirstBar, mFirstBarLp);

        mThumb = new TextView(context);
        mThumb.setBackgroundResource(R.drawable.slid_thumb_bkg);
        mThumbLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mThumbLp.addRule(CENTER_VERTICAL);
        mThumbLp.addRule(ALIGN_PARENT_LEFT);
        mThumbLp.leftMargin = 0;
        addView(mThumb, mThumbLp);
        mThumb.setId(THUMB_ID);
        mThumb.setGravity(Gravity.CENTER);
        mThumb.setText("10%");

        mSecondBar = new LinearLayout(context);
        mSecondBar.setBackgroundResource(R.drawable.secondbar_bkg);
        mSecondBarLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mSecondBarLp.addRule(CENTER_VERTICAL);
        mSecondBarLp.addRule(RIGHT_OF, THUMB_ID);
        addView(mSecondBar,1, mSecondBarLp);
    }

    private void initViewTouchEvent(){
        mThumb.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mHitDowX = event.getRawX();
                        mThumbOriginalLeft = mThumbLp.leftMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //right thumb started position is right
                        //so the user slit this thumb will generate negative count
                        float distance = (event.getRawX() - mHitDowX);
                        mThumbLp.leftMargin = (int) (mHitDowX + distance);
                        //confirm this thumb is show, no anywhere is hide
//                        if (mMDThumbRl.leftMargin < mLTRl.leftMargin) {
//                            mMDThumbRl.leftMargin = mLTRl.leftMargin;
//                        }
//                        if (mMDThumbRl.leftMargin > getMeasuredWidth() - mRTRl.rightMargin) {
//                            Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin);
//                            mMDThumbRl.leftMargin = getMeasuredWidth() - mRTRl.rightMargin;
//                        }
//                        Log.d(TAG, " current middle thumb left margin = " + mMDThumbRl.leftMargin + " sss = " + (getMeasuredWidth() - getStartEndMargin()));
                        updateViewLayout(mThumb, mThumbLp);
                        updateViewLayout(mSecondBar, mSecondBarLp);
//                        seekTo(SlideType.PLAY, mMDThumbRl.leftMargin);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
    }
}
