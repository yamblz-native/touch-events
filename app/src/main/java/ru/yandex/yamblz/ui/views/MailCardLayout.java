package ru.yandex.yamblz.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;

import static android.view.MotionEvent.ACTION_UP;

public class MailCardLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private float cardStartPositionX;
    private float cardStartPositionY;
    private float deleteIconStartPositionX;
    private float inboxIconStartPositionX;

    @BindView(R.id.email_card)
    CardView emailCardView;
    @BindView(R.id.email_text)
    TextView emailTextView;
    @BindView(R.id.newCard)
    Button newCard;
    @BindView(R.id.email_icon_delete)
    ImageView deleteIcon;
    @BindView(R.id.email_icon_inbox)
    ImageView inboxIcon;

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
        emailCardView.setPivotY(emailCardView.getHeight());
        cardStartPositionX = emailCardView.getX();
        cardStartPositionY = emailCardView.getY();
        deleteIconStartPositionX = deleteIcon.getX();
        inboxIconStartPositionX = inboxIcon.getX();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_UP:
                //Return card to startPosition if scrolling was short
                float offset = cardStartPositionX - emailCardView.getX();
                if (Math.abs(offset) < emailCardView.getWidth() / 2.3f) {
                    returnStartLocation();
                } else {
                    swipeEmailCardView(-offset, true);
                }
        }
        return gestureDetector.onTouchEvent(event);
    }

    @OnClick(R.id.newCard)
    void returnStartLocation() {
        emailCardView.animate().cancel();
        emailCardView.animate()
                .x(cardStartPositionX)
                .y(cardStartPositionY)
                .rotation(0)
                .start();
        deleteIcon.animate()
                .x(deleteIconStartPositionX)
                .alpha(0)
                .start();
        inboxIcon.animate()
                .x(inboxIconStartPositionX)
                .alpha(0)
                .start();
    }

    /**
     * <p>Swipe and animate emailCardView</p>
     * <p>
     *
     * @param value Distance for Scrolling or Velocity in Flinging mode
     * @param mode  false for Scrolling or true for Flinging
     */
    private void swipeEmailCardView(float value, boolean mode) {
        boolean chooseViewCondition = emailCardView.getRotation() < 0;
        View iconView = chooseViewCondition ? inboxIcon : deleteIcon;
        if (mode) {
            float destination = cardStartPositionX + (value > 0 ? 1 : -1) * 2 * emailCardView.getWidth();
            emailCardView.animate().cancel();
            emailCardView.animate()
                    .x(destination)
                    .setDuration(200)
                    .rotation(value / 600)
                    .start();

            iconView.animate().cancel();
            iconView.animate()
                    .x(cardStartPositionX + emailCardView.getMeasuredWidth() / 2 - iconView.getMeasuredWidth() / 2)
                    .setDuration(200)
                    .alpha(1)
                    .start();

        } else {
            //0,9f for view point maximum stay under finger position
            float offset = 0.9f * value;
            emailCardView.setX(emailCardView.getX() - offset);
            emailCardView.setRotation(emailCardView.getRotation() - offset / 13);

            if (emailCardView.getRotation() == 0) {
                deleteIcon.setX(deleteIconStartPositionX);
                inboxIcon.setX(inboxIconStartPositionX);
            }

            if (Math.abs(emailCardView.getRotation()) < 10) {
                iconView.setX(iconView.getX() + value);
                if (chooseViewCondition) {
                    if (value < 0) {
                        iconView.setAlpha(iconView.getAlpha() - Math.abs(value) / 140);
                    } else {
                        iconView.setAlpha(iconView.getAlpha() + Math.abs(value) / 140);
                    }
                }
                if (!chooseViewCondition) {
                    if (value > 0) {
                        iconView.setAlpha(iconView.getAlpha() - Math.abs(value) / 140);
                    } else {
                        iconView.setAlpha(iconView.getAlpha() + Math.abs(value) / 140);
                    }
                }
            } else {
                iconView.setX(iconView.getX() - value);
            }
        }
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
        int dy = mode ? Math.round(-value / 6) : Math.round(value);

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
            if ((scrollModeConstant * Math.abs(distanceX)) > Math.abs(distanceY)) {
                swipeEmailCardView(distanceX, false);
            } else {
                scrollEmailTextView(distanceY, false);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((flingModeConstant * Math.abs(velocityX)) > Math.abs(velocityY)) {
                //If velocity is so small,maybe it's not a swipe
                if (Math.abs(velocityX) > 5000) {
                    swipeEmailCardView(velocityX, true);
                }
            } else {
                scrollEmailTextView(velocityY, true);
            }
            return true;
        }
    }
}