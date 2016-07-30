package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import ru.yandex.yamblz.R;

/**
 * Created by dalexiv on 7/28/16.
 */

public class SwipeableCard extends CardView {
    private static final String TAG = SwipeableCard.class.getSimpleName();
    private GestureDetectorCompat gd = new GestureDetectorCompat(getContext(), new FlingGestureListener());

    public SwipeableCard(Context context) {
        super(context);

    }

    public SwipeableCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        final ImageView child = new ImageView(getContext());
        child.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.anwerrr));
        addView(child);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        Log.d(TAG, ev.toString());
        // Handle scroll
        gd.onTouchEvent(ev);

        // Handle ACTION_UP
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (Math.abs(getRotation()) < 30) {
                Log.d(TAG, "Current rotation is " + getRotation());
                animate().rotation(0)
                        .translationX(0)
                        .setStartDelay(100)
                        .setDuration(100);
            } else {
                float translationToGo = getTranslationX() > 0
                        ? getHeight() : -getHeight();
                animate().rotation(90)
                        .translationX(translationToGo)
                        .alpha(0)
                        .setStartDelay(100)
                        .setDuration(100);
            }
        }

        return true;
    }

    private class FlingGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final float delta = e2.getX() - e1.getX();
            animate().translationXBy(delta)
                    .rotationBy(delta / 10)
                    .setStartDelay(0)
                    .setDuration(0);

            return true;
        }


    }
}
