package ru.yandex.yamblz.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.squareup.seismic.ShakeDetector;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;

import static android.content.Context.SENSOR_SERVICE;
import static android.view.MotionEvent.ACTION_UP;

public class MailCardLayout extends FrameLayout implements ShakeDetector.Listener {
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private float cardStartPositionX;
    private float cardStartPositionY;
    private float deleteIconStartPositionX;
    private float inboxIconStartPositionX;
    private int countOfCards = 5;

    @BindView(R.id.email_card) CardView emailCardView;
    @BindView(R.id.email_text) TextView emailTextView;
    @BindView(R.id.email_icon_delete) ImageView deleteIcon;
    @BindView(R.id.email_icon_inbox) ImageView inboxIcon;
    @BindView(R.id.email_icon_check) ImageView checkIcon;
    @BindView(R.id.background_error) View background_error;

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
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
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
                float offset = cardStartPositionX - emailCardView.getX();
                if (Math.abs(offset) < emailCardView.getWidth() / 2.3f) {
                    returnCardStartLocation();
                    hideAllIcons();
                } else {
                    dismissEmailCardView(-offset, getIconByRotation());
                }
        }
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * <p>Show new card or Error background if all cards gone</p>
     */
    private void getNewCard() {
        emailCardView.animate().setListener(null).cancel();
        hideAllIcons();
        if (countOfCards > 0) {
            emailTextView.setScrollY(0);
            emailCardView.setRotation(0);
            emailCardView.setY(cardStartPositionY - 2000);
            emailCardView.setX(cardStartPositionX);
            countOfCards--;
            returnCardStartLocation();
        } else {
            background_error.animate().alpha(1).setDuration(1000)
                    .start();
            emailCardView.animate().alpha(0)
                    .start();
        }
    }

    /**
     * <p>Hide all icons element and return them to the start position</p>
     */
    private void hideAllIcons() {
        deleteIcon.animate().x(deleteIconStartPositionX).alpha(0).start();
        inboxIcon.animate().x(inboxIconStartPositionX).alpha(0).start();
        checkIcon.animate().alpha(0).start();
    }

    /**
     * <p>Returning start position to all moving element</p>
     */
    private void returnCardStartLocation() {
        emailCardView.animate().x(cardStartPositionX).y(cardStartPositionY).rotation(0).start();
    }

    @OnClick({R.id.mail_move_to_inbox, R.id.mail_remove, R.id.mail_check})
    void onClick(View v) {
        float swipeValue = 6000;
        switch (v.getId()) {
            case R.id.mail_move_to_inbox:
                dismissEmailCardView(-swipeValue, inboxIcon);
                break;
            case R.id.mail_remove:
                dismissEmailCardView(swipeValue, deleteIcon);
                break;
            case R.id.mail_check:
                dismissEmailCardView(swipeValue, checkIcon);
                break;
        }
    }

    /**
     * <p>Return view which which should using now by MailCardView rotation info </p>
     * And set other view Alpha 0
     *
     * @return icon view, which should using now
     */
    private View getIconByRotation() {
        boolean chooseViewCondition = emailCardView.getRotation() < 0;
        return chooseViewCondition ? inboxIcon : deleteIcon;
    }

    private void dismissEmailCardView(float value, View iconView) {
        emailCardView.animate().cancel();
        float destination = cardStartPositionX + (value > 0 ? 1 : -1) * 2 * emailCardView.getWidth();
        ViewPropertyAnimator dismissAnim = emailCardView.animate();
        switch (iconView.getId()) {
            case R.id.email_icon_delete:
            case R.id.email_icon_inbox:
                dismissAnim.x(destination).rotation(emailCardView.getRotation() + value / 600)
                        .setDuration(400);
                iconView.animate().cancel();
                iconView.animate()
                        .x(cardStartPositionX + emailCardView.getMeasuredWidth() / 2 - iconView.getMeasuredWidth() / 2)
                        .setDuration(400).alpha(1)
                        .start();
                break;
            case R.id.email_icon_check:
                dismissAnim.y(destination).setDuration(300).start();
                iconView.animate().setDuration(300).alpha(1).start();
                break;
        }
        dismissAnim.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getNewCard();
            }
        });
        dismissAnim.start();
    }

    /**
     * <p>Moving and animate emailCardView</p>
     * <p>
     *
     * @param value Distance for Scrolling
     */
    private void moveEmailCardView(float value) {
        float absCardRotation = Math.abs(emailCardView.getRotation());
        View iconView = getIconByRotation();
        float postionOffset = 0.9f * value;
        emailCardView.setX(emailCardView.getX() - postionOffset);
        emailCardView.setRotation(emailCardView.getRotation() - postionOffset / 13);

        if (absCardRotation < 11) {
            iconView.setAlpha(absCardRotation / 10-0.1f);
        }

        float iconStartPosition = emailCardView.getRotation() < 0 ? inboxIconStartPositionX : deleteIconStartPositionX;
        float offsetForIcon = cardStartPositionX - emailCardView.getX() + value;
        iconView.setX(getIconNewX(offsetForIcon, iconView.getWidth(), iconStartPosition));
    }


    /**
     * <p>Get new X position to icon when moving email card</p>
     *
     * @param cardOffset card offset
     * @param iconWidth  width of icon
     * @param iconStartX icon start position
     */
    private float getIconNewX(float cardOffset, float iconWidth, float iconStartX) {
        float offset = Math.abs(cardOffset);
        iconWidth = iconWidth / 2f;
        if (offset < iconWidth) {
            return iconStartX + cardOffset;
        } else {
            float delta = (offset - iconWidth) * 0.3f;
            if (cardOffset < 0) {
                return Math.min(iconStartX, iconStartX - iconWidth + delta);
            } else {
                return Math.max(iconStartX, iconStartX + iconWidth - delta);
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
                moveEmailCardView(distanceX);
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
                    dismissEmailCardView(velocityX, getIconByRotation());
                }
            } else {
                scrollEmailTextView(velocityY, true);
            }
            return true;
        }
    }

    /**
     * <p>For renew email cards in debug</p>
     */
    @Override
    public void hearShake() {
        countOfCards += 5;
        emailCardView.setAlpha(1);
        background_error.animate().alpha(0).start();
        getNewCard();
    }
}