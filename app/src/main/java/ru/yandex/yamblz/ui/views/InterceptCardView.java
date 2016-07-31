package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

/**
 * Created by aleien on 30.07.16.
 */

public class InterceptCardView extends CardView implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    private float xStartPosition, yStartPosition;

    public InterceptCardView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    public InterceptCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xStartPosition = event.getRawX();
                yStartPosition = event.getRawY();
                Timber.d("Action DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                float distanceX = xStartPosition - event.getRawX();
                float distanceY = yStartPosition - event.getRawY();
                Timber.d("INTERCEPTOR: DistanceX: " + distanceX + ", distanceY: " + distanceY);
                return Math.abs(distanceY) < Math.abs(distanceX);

        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xStartPosition = event.getRawX();
                yStartPosition = event.getRawY();
                Timber.d("Action DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = xStartPosition - event.getRawX();
                float distanceY = yStartPosition - event.getRawY();
                Timber.d("DistanceX: " + distanceX + ", distanceY: " + distanceY);
                return Math.abs(distanceY) > Math.abs(distanceX);

        }
        return true;
    }

    // Return false if we're scrolling in the x direction
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Timber.d("Scrolling x: " + distanceX + ", scrollingY: " + distanceY);
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }
}
