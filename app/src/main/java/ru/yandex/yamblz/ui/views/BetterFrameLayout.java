package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class BetterFrameLayout extends FrameLayout {
    private float x0, x;

    public BetterFrameLayout(Context context) {
        super(context);
    }

    public BetterFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BetterFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = event.getX();
                return false;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                return Math.abs(x - x0) > 20;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return false;
    }

    public float getX0() {
        return x0;
    }
}