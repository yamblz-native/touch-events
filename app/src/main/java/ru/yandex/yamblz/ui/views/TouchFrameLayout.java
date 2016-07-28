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
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.SWIPE;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.IDLE;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.State.DISMISSED;
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

    private GestureListener listener;
    private GestureDetector detector;
    private Scroller scroller;

    private State state;

    // Constant values dependent on the views parameters
    private float xRenewButton;
    private float xDismissible;
    private float yDismissible;

    @BindView(R.id.renew) View renewButton;
    @BindView(R.id.dismissible) View dismissible;
    @BindView(R.id.scrollable) TextView scrollable;


    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        listener = new GestureListener();
        detector = new GestureDetector(context, listener);
        scroller = new Scroller(context, decelerateInterpolator);
    }


    /**
     * Enables callback of the renew button only when the card is dismissed
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return state != DISMISSED;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (state == null) init();

        if (event.getAction() == ACTION_UP) {
            listener.onUp();
        }

        return detector.onTouchEvent(event);
    }


    @OnClick(R.id.renew)
    void renew() {
        listener.renewCard();
    }


    private void init() {
        state = IDLE;

        xRenewButton = renewButton.getX();
        xDismissible = dismissible.getX();
        yDismissible = dismissible.getY();

        dismissible.setPivotY(dismissible.getHeight() / 1.3f);
        scrollable.setScroller(scroller);
    }


    private class GestureListener extends SimpleOnGestureListener {

        /**
         * Resets state (because of a new tap).
         *
         * @return true if a user hit the card, thus subscribing
         * to the following touch events, false otherwise.
         */
        @Override
        public boolean onDown(MotionEvent event) {
            if (state == DISMISSED) {
                return false;
            }

            state = IDLE;

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

                case IDLE: {
                    float dx = Math.abs(distanceX);
                    float dy = Math.abs(distanceY);
                    state = (dx > dy) ? SWIPE : SCROLL;
                    break;
                }

                case SWIPE: {
                    float xNew = dismissible.getX() - distanceX;
                    dismissible.setX(xNew);

                    float offset = xDismissible - xNew;
                    dismissible.setRotation(getDismissibleAngle(offset));

                    renewButton.setX(getRenewButtonX(offset));
                    renewButton.setAlpha(getRenewButtonAlpha(offset));

                    break;
                }

                case SCROLL: {
                    scrollable.scrollBy(0, getScrollableDy((int) distanceY));
                    break;
                }
            }

            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            switch (state) {

                case SWIPE:
                case DISMISSED:
                    float velocity = Math.abs(velocityX / density);
                    if (velocity > 500) {
                        dismissCard(velocityX < 0, velocity);
                    }
                    break;

                case SCROLL:
                    int dy = getScrollableDy((int) -velocityY / 7);
                    scroller.startScroll(0, scrollable.getScrollY(), 0, dy, 300);
                    scrollable.invalidate();
                    break;
            }

            return true;
        }


        /**
         * Starts 'dismiss' or 'return' animation of the card.
         * Note that if there is enough velocity in onFling() callback,
         * animation started here will be canceled and a faster one will be started.
         */
        void onUp() {
            if (state != SWIPE) return;

            float offset = xDismissible - dismissible.getX();
            float dismissThreshold = dismissible.getWidth() / 2.5f;

            if (Math.abs(offset) < dismissThreshold) {
                returnCard();
            } else {
                dismissCard(offset > 0, 0);
            }
        }


        void renewCard() {
            state = IDLE;

            dismissible.animate().cancel();

            dismissible.setX(xDismissible);
            dismissible.setY(-screenSize / 2);
            dismissible.setRotation(0);
            dismissible.animate()
                    .y(yDismissible)
                    .setInterpolator(bounceInterpolator)
                    .setDuration(1000)
                    .start();

            renewButton.setRotation(0);
            renewButton.animate().alpha(0).rotation(180).start();
        }


        private void returnCard() {
            dismissible.animate()
                    .x(xDismissible)
                    .rotation(0)
                    .setDuration(200)
                    .setInterpolator(overshootInterpolator)
                    .start();
        }


        private void dismissCard(boolean leftward, float velocity) {
            state = DISMISSED;

            Interpolator interpolator = (velocity > 0) ? decelerateInterpolator : accelerateInterpolator;
            float destination = xDismissible + (dismissible.getWidth() + screenSize * 0.5f) * (leftward ? -1 : 1);
            long duration = Math.max(300, 600 - (long) velocity / 13);

            dismissible.animate().cancel();

            dismissible.animate()
                    .x(destination)
                    .rotation(getDismissibleAngle(xDismissible - destination))
                    .setInterpolator(interpolator)
                    .setDuration(duration)
                    .start();

            renewButton.animate().x(xRenewButton).alpha(1).start();
        }


        /**
         * Prevents upward scrolling when the top is already reached.
         * Similarly for the bottom.
         */
        private int getScrollableDy(int distance) {
            int scroll = scrollable.getScrollY();
            if (distance < 0) {
                return (scroll + distance < 0) ? -scroll : distance;
            } else {
                int maxScroll = scrollable.getLineCount() * scrollable.getLineHeight() - scrollable.getHeight();
                return (scroll + distance > maxScroll) ? maxScroll - scroll : distance;
            }
        }


        private float getDismissibleAngle(float offset) {
            return -offset / 25;
        }


        private float getRenewButtonX(float cardOffset) {
            float offset = Math.abs(cardOffset);
            float width = renewButton.getWidth() / 1.5f;
            if (offset < width) {
                return xRenewButton + cardOffset;
            } else {
                float delta = (offset - width) * 0.4f;
                if (cardOffset < 0) {
                    return Math.min(xRenewButton, xRenewButton - width + delta);
                } else {
                    return Math.max(xRenewButton, xRenewButton + width - delta);
                }
            }
        }


        private float getRenewButtonAlpha(float cardOffset) {
            float width = renewButton.getWidth();
            float offset = Math.abs(cardOffset) - (width / 3);
            return Math.min(offset / width, 1);
        }
    }


    enum State {
        IDLE,
        SWIPE,
        SCROLL,
        DISMISSED
    }
}
