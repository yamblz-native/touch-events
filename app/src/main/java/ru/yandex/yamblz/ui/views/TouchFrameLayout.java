package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import ru.yandex.yamblz.R;

import static android.view.MotionEvent.ACTION_UP;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.DISMISS;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.NONE;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.SCROLL;

public class TouchFrameLayout extends FrameLayout {
    private static OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2);
    private static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(0.8f);
    private static float density;
    private static float screenSize;

    static {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        density = metrics.density;
        screenSize = Math.max(metrics.widthPixels, metrics.heightPixels);
    }

    private GestureDetector detector;
    private GestureListener listener;
    private Scroller scroller;
    private Action action;
    private int maxScroll;
    private float xBase;
    private float dismissThreshold;

    @BindView(R.id.dismissible) View dismissible;
    @BindView(R.id.scrollable) TextView scrollable;

    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        listener = new GestureListener();
        detector = new GestureDetector(context, listener);
        scroller = new Scroller(context, decelerateInterpolator);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (action == null) init();

        if (event.getAction() == ACTION_UP) {
            listener.onUp();
        }

        return detector.onTouchEvent(event);
    }


    private void init() {
        maxScroll = scrollable.getLineCount() * scrollable.getLineHeight() - scrollable.getHeight();
        scrollable.setScroller(scroller);

        xBase = dismissible.getX();
        dismissThreshold = dismissible.getWidth() / 2.5f;
        dismissible.setPivotY(dismissible.getHeight() / 1.3f);
    }


    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            action = NONE;

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
            switch (action) {

                case NONE: {
                    float dx = Math.abs(distanceX);
                    float dy = Math.abs(distanceY);
                    action = (dx > dy) ? DISMISS : SCROLL;
                    break;
                }

                case DISMISS: {
                    float xNew = dismissible.getX() - distanceX;
                    dismissible.setX(xNew);
                    dismissible.setRotation(getAngle(xBase - xNew));

                    break;
                }

                case SCROLL: {
                    scrollable.scrollBy(0, getDy((int) distanceY));
                    break;
                }
            }

            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            switch (action) {

                case DISMISS:
                    float velocity = Math.abs(velocityX / density);
                    if (velocity > 500) {
                        animateDismiss(velocityX < 0, velocity);
                    }
                    break;

                case SCROLL:
                    int dy = getDy((int) -velocityY / 7);
                    scroller.startScroll(0, scrollable.getScrollY(), 0, dy, 300);
                    scrollable.invalidate();
                    break;
            }

            return true;
        }


        /**
         * Starts 'dismiss' or 'return' animation of the view.
         * Note that if there is enough velocity in onFling() callback,
         * animation started here will be canceled and a faster one will be started.
         */
        public void onUp() {
            if (action != DISMISS) return;

            float offset = xBase - dismissible.getX();

            if (Math.abs(offset) < dismissThreshold) {
                animateReturn();
            } else {
                animateDismiss(offset > 0, 0);
            }
        }


        private void animateReturn() {
            dismissible.animate().x(xBase).rotation(0).setInterpolator(overshootInterpolator).start();
        }


        private void animateDismiss(boolean leftward, float velocity) {
            Interpolator interpolator = (velocity > 0) ? decelerateInterpolator : accelerateInterpolator;
            float destination = xBase + (dismissible.getWidth() + screenSize * 0.5f) * (leftward ? -1 : 1);
            long duration = Math.max(300, 600 - (long) velocity / 13);

            dismissible.animate().cancel();

            dismissible.animate()
                    .x(destination)
                    .rotation(getAngle(xBase - destination))
                    .setInterpolator(interpolator)
                    .setDuration(duration)
                    .start();
        }


        private int getDy(int distance) {
            int scroll = scrollable.getScrollY();
            if (distance < 0) {
                return (scroll + distance < 0) ? -scroll : distance;
            } else {
                return (scroll + distance > maxScroll) ? maxScroll - scroll : distance;
            }
        }


        private float getAngle(float offset) {
            return -offset / 25;
        }
    }


    enum Action {
        NONE,
        DISMISS,
        SCROLL
    }
}
