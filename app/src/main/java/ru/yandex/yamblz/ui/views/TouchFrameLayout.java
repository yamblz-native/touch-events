package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import ru.yandex.yamblz.R;

import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.DISMISS;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.NONE;
import static ru.yandex.yamblz.ui.views.TouchFrameLayout.Action.SCROLL;

public class TouchFrameLayout extends FrameLayout {
    private GestureDetector detector;
    private Scroller scroller;
    private Action action;
    private int maxScroll;
    private float xBase;

    @BindView(R.id.dismissible) View dismissible;
    @BindView(R.id.scrollable) TextView scrollable;

    public TouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(context, new GestureListener());
        scroller = new Scroller(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (action == null) init();
        return detector.onTouchEvent(event);
    }


    private void init() {
        maxScroll = scrollable.getLineCount() * scrollable.getLineHeight() - scrollable.getHeight();

        scrollable.setScroller(scroller);

        xBase = dismissible.getX();

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

                    float offset = xBase - xNew;
                    float degrees = -offset / 25;
                    dismissible.setRotation(degrees);

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
            if (action != SCROLL) return false;

            int dy = getDy((int) -velocityY / 10);
            scroller.startScroll(0, scrollable.getScrollY(), 0, dy, 300);
            scrollable.invalidate();

            return true;
        }


        private int getDy(int distance) {
            int scroll = scrollable.getScrollY();
            if (distance < 0) {
                return (scroll + distance < 0) ? -scroll : distance;
            } else {
                return (scroll + distance > maxScroll) ? maxScroll - scroll : distance;
            }
        }
    }


    enum Action {
        NONE,
        DISMISS,
        SCROLL
    }
}
