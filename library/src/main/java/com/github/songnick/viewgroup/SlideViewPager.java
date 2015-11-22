package com.github.songnick.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.github.songnick.utils.LogUtils;
import com.nick.library.R;

/**
 * Created by SongNick on 15/10/26.
 */
public class SlideViewPager extends ViewGroup {

    private static final int MARGIN_LEFT_RIGHT = 150;
    private static final int MARGIN_TOP_BOTTOM = 400;
    private static float SCALE_RATIO = 0.8f;

    private static final int MAX_SETTLE_DURATION = 600; // ms

    /**
     * view slide direction left to right
     */
    private static int LEFT_TO_RIGHT = 0x011;

    /***
     * view slide direction right to left
     */

    private static int RIGHT_TO_LEFT = 0x022;

    /**
     * view slide direction invalid
     */
    private static int INVALID_DIRECTION = 0x033;

    /**
     * A null/invalid pointer ID.
     */
    public static final int INVALID_POINTER = -1;

    /**
     * Indicates that the pager is in an idle, settled state. The current page
     * is fully in view and no animation is in progress.
     */
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;

    /**
     * Indicates that the pager is in the process of settling to a final position.
     */
    public static final int SCROLL_STATE_SETTLING = 2;

    public static int SNAP_VELOCITY = 600;

    private static final String TAG = SlideViewPager.class.getSimpleName();
    private float mDownX = 0.0f;
    private float mOriginalX = 0.0f;

    // Last known position/pointer tracking
    private int mActivePointerId = INVALID_POINTER;

    private static final int MIN_FLING_VELOCITY = 400; // dips
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    /**
     * determines speed during scroll
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mCloseEnough;
    private int mCurrentPosition = 0;
    private int mCurrentDir = INVALID_DIRECTION;

    private ScrollerCompat mScroller;

    private float mMarginLeftRight = 0.0f;
    private float mGutterSize = 0.0f;
    private int mTouchSlop = 0;

    private int mSwitchSize = 0;
    private int mScrollState = SCROLL_STATE_IDLE;

    private boolean mIsBeingDragged = false;
    private boolean mIsUnableToDrag = false;

    private OnPagerChangeListener mOnPagerChangeListener = null;

    private final Runnable mEndScrollRunnable = new Runnable() {
        public void run() {
            setScrollState(SCROLL_STATE_IDLE);
        }
    };


    public interface OnPagerChangeListener{

        /**
         * this method will be invoked, when new page is selected
         * @param position the position of new page selected
         * */
        void onPageSelected(int position);

        /**
         * when the page is scrolling,
         * @param position
         * @param positionOffset
         * @param positionOffsetPixel
         * */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixel);

        /**
         * scroll state of current page
         *
         * @param state scroll state
         * @see SlideViewPager#SCROLL_STATE_DRAGGING
         * @see SlideViewPager#SCROLL_STATE_IDLE
         * @see SlideViewPager#SCROLL_STATE_SETTLING
         *
         * */
        void onPageScrollStateChanged(int state);
    }

    public SlideViewPager(Context context) {

        this(context, null);
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleViewPager, 0, 0);
        mMarginLeftRight = a.getDimension(R.styleable.ScaleViewPager_marginLeftRight, 0);
        mGutterSize = a.getDimensionPixelSize(R.styleable.ScaleViewPager_gutterSize, 0);
        a.recycle();
        init(context);
    }

    /**
     * initialize some config
     *
     * @param context this view's context
     */
    private void init(Context context) {
        setWillNotDraw(false);
        mScroller = ScrollerCompat.create(context, sInterpolator);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        LogUtils.LogD(TAG, " touch slop == " + mTouchSlop);
        final float density = context.getResources().getDisplayMetrics().density;
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
    }

    /**
     * Interpolator defining the animation curve for mScroller
     */
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    private void setScrollState(int state) {
        mScrollState = state;
        if (mOnPagerChangeListener != null){
            mOnPagerChangeListener.onPageScrollStateChanged(state);
        }
    }


    /**
     * The result of a call to this method is equivalent to
     */
    public void cancel() {

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.LogD(TAG, " onInterceptTouchEvent hit touch event");
        final int actionIndex = MotionEventCompat.getActionIndex(event);
        mActivePointerId = MotionEventCompat.getPointerId(event, 0);

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                //calculate moving distance
                float distance = -(event.getRawX() - mDownX);
                mDownX = event.getRawX();
                LogUtils.LogD(TAG, " current distance == " + distance);
                performDrag((int)distance);
                break;
            case MotionEvent.ACTION_UP:
                releaseViewForTouchUp();
                cancel();
                break;
        }
        return true;
    }

    /***
     * drag the this view smooth scale
     * @param distance should be drag
     * */
    private void performDrag(int distance) {
        if (mOnPagerChangeListener != null){
            mOnPagerChangeListener.onPageScrollStateChanged(SCROLL_STATE_DRAGGING);
        }
        LogUtils.LogD(TAG, " perform drag distance == " + distance);
        scrollBy(distance, 0);
        if (distance < 0) {
            dragScaleShrinkView(mCurrentPosition, LEFT_TO_RIGHT);
        } else {
            LogUtils.LogD(TAG, " current direction is right to left and current child position =  " + mCurrentPosition);
            dragScaleShrinkView(mCurrentPosition, RIGHT_TO_LEFT);
        }
    }

    /**
     * user move the view and release view
     * but there is also some question for tow pointer event
     */
    private void releaseViewForTouchUp() {
//        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
//        final float xvel = clampMag(
//                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
//                mMinVelocity, mMaxVelocity);
//        if (xvel != 0){
//            smoothScrollToDes();
//        }
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
                velocityTracker, mActivePointerId);
        float xVel = mVelocityTracker.getXVelocity();
        if (xVel > SNAP_VELOCITY && mCurrentPosition > 0) {
            smoothScrollToItemView(mCurrentPosition - 1, true);
        } else if (xVel < -SNAP_VELOCITY && mCurrentPosition < getChildCount() - 1) {
            smoothScrollToItemView(mCurrentPosition + 1, true);
        } else {
            smoothScrollToDes();
        }
        setScrollState(SCROLL_STATE_SETTLING);
    }

    public void setCurrentItem(int position, boolean smooth) {

        if (position >= 0 && position <= getChildCount() - 1){
            smoothScrollToItemView(position, true);
        }

    }

    private int determineTargetPosition(int currentPosition, int velocity, int deltaX) {

        int targetPosition = 0;

        if (Math.abs(velocity) > mMinimumVelocity) {
            targetPosition = velocity > 0 ? currentPosition : currentPosition + 1;
        }

        return targetPosition;
    }

    // We want the duration of the page snap animation to be influenced by the distance that
    // the screen has to travel, however, we don't want this duration to be effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect that the distance
    // of travel has on the overall snap duration.
    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }


    /**
     * when user touch up, invoke this method,
     * and scroll to confirmed view smoothly
     */
    private void smoothScrollToDes() {
        int scrollX = getScrollX();
        //confirm the position to scroll
        int position = (scrollX + mSwitchSize / 2) / mSwitchSize;
        LogUtils.LogD(TAG, " smooth scroll to des position == before =" + mCurrentPosition
                + " scroll X = " + scrollX + " switch size == " + mSwitchSize + " position == " + position);
        smoothScrollToItemView(position, mCurrentPosition == position);
//        if (mCurrentPosition != position){
//            if (mOnPagerChangeListener != null){
//                mOnPagerChangeListener.onPageSelected(position);
//            }
//        }
//        mCurrentPosition = position;
//
//        int dx = position * (getMeasuredWidth() - (int) mMarginLeftRight * 2) - scrollX;
//        LogUtils.LogD(TAG, " smooth scroll to des position == " + position + " dx = " + dx + " scroll x == " + scrollX);
//        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.min(Math.abs(dx) * 2, MAX_SETTLE_DURATION));
//        invalidate();
    }

    /**
     * scroll to confirmed position of child
     *
     * @param position the view position in this {@link #ViewGroup}
     */
    private void smoothScrollToItemView(int position, boolean pageSelected) {
        mCurrentPosition = position;
        if (mCurrentPosition > getChildCount() - 1) {
            mCurrentPosition = getChildCount() - 1;
        }
        if (mOnPagerChangeListener != null && pageSelected){
            mOnPagerChangeListener.onPageSelected(position);
        }
        int dx = position * (getMeasuredWidth() - (int) mMarginLeftRight * 2) - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.min(Math.abs(dx) * 2, MAX_SETTLE_DURATION));
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int dx = mCurrentPosition * mSwitchSize - mScroller.getCurrX();
            LogUtils.LogD(TAG, " compute scroll dx == " + dx + " position == " + mCurrentPosition);
//                int position = mCurrentPosition;
//                if (mCurrentDir == RIGHT_TO_LEFT){
//                    LogUtils.LogD(TAG, " current direction == right to left" );
//                    position = mCurrentPosition - 1;
//                }else if (mCurrentDir == LEFT_TO_RIGHT){
//                    LogUtils.LogD(TAG, " current direction == left to right " );
//                    position = mCurrentPosition + 1;
//                }
            dragScaleShrinkView(mCurrentPosition, mCurrentDir);
