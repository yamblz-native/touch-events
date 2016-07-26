package ru.yandex.yamblz.util;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 *
 * This class helps you to detect different gestures like swipe, flint, scroll, etc.
 * Only set it as onTouchListener to your view that should detect gestures and override callbacks.
 *
 * Created by root on 7/26/16.
 */
public class OnSwipeListener implements View.OnTouchListener {

    private GestureDetector detector;

    /**
     * This field shows which scroll should be handle
     * @value null - unknown
     * @value true - scroll X
     * @value false - scroll Y
     *
     */

    private Boolean scrollX;

    public OnSwipeListener(Context context) {
        detector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                scrollX = null;
                onUp(event.getX(), event.getY());
                return true;
        }

        return detector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    onSwipeLeft();
                    return true;
                }
            }

            return false;
        }

        /**
         * This method call only one scroll (x or y)
         * While the finger is down it scroll only one coordinate
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * @return
         */

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(scrollX == null) {
                if (Math.abs(e2.getX() - e1.getX()) > Math.abs(e2.getY() - e1.getY())) {
                    scrollX = true;
                    onScrollX(e1.getX(), e2.getX());
                } else {
                    scrollX = false;
                    onScrollY(e1.getY(), e2.getY());
                }
            } else {
                if(scrollX) {
                    onScrollX(e1.getX(), e2.getX());
                } else {
                    onScrollY(e1.getY(), e2.getY());
                }
            }
            return true;
        }
    }

    public void onScrollY(float y1, float y2) {
    }

    public void onScrollX(float x1, float x2) {

    }

    public void onUp(float x, float y) {

    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

}
