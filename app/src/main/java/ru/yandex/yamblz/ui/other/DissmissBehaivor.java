package ru.yandex.yamblz.ui.other;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Created by Александр on 26.07.2016.
 */

public class DissmissBehaivor extends CoordinatorLayout.Behavior {
    /**
     * Swiping threshold is added for neglecting swiping
     * when differences between changed x or y coordinates are too small
     */
    public final static int SWIPING_THRESHOLD = 50;

    private View bottomView;
    private View surfaceView;

    private float xInitSurface, xInitBottom = -1;
    private float distanceScale = -1;
    private float xDown, xMove, xUp, yDown, yMove, yUp;
    private float xSwipedThreshold = -1;

    public DissmissBehaivor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        bottomView = dependency;
        surfaceView = child;
        xInitSurface = (xInitSurface == -1) ? surfaceView.getX() : xInitSurface;
        xInitBottom = (xInitBottom == -1) ? bottomView.getX(): xInitBottom;
        distanceScale = (distanceScale == -1) ? surfaceView.getWidth() / bottomView.getWidth() : distanceScale ;
        xSwipedThreshold = (xSwipedThreshold == -1) ? surfaceView.getWidth() / 2: xSwipedThreshold;
        return true;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        return dispatchTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        return dispatchTouchEvent(parent, child, ev);
    }

    /**
     * Called to process touch screen events.
     * @param event MotionEvent
     */
    public boolean dispatchTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // user started touching the screen
                return onActionDown(event);
            case MotionEvent.ACTION_UP:   // user stopped touching the screen
                return onActionUp(parent, child, event);
            case MotionEvent.ACTION_MOVE: // user is moving finger on the screen
                return onActionMove(parent, child, event);
            default:
                return false;
        }

    }

    private boolean onActionDown(final MotionEvent event) {
        xDown = event.getX();
        yDown = event.getY();
        return true;
    }

    private boolean onActionUp(CoordinatorLayout parent, View child, MotionEvent event) {
        xUp = event.getX();
        yUp = event.getY();
        if(Math.abs(xUp - xDown) < Math.abs(yUp - yDown)){
            return false;
        }
        final boolean swipedHorizontally = Math.abs(xUp - xDown) > SWIPING_THRESHOLD;
        if (swipedHorizontally) {
            final boolean swipedLeft = xUp < xDown;
            if (swipedLeft) {
                if(Math.abs(xUp - xDown) > xSwipedThreshold){
                    Timber.d("swiped");
                    outAnim(parent, event);
                }else {
                    Timber.d("Not swiped");
                    returnToInitState();
                }
            }
            return true;
        }
        return false;
    }

    private boolean onActionMove(CoordinatorLayout parent, View child, MotionEvent event) {
        xMove = event.getX();
        yMove = event.getY();
        if(Math.abs(xMove - xDown) < Math.abs(yMove - yDown)){
            return false;
        }
        final boolean isSwipingHorizontally = Math.abs(xMove - xDown) > SWIPING_THRESHOLD;

        if (isSwipingHorizontally) {
            final boolean isSwipingLeft = xMove < xDown;
            if (isSwipingLeft) {
                moveOn(xDown - xMove);
                return true;
            }
        }
        return false;
    }

    private void moveOn(float distance){
        surfaceView.setX(xInitSurface - distance);
        surfaceView.setRotation(getRotation(distance));
        bottomView.setAlpha(distance / xSwipedThreshold);
        bottomView.setX(xInitBottom + computeBottomViewDistance(distance / distanceScale));
    }

    private float computeBottomViewDistance(float distance){
        float newDis = distance / distanceScale;
        return (float) (0.1 * (newDis * newDis) - 2.9 * newDis + 7.9);
    }

    private void returnToInitState(){
        surfaceView.animate()
                .xBy(xInitSurface - surfaceView.getX())
                .rotation(0)
                .setDuration(500)
                .start();
        bottomView.animate()
                .xBy(xInitBottom - bottomView.getX())
                .setDuration(500)
                .start();
    }

    private void outAnim(CoordinatorLayout parent, MotionEvent event){
        surfaceView.animate()
                .xBy(-surfaceView.getWidth())
                .alpha(0)
                .setDuration(500)
                .start();
        bottomView.animate()
                .xBy(bottomView.getWidth())
                .alpha(0)
                .setDuration(500)
                .start();
    }

    private float getRotation(float distance){
        return -(10) * (distance / xSwipedThreshold);
    }

}