//            }
            scrollTo(mScroller.getCurrX(), 0);
        }
        completeScroll(true);

    }

    /**
     * whether the scroll animation is end
     * @param postEvents post run the runnable event
     * */
    private void completeScroll(boolean postEvents){

        boolean needPopulate = mScrollState == SCROLL_STATE_SETTLING;

        if (needPopulate){
            if (postEvents) {
                ViewCompat.postOnAnimation(this, mEndScrollRunnable);
            } else {
                mEndScrollRunnable.run();
            }

        }

    }

    /**
     * when the user drag the view, current view should be scaled or shrink and next view or previous view size should be changed
     *
     * @param position the position of dragging view
     *
     * @param direction the direction of drag
     *                  @see SlideViewPager#RIGHT_TO_LEFT
     *                  @see SlideViewPager#LEFT_TO_RIGHT
     *                  @see SlideViewPager#INVALID_DIRECTION
     * */
    private void dragScaleShrinkView(int position, int direction) {

        int distance = getScrollX() - position * mSwitchSize;
        mCurrentDir = direction;
        View scaleView = null;
        View shrinkView = null;
        float scaleRatio = 0.0f;
        float shrinkRatio = 0.0f;
        //if distance is bigger than zero,
        //current drag action is between current page and next page
        //otherwise is between front page and current page
        if (distance > 0) {
            int moveSize = getScrollX() - position * mSwitchSize;
            float ratio = (float) moveSize / mSwitchSize;//this value is from 0 to 1;
            if (direction == LEFT_TO_RIGHT) {//value may be from X to 0
                if (position >= 0) {
                    //current view should be scaled
                    scaleView = getChildAt(position);
                    //next view should be shrink
                    shrinkView = getChildAt(position + 1);
                    shrinkRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * ratio;
                    scaleRatio = 1.0f - (1.0f - SCALE_RATIO) * ratio;
                    LogUtils.LogD(TAG, " current scale ratio = " + scaleRatio + " shrink ratio = " + shrinkRatio + " ratio = " + ratio);
                }
            } else if (direction == RIGHT_TO_LEFT) {
                if (position < getChildCount() - 1) {
                    //
                    scaleView = getChildAt(position + 1);
                    shrinkView = getChildAt(position);
                    scaleRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * ratio;
                    ;
                    shrinkRatio = 1.0f - (1.0f - SCALE_RATIO) * ratio;
                }
            }
        } else if (distance < 0) {
            float moveSize = position * mSwitchSize - getScrollX();
            float ratio = moveSize / mSwitchSize;

            if (direction == LEFT_TO_RIGHT) {
                scaleView = getChildAt(position - 1);
                shrinkView = getChildAt(position);
                scaleRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * ratio;
                shrinkRatio = 1.0f - (1.0f - SCALE_RATIO) * ratio;
            } else if (direction == RIGHT_TO_LEFT) {
                scaleView = getChildAt(position);
                shrinkView = getChildAt(position - 1);
                shrinkRatio = SCALE_RATIO + (1.0f - SCALE_RATIO) * ratio;
                scaleRatio = 1.0f - (1.0f - SCALE_RATIO) * ratio;
            }
        }
        if (scaleView != null) {
            ViewCompat.setScaleX(scaleView, scaleRatio);
            ViewCompat.setScaleY(scaleView, scaleRatio);
            scaleView.invalidate();
        }
        if (shrinkView != null) {
            ViewCompat.setScaleX(shrinkView, shrinkRatio);
            ViewCompat.setScaleY(shrinkView, shrinkRatio);
            shrinkView.invalidate();
        }
    }

    /**
     * set current page change listener
     * @param onPageChangListener
     * @see com.github.songnick.viewgroup.SlideViewPager.OnPagerChangeListener
     * */
    public void setOnPageChangListener(OnPagerChangeListener onPageChangListener){

        mOnPagerChangeListener = onPageChangListener;

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int originLeft = (int) mMarginLeftRight;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int left = originLeft + child.getMeasuredWidth() * i;
            int right = originLeft + child.getMeasuredWidth() * (i + 1);
            int bottom = child.getMeasuredHeight();
            child.layout(left, 0, right, bottom);
            if (i != 0) {
                child.setScaleX(SCALE_RATIO);
                child.setScaleY(SCALE_RATIO);
                child.setTag(SCALE_RATIO);
            } else {
                child.setTag(1.0f);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, our internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view.  We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int childCount = getChildCount();
        int width = measuredWidth - (int) (mMarginLeftRight * 2);
        int height = measuredHeight;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
        mSwitchSize = width;
        confirmScaleRatio(width, mGutterSize);
    }

    private void confirmScaleRatio(int width, float gutterSize) {
        SCALE_RATIO = (width - gutterSize * 2) / width;
        LogUtils.LogD(TAG, " confirm scale ratio == " + gutterSize + " ration ==  " + SCALE_RATIO + " margin lef t == " + mMarginLeftRight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        LogUtils.LogD(TAG, " onSize changed com ");

        super.onSizeChanged(w, h, oldw, oldh);
    }
}
