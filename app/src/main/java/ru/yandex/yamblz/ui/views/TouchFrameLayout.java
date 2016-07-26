package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import ru.yandex.yamblz.R;

public class TouchFrameLayout extends FrameLayout {
    private GestureDetector detector;
    private View dismissible;

    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(context, new GestureListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dismissible == null) {
            dismissible = findViewById(R.id.dismissible);
        }

        return detector.onTouchEvent(event);
    }


    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            float left = dismissible.getX();
            float top = dismissible.getY();
            float right = left + dismissible.getWidth();
            float bottom = top + dismissible.getHeight();

            return left < x && x < right && top < y && y < bottom;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            dismissible.setX(dismissible.getX() - distanceX);
            dismissible.setY(dismissible.getY() - distanceY);
            return true;
        }
    }
}
