package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import ru.yandex.yamblz.R;

public class MailCardLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private Scroller scroller;

    @BindView(R.id.email_card)
    CardView emailCardView;
    @BindView(R.id.email_text)
    TextView emailTextView;

    public MailCardLayout(Context context) {
        this(context, null, 0);
    }

    public MailCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MailCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, new MainCardLayoutGestureListener());
        scroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        emailTextView.setScroller(scroller);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    /**
     * <p>Scroll and animate emailTextView</p>
     * <p>
     * 13 and 900 - it's constant for enjoy slow scrolling, change it if you need
     *
     * @param value Distance for Scrolling or Velocity in Flinging mode
     * @param mode  false for Scrolling or true for Flinging
     */
    private void scrollEmailTextView(float value, boolean mode) {
        int startY = emailTextView.getScrollY();
        int dy = mode ? Math.round(-value / 13) : Math.round(value);

        if (dy < 0) { //Upper scroll, check top bound
            //Check top bound
            dy = (startY + dy < 0) ? -startY : dy;
        } else { //Down scroll, check bottom bound
            int maxScroll = emailTextView.getLineCount() * emailTextView.getLineHeight() - emailTextView.getHeight();
            dy = (startY + dy > maxScroll) ? maxScroll - startY : dy;
        }

        if (mode) {
            scroller.startScroll(0, startY, 0, dy, 900);
            emailTextView.invalidate();
        } else {
            emailTextView.scrollBy(0, dy);
        }
    }


    private class MainCardLayoutGestureListener extends GestureDetector.SimpleOnGestureListener {
        final static String TAG = "GestureDetector";
        /**
         * <p>Constants for check that gesture is dismissing view or scrolling text.</p>
         * Because wrong gesture is for text scrolling more possible
         *
         * @value scrollModeConstant for onScroll method
         * @value flingModeConstant for onFling method
         */
        final float scrollModeConstant = 0.7f;
        final float flingModeConstant = 0.6f;


        /**
         * <p>Check that a gesture on the emailcard view</p>
         * At First in Y coordinate because using vertical layout
         * After in X coordinate
         *
         * @return true if a gesture on the emailcard view and false whatever other
         */
        @Override
        public boolean onDown(MotionEvent ev) {
            float evY = ev.getY();
            float viewTop = emailCardView.getY();
            float viewBottom = viewTop + emailCardView.getHeight() - emailCardView.getContentPaddingBottom();
            viewTop += emailCardView.getContentPaddingTop();
            if (viewBottom < evY || evY < viewTop) {
                return false;
            }

            float evX = ev.getX();
            float viewLeft = emailCardView.getX();
            float viewRight = viewLeft + emailCardView.getWidth() - emailCardView.getContentPaddingRight();
            viewLeft += emailCardView.getContentPaddingLeft();
            return viewLeft < evX && evX < viewRight;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.d(TAG, new StringBuilder("onScroll ").append("x=").append(distanceX).append(";y=").append(distanceY).toString());
            if ((scrollModeConstant * Math.abs(distanceX)) > Math.abs(distanceY)) {
//                animateScrollView();
            } else {
//                animateScrollText();
                scrollEmailTextView(distanceY, false);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, new StringBuilder("onFling ").append("x=").append(velocityX).append(";y=").append(velocityY).toString());
            if ((flingModeConstant * Math.abs(velocityX)) > Math.abs(velocityY)) {
//                animateFlingView();
            } else {
                scrollEmailTextView(velocityY, true);
            }
            return true;
        }
    }
}