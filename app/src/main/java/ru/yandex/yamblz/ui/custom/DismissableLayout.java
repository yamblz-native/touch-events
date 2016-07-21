package ru.yandex.yamblz.ui.custom;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * A layout which implements swipe behavior. Also allows vertical scrolling.
 */
public class DismissableLayout extends FrameLayout {

    //Scroll direction horizontal axis
    private static final int HORIZONTAL = 0;
    //Scroll direction vertical axis
    private static final int VERTICAL = 1;
    //No scroll
    private static final int NO = 2;

    private static final float RETURN_DURATION = 300f;

    private static final float ANGLE_CONSTRAINT = -90f;

    //Touch slop from ViewConfiguration
    private int mTouchSlop;
    //Last touch coordinates (not all, but the only we are interested in)
    private float mLastX, mLastY;
    //Dragging direction axis
    private int mDragMainAxis;

    private ArgbEvaluator mEvaluator;
    private ValueAnimator mValueAnimator;

    private Integer mEndColor;
    private Integer mStartColor;

    public DismissableLayout(Context context) {
        super(context);
        init(context);
    }

    public DismissableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DismissableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mEndColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
        mEvaluator = new ArgbEvaluator();
        mValueAnimator = ValueAnimator.ofObject(mEvaluator);
        mValueAnimator.addUpdateListener((ValueAnimator animation) ->
                setBackgroundColor((Integer)animation.getAnimatedValue()));
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean exceedsTouchSlop(float diff) {
        return Math.abs(diff) > mTouchSlop;
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    /**
     * Returns children max height
     * @return max height
     */
    private int getChildMaxHeight() {
        final int childCount = getChildCount();
        int maxHeight = 0;
        for(int i = 0; i < childCount; i++) {
            maxHeight = Math.max(getChildAt(i).getHeight(), maxHeight);
        }
        return maxHeight;
    }

    @Override
    public void scrollBy(int x, int y) {
        final int maxHeight = getChildMaxHeight();

        int scrollY;
        //Need this if so not to go out of layout boundaries
        if(y < 0) {
            scrollY = Math.max(-getScrollY(), y);
        } else {
            scrollY = Math.min(maxHeight - getHeight() - getScrollY(), y);
            scrollY = Math.max(0, scrollY); //if {@code maxHeight < getHeight()} the result would be negative
        }

        super.scrollBy(x, scrollY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "ON TOUCH " + event.getAction());
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                final float x = event.getRawX(); //getX() not working because we move container, so center moves with it. if layout was fixed it would be nice
                final float y = event.getRawY();
                final float diffX = x - mLastX;
                final float diffY = y - mLastY;
                final boolean exceedsX = exceedsTouchSlop(diffX);
                final boolean exceedsY = exceedsTouchSlop(diffY);

                if(mDragMainAxis == NO) {
                    if(exceedsX && exceedsY) {
                        break;
                    }
                    if(exceedsX) {
                        mDragMainAxis = HORIZONTAL;
                    } else if(exceedsY) {
                        mDragMainAxis = VERTICAL;
                    }
                }
                if(mDragMainAxis != NO) {
                    mLastX = x;
                    mLastY = y;
                    if(mDragMainAxis == HORIZONTAL) {
                        final float translationX = getTranslationX() + diffX;
                        final float rotation = getRotation() + 0.2f * diffX / getWidth() * 180;
                        if(translationX <= 0 && rotation >= ANGLE_CONSTRAINT) {
                            setTranslationX(translationX);
                            setRotation(rotation);
                            if(mStartColor != null) {
                                setBackgroundColor((Integer)mEvaluator.evaluate(rotation / ANGLE_CONSTRAINT, mStartColor, mEndColor));
                            }
                        }
                    } else {
                        scrollBy(0, (int)-diffY);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragMainAxis = NO;
                returnToStartPosition();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Returns layout to source position
     */
    private void returnToStartPosition() {
        final long duration = (long)(getRotation() / ANGLE_CONSTRAINT * RETURN_DURATION);
        animate().rotation(0).setDuration(duration).start();
        animate().translationX(0).setDuration(duration).start();
        if(mStartColor != null) {
            mValueAnimator.setIntValues(getBackgroundColor(), mStartColor);
            mValueAnimator.setDuration(duration);
            mValueAnimator.start();
        }
    }

    @Nullable private Integer getBackgroundColor() {
        Drawable background = getBackground();
        Integer color = null;
        if(background instanceof ColorDrawable) {
            color = ((ColorDrawable) background).getColor();
        }
        return color;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("TAG", "ON INTERCEPT " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                mStartColor = getBackgroundColor();
                return false;
            case MotionEvent.ACTION_MOVE:
                float diffX = ev.getRawX() - mLastX;
                float diffY = ev.getRawY() - mLastY;
                final boolean exceedsX = exceedsTouchSlop(diffX);
                final boolean exceedsY = exceedsTouchSlop(diffY);
                if(exceedsX && exceedsY) {
                    break;
                }
                if(exceedsX) {
                    mDragMainAxis = HORIZONTAL;
                    return true;
                } else if(exceedsY) {
                    mDragMainAxis = VERTICAL;
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);

    }
}
