package ru.yandex.yamblz.ui.custom;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
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

    //Max duration of the return animation
    private static final float RETURN_DURATION = 300f;

    //How much can the layout be rotated
    private static final float ANGLE_CONSTRAINT = -90f;

    //How fast the view will be rotated, the more the faster
    private static final float ROTATION_MULTIPLE = 0.3f;

    //Touch slop from ViewConfiguration
    private int mTouchSlop;
    //Last touch coordinates (not all, but the only we are interested in)
    private float mLastX, mLastY;
    //Dragging direction axis
    private int mDragMainAxis;

    private ArgbEvaluator mEvaluator;
    private ValueAnimator mValueAnimator;

    //The color at the end of the gesture
    private Integer mEndColor;

    /**
     * The original color of the view, can be {@code null} if an image is set for background.
     * In that case no color animation will be shown.
     */
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
        mValueAnimator = ObjectAnimator.ofInt(this, "backgroundColor", 0);
        mValueAnimator.setEvaluator(mEvaluator);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * Checks whether diff exceeds TouchSlop
     * @param diff value to check
     * @return {@code true} if exceeds
     */
    private boolean exceedsTouchSlop(float diff) {
        return Math.abs(diff) > mTouchSlop;
    }

    //Make child's height as mush as it wants to have
    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    //Make child's height as mush as it wants to have
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

    /**
     * Changes background color depending on the rotation (forward animation)
     */
    private void changeBackgroundOnForwardAnimation() {
        if(mStartColor != null) {
            setBackgroundColor((Integer)mEvaluator.evaluate(getRotation() / ANGLE_CONSTRAINT,
                    mStartColor, mEndColor));
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        final int maxHeight = getChildMaxHeight();
        int scrollY;
        int paddings = getPaddingBottom() + getPaddingTop();
        //Need this so not to go out of layout boundaries
        if(y < 0) {
            scrollY = Math.max(-getScrollY(), y);
        } else {
            scrollY = Math.min(maxHeight - getHeight() - getScrollY() + paddings, y);
            scrollY = Math.max(0, scrollY); //if {@code maxHeight < getHeight()} the result would be negative
        }

        super.scrollBy(x, scrollY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //if down returned to us from children, then we need to handle, so not to lose it
                return true;
            case MotionEvent.ACTION_MOVE:
                //getX() not working because we move container, so center moves with it. if layout was fixed it would be nice
                final float x = event.getRawX();
                final float y = event.getRawY();
                final float diffX = x - mLastX;
                final float diffY = y - mLastY;
                final boolean exceedsX = exceedsTouchSlop(diffX);
                final boolean exceedsY = exceedsTouchSlop(diffY);

                //if not scrolling
                if(mDragMainAxis == NO) {
                    //do not know what to do in this case, ignore it
                    if(exceedsX && exceedsY) {
                        break;
                    }
                    //choose needed direction
                    if(exceedsX) {
                        mDragMainAxis = HORIZONTAL;
                    } else if(exceedsY) {
                        mDragMainAxis = VERTICAL;
                    }
                }
                //if scrolling
                if(mDragMainAxis != NO) {
                    mLastX = x;
                    mLastY = y;
                    if(mDragMainAxis == HORIZONTAL) {
                        //the translation after moving
                        final float translationX = getTranslationX() + diffX;
                        //the angle in degrees to rotate on
                        final float rotation = getRotation() + ROTATION_MULTIPLE * (diffX / getWidth()) * 180;
                        if(translationX <= 0 && rotation >= ANGLE_CONSTRAINT) {
                            setTranslationX(translationX);
                            setRotation(rotation);
                            changeBackgroundOnForwardAnimation();
                        }
                    } else {
                        //otherwise scroll vertically
                        scrollBy(0, (int)-diffY);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //run reverse animation
                mDragMainAxis = NO;
                returnToStartPosition();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Calculates duration of the reverse animation using {@link #getRotation()}
     * @return the duration
     */
    private long calculateDurationOfReverseAnimation() {
        return (long)(getRotation() / ANGLE_CONSTRAINT * RETURN_DURATION);
    }

    /**
     * Returns layout to source position
     */
    private void returnToStartPosition() {
        final long duration = calculateDurationOfReverseAnimation();
        animate().rotation(0).setDuration(duration).start();
        animate().translationX(0).setDuration(duration).start();
        if(mStartColor != null) {
            mValueAnimator.setIntValues(getBackgroundColor(), mStartColor);
            mValueAnimator.setDuration(duration).start();
        }
    }

    /**
     * Returns background color
     * @return background color, or {@code null} if an image is set
     */
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
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //if down we need to remember coordinates to detect scroll
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                //we need to remember color to animate changing of background
                mStartColor = getBackgroundColor();
                //don't need to intercept touch on down, because the children may need it
                return false;
            case MotionEvent.ACTION_MOVE:
                final float diffX = ev.getRawX() - mLastX;
                final float diffY = ev.getRawY() - mLastY;
                final boolean exceedsX = exceedsTouchSlop(diffX);
                final boolean exceedsY = exceedsTouchSlop(diffY);

                //if both directions exceeded then we do not know what to do
                if(exceedsX && exceedsY) {
                    break;
                }
                //otherwise we intercept the touch
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
