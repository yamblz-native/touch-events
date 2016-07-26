package ru.yandex.yamblz.ui.other;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by user on 25.07.16.
 */

public class OnSwipeTouchListener implements View.OnTouchListener {

    private GestureDetectorCompat detector;
    private int flagLockScroll;

    protected OnSwipeTouchListener(Context context) {
        detector = new GestureDetectorCompat(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                flagLockScroll = 0;
                onReturnFirstState();
                return true;
        }
        return detector.onTouchEvent(event);
    }

    public void onVerticalScroll(float y1, float y2) {
    }

    public void onHorisontalScroll(float x1, float x2) {
    }

    public void onReturnFirstState() {
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SLIDE_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float deltaY = e2.getY() - e1.getY();
            float deltaX = e2.getX() - e1.getX();

            if (flagLockScroll == 0) {
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > SLIDE_THRESHOLD) {
                        flagLockScroll = 1;
                        onHorisontalScroll(e1.getX(), e2.getX());
                    }
                } else {
                    if (Math.abs(deltaY) > SLIDE_THRESHOLD) {
                        flagLockScroll = 2;
                        onVerticalScroll(e1.getY(), e2.getY());
                    }
                }
            } else if (flagLockScroll == 1) {
                onHorisontalScroll(e1.getX(), e2.getX());
            } else if (flagLockScroll == 2) {
                onVerticalScroll(e1.getY(), e2.getY());
            }
            return true;
        }
    }
}