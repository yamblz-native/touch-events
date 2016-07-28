package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import ru.yandex.yamblz.R;

public class MailCardLayout extends FrameLayout {
    private GestureDetector gestureDetector;

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    private class MainCardLayoutGestureListener extends GestureDetector.SimpleOnGestureListener {
        final static String TAG = "GestureDetector";
        /**
         * <p>Constants for check that gesture is dismissing view or scrolling text.</p>
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
            if (viewBottom < evY ||  evY < viewTop) {
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
            Log.d(TAG, new StringBuilder("onScroll ").append("x=").append(distanceX).append(";y=").append(distanceY).toString());
            if ((scrollModeConstant * Math.abs(distanceX)) > Math.abs(distanceY)) {
//                animateScrollView();
            } else {
//                animateScrollText();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, new StringBuilder("onFling ").append("x=").append(velocityX).append(";y=").append(velocityY).toString());
            if ((flingModeConstant * Math.abs(velocityX)) > Math.abs(velocityY)) {
//                animateFlingView();
            } else {
//                animateFlingText();
            }
            return true;
        }

    }
}