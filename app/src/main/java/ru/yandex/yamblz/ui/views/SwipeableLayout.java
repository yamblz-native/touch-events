package ru.yandex.yamblz.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

/**
 * Created by dalexiv on 7/28/16.
 */

public class SwipeableLayout extends FrameLayout {
    private static final String TAG = SwipeableLayout.class.getSimpleName();

    // Animation constants
    private static final int FADE_OUT_ROTATION = 45;
    private static final float ROTATION_COEF = 1.0f / 20;
    private static final int ANIMATION_DURATION = 200;
    private static final int MAX_RETURN_BACK_ROTATION = 15;

    // Inner views
    @BindView(R.id.content)
    View swipeableCard;
    @BindView(R.id.back_image)
    ImageView deleteImage;

    private boolean isSwiped = false;
    private ISwipeableLayout callback;

    private GestureDetectorCompat gd
            = new GestureDetectorCompat(getContext(), new FlingGestureListener());

    public SwipeableLayout(Context context) {
        super(context);
        initView(context);
    }

    public SwipeableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.swipeable_element, this);
        ButterKnife.bind(this);

        // Set listener to restore view state
        deleteImage.setOnClickListener(view -> {
            restoreLayout();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        // Handle scroll
        gd.onTouchEvent(ev);

        // Handling ACTION_UP
        if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            int targetAlpha = 0;
            // Card animation
            if (Math.abs(swipeableCard.getRotation()) < MAX_RETURN_BACK_ROTATION) {
                // Return card back
                swipeableCard.animate().rotation(0)
                        .translationX(0)
                        .setStartDelay(0)
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new FastOutSlowInInterpolator());
                targetAlpha = 0;
            } else {
                // Swipe card out
                float translationToGo = swipeableCard.getTranslationX() > 0
                        ? swipeableCard.getHeight() : -swipeableCard.getHeight();
                float rotationToGo = swipeableCard.getRotation() > 0
                        ? FADE_OUT_ROTATION : -FADE_OUT_ROTATION;

                swipeableCard.animate().rotation(rotationToGo)
                        .translationX(translationToGo)
                        .alpha(0)
                        .setStartDelay(0)
                        .setDuration(ANIMATION_DURATION);
                targetAlpha = 255;

                isSwiped = true;
                if (callback != null)
                    callback.onLayoutSwiped();
            }
            animateImageReturnBack(targetAlpha);
        }
        return true;
    }


    private void animateImageReturnBack(int targetAlpha) {
        ObjectAnimator alphaDiscardAnimator = ObjectAnimator.ofInt(deleteImage,
                "alpha", deleteImage.getImageAlpha(), targetAlpha);
        alphaDiscardAnimator.setInterpolator(new FastOutSlowInInterpolator());
        alphaDiscardAnimator.setDuration(ANIMATION_DURATION);

        ObjectAnimator translationXDiscardAnimator = ObjectAnimator.ofFloat(deleteImage,
                "translationX", deleteImage.getTranslationX(), 0);
        translationXDiscardAnimator.setInterpolator(new FastOutSlowInInterpolator());
        translationXDiscardAnimator.setDuration(ANIMATION_DURATION);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.playTogether(alphaDiscardAnimator, translationXDiscardAnimator);
        animationSet.start();
    }

    private class FlingGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isSwiped)
                return true;

            final float delta = e2.getX() - e1.getX();

            // Animate content
            swipeableCard.animate()
                    .translationX(delta)
                    .rotation(delta * ROTATION_COEF)
                    .setStartDelay(0)
                    .setDuration(0);

            // Animate background card alpha
            final int alpha = (int) (Math.abs(delta * ROTATION_COEF)
                    / (1.5 * MAX_RETURN_BACK_ROTATION) * 255.0);
            deleteImage.setImageAlpha(alpha);

            // Animate background card position
            float imageDelta = -delta;
            if (Math.abs(delta) > deleteImage.getWidth() / 2)
                if (swipeableCard.getRotation() > 0)
                    imageDelta -= 2 * (deleteImage.getWidth() / 2 - delta);
                else
                    imageDelta += 2 * (deleteImage.getWidth() / 2 + delta);
            deleteImage.animate()
                    .translationX(imageDelta)
                    .setStartDelay(0)
                    .setDuration(0);
            return true;
        }
    }

    public void setCallback(ISwipeableLayout callback) {
        this.callback = callback;
    }

    public void restoreLayout() {
        ObjectAnimator alphaDiscardAnimator = ObjectAnimator.ofInt(deleteImage,
                "alpha", deleteImage.getImageAlpha(), 0);
        alphaDiscardAnimator.setInterpolator(new FastOutSlowInInterpolator());
        alphaDiscardAnimator.setDuration(ANIMATION_DURATION);
        alphaDiscardAnimator.start();

        swipeableCard.animate().rotation(0)
                .translationX(0)
                .alpha(255)
                .setStartDelay(0)
                .setDuration(ANIMATION_DURATION);
        isSwiped = false;
    }
}
