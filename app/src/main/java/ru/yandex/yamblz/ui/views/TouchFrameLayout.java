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
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;

import static android.view.MotionEvent.ACTION_UP;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.DISMISS;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.INIT;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.REMOVED;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.SCROLL;

public class TouchFrameLayout extends FrameLayout {
    private static BounceInterpolator bounceInterpolator = new BounceInterpolator();
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
    private State state;
    private int maxScroll;
    private float xBaseRepeat;
    private float xBase;
    private float yBase;
    private float dismissThreshold;

    @BindView(R.id.repeat) View repeat;
    @BindView(R.id.dismissible) View dismissible;
    @BindView(R.id.scrollable) TextView scrollable;

    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        listener = new GestureListener();
        detector = new GestureDetector(context, listener);
        scroller = new Scroller(context, decelerateInterpolator);
    }


    /**
     * 'Enables' Repeat-button callback only when the card is removed.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return state != REMOVED;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (state == null) init();

        if (event.getAction() == ACTION_UP) {
            listener.onUp();
        }

        return detector.onTouchEvent(event);
    }


    @OnClick(R.id.repeat)
    void repeat() {
        listener.animateReappearance();
    }


    private void init() {
        maxScroll = scrollable.getLineCount() * scrollable.getLineHeight() - scrollable.getHeight();
        scrollable.setScroller(scroller);

        xBaseRepeat = repeat.getX();

        xBase = dismissible.getX();
        yBase = dismissible.getY();
        dismissThreshold = dismissible.getWidth() / 2.5f;
        dismissible.setPivotY(dismissible.getHeight() / 1.3f);
    }


    private class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            if (state == REMOVED) {
                return false;
            }

            state = INIT;

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
            switch (state) {

                case INIT: {
                    float dx = Math.abs(distanceX);
                    float dy = Math.abs(distanceY);
                    state = (dx > dy) ? DISMISS : SCROLL;
                    break;
                }

                case DISMISS: {
                    float xNew = dismissible.getX() - distanceX;
                    dismissible.setX(xNew);

                    float offset = xBase - xNew;
                    dismissible.setRotation(getAngle(offset));

                    repeat.setX(getRepeatX(offset));
                    repeat.setAlpha(getRepeatAlpha(offset));

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
            switch (state) {

                case DISMISS:
                case REMOVED:
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
            if (state != DISMISS) return;

            float offset = xBase - dismissible.getX();

            if (Math.abs(offset) < dismissThreshold) {
                animateReturn();
            } else {
                animateDismiss(offset > 0, 0);
            }
        }


        void animateReappearance() {
            state = INIT;

            dismissible.animate().cancel();

            dismissible.setX(xBase);
            dismissible.setY(-screenSize / 2);
            dismissible.setRotation(0);
            dismissible.animate()
                    .y(yBase)
                    .setInterpolator(bounceInterpolator)
                    .setDuration(1000)
                    .start();

            repeat.animate().alpha(0).start();
        }


        private void animateReturn() {
            dismissible.animate()
                    .x(xBase)
                    .rotation(0)
                    .setDuration(200)
                    .setInterpolator(overshootInterpolator)
                    .start();
        }


        private void animateDismiss(boolean leftward, float velocity) {
            state = REMOVED;

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

            repeat.animate().x(xBaseRepeat).alpha(1).start();
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


        private float getRepeatX(float cardOffset) {
            float offset = Math.abs(cardOffset);
            float width = repeat.getWidth() / 1.5f;
            if (offset < width) {
                return xBaseRepeat + cardOffset;
            } else {
                if (cardOffset < 0) {
                    return Math.min(xBaseRepeat, xBaseRepeat - width + (offset - width) * 0.4f);
                } else {
                    return Math.max(xBaseRepeat, xBaseRepeat + width - (offset - width) * 0.4f);
                }
            }
        }


        private float getRepeatAlpha(float cardOffset) {
            float width = repeat.getWidth();
            float offset = Math.abs(cardOffset) - width / 3;
            return Math.min(offset / width, 1);
        }
    }


    enum State {
        INIT,
        DISMISS,
        SCROLL,
        REMOVED
    }
}
