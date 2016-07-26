package ru.yandex.yamblz.ui.views;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.util.OnSwipeListener;

/**
 * Created by root on 7/26/16.
 */
public class Content extends FrameLayout {

    @BindView(R.id.card) CardView card;
    @BindView(R.id.card_layout) LinearLayout cardLayout;
    @BindView(R.id.text_content) TextView textContent;
    @BindView(R.id.delete_button) Button deleteButton;

    private float initialX;

    private static final int ANIMATION_DURATION = 150;

    private boolean isGone = false;
    private boolean buttonMoved = false;

    public Content(Context context) {
        super(context);
        init();
    }

    public Content(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Content(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.content_item, this);
        ButterKnife.bind(this);
        initialX = card.getX();
        initScrollableText();
        setCardAnimation();

    }

    private void initScrollableText() {
        textContent.setVerticalScrollBarEnabled(true);
        textContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
    }

    private void setCardAnimation() {
        card.setOnTouchListener(new OnSwipeListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                swipe();
            }

            @Override
            public void onScrollX(float x1, float x2) {

                final float ROTATION_NORMALIZATION = 45f;

                float diffX = x2 - x1;

                // block right swipe
                if(card.getX() > card.getWidth() / 3 && diffX > 0) {
                    return;
                }

                moveButton(diffX);

                card.animate().setInterpolator(new LinearInterpolator()).setDuration(0)
                        .translationXBy(diffX)
                        .alphaBy(diffX / card.getWidth())
                        .rotationBy(diffX / ROTATION_NORMALIZATION)
                        .start();

                deleteButton.animate().setInterpolator(new LinearInterpolator()).setDuration(0)
                        .alphaBy(-diffX / card.getWidth())
                        .start();

            }

            @Override
            public void onScrollY(float y1, float y2) {

                float diffY = y2 - y1;

                int minScrollY = 0;
                int maxScrollY = (textContent.getLineCount() - textContent.getMaxLines()) * textContent.getLineHeight();

                if(diffY >= minScrollY) {
                    textContent.scrollTo(0, minScrollY);
                    return;
                }

                if(diffY <= -maxScrollY) {
                    textContent.scrollTo(0, maxScrollY);
                    return;
                }

                textContent.scrollTo(0, (int) -diffY);
            }

            @Override
            public void onUp(float x, float y) {

                if(-card.getX() > card.getWidth() / 2) {
                    swipe();
                } else {
                    backToHome();
                }
            }
        });
    }

    private void moveButton(float diffX) {
        if(-card.getX() > card.getWidth() / 4 && diffX < 0 && !buttonMoved) {
            buttonMoved = true;
            deleteButton.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                    .translationXBy(deleteButton.getWidth())
                    .start();
        }

        if(-card.getX() < card.getWidth() / 3 && diffX > 0 && buttonMoved) {
            buttonMoved = false;
            deleteButton.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                    .translationXBy(-deleteButton.getWidth())
                    .start();
        }
    }

    private void backToHome() {
        if(isGone) {
            return;
        }

        card.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                .translationX(initialX)
                .alpha(1)
                .rotation(0)
                .start();

        deleteButton.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                .alpha(0)
                .start();
    }

    private void swipe() {

        if(isGone) {
            return;
        }

        isGone = true;

        final float FINAL_ROTATION = -90f;

        card.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        close();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .translationX(-card.getWidth())
                .alpha(0)
                .rotation(FINAL_ROTATION)
                .start();

        deleteButton.animate().setInterpolator(new LinearInterpolator()).setDuration(ANIMATION_DURATION)
                .alpha(0)
                .scaleX(0)
                .scaleY(0)
                .start();
    }

    public void close() {
        card.setVisibility(GONE);
        deleteButton.setVisibility(GONE);
    }

}
