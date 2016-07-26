package ru.yandex.yamblz.ui.views;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.OnSwipeTouchListener;

/**
 * Created by user on 26.07.16.
 */

public class CustomLayout extends FrameLayout {

    private static final int ANIMATION_DURATION = 200;
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    @BindView(R.id.dotsView)
    DotsView dotsView;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.deleteBtn)
    Button deleteButton;

    private float cardView_x;
    private float deleteBtn_x;
    private float dotsView_x;

    public CustomLayout(Context context) {
        super(context);
        init();
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.inside_custom_layout, this);
        ButterKnife.bind(this);
        cardView_x = cardView.getX();
        deleteBtn_x = deleteButton.getX();
        dotsView_x = dotsView.getX();
        animationListener();
    }

    private void animationListener() {
        cardView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            int speedScroll = 0;

            @Override
            public void onReturnFirstState() {

                if (Math.abs(cardView.getX()) < 800) {
                    cardView.animate()
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(ANIMATION_DURATION)
                            .translationX(cardView_x)
                            .rotation(0)
                            .start();

                    deleteButton.animate()
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(ANIMATION_DURATION)
                            .translationX(deleteBtn_x)
                            .alpha(0)
                            .rotation(0)
                            .start();

                    dotsView.animate()
                            .translationX(dotsView_x)
                            .setDuration(0)
                            .rotation(0)
                            .start();

                } else {
                    ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(dotsView, DotsView.DOTS_PROGRESS, 0, 1f);
                    dotsAnimator.setDuration(1000);
                    dotsAnimator.setStartDelay(0);
                    dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
                    dotsAnimator.start();

                    cardView.setVisibility(GONE);
                    deleteButton.setVisibility(GONE);
                }
            }

            @Override
            public void onHorisontalScroll(float x1, float x2) {

                float diffX = x2 - x1;

                cardView.animate()
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(0)
                        .translationXBy(diffX)
                        .rotationBy(diffX / 50f)
                        .start();

                deleteButton.animate()
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(0)
                        .translationXBy(diffX / -5f)
                        .alphaBy(0.05f)
                        .start();

                dotsView.animate()
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(0)
                        .translationXBy(diffX / -5f)
                        .start();
            }

            @Override
            public void onVerticalScroll(float y1, float y2) {

                float diffY = y2 - y1;

                int startScroll = 0;
                int endScroll = (textView.getLineCount() - textView.getMaxLines()) * textView.getLineHeight();
                if (diffY >= startScroll) {
                    if (speedScroll < startScroll) {
                        speedScroll = startScroll;
                    }
                    speedScroll -= diffY / 10;
                    textView.scrollTo(0, speedScroll);
                    return;
                }

                if (speedScroll > endScroll) {
                    speedScroll = endScroll;
                }
                speedScroll += -diffY / 10;
                textView.scrollTo(0, speedScroll);
            }
        });
    }
}
