package ru.yandex.yamblz.ui.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.BetterFrameLayout;

public class ContentFragment extends BaseFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ImageView backgroundImageView = (ImageView) view.findViewById(R.id.background_image_view);
        ViewGroup frameLayout = (ViewGroup) view.findViewById(R.id.frame_layout);
        BetterFrameLayout betterFrameLayout = (BetterFrameLayout) frameLayout;
        ViewGroup smallFrameLayout = (ViewGroup) view.findViewById(R.id.small_frame_layout);
        TextView textView = (TextView) view.findViewById(R.id.text_view);

        textView.setText("В ходе реформы Российской академии наук предусмотрен демонтаж всех металлических конструкций с крыши главного здания академии. Эти конструкции затем будут переплавлены в огромный таз, которым и накроется вся Российская наука");

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            float x0, x;
            boolean isAnimated;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0 = event.getX();
                        isAnimated = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!isAnimated) {
                            x = event.getX();
                            x0 = betterFrameLayout.getX0();
                            float delta = x - x0;
                            smallFrameLayout.setRotation((delta) / 10);
                            smallFrameLayout.setTranslationX(delta);
                            backgroundImageView.setAlpha(-delta / 600);
                            if (-delta < 200) {
                                backgroundImageView.setTranslationX(-delta / 10 - (delta + 200) / 4);
                            } else {
                                backgroundImageView.setTranslationX(-delta / 10 + (delta + 200) / 4);
                            }

                            if (-delta > 350) {
                                isAnimated = true;
                                ObjectAnimator alphaAnimator = ObjectAnimator
                                        .ofFloat(frameLayout, "alpha", smallFrameLayout.getAlpha(), 0.0f)
                                        .setDuration(1000);
                                alphaAnimator.start();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!isAnimated) {
                            ObjectAnimator rotatingAnimator = ObjectAnimator
                                    .ofFloat(smallFrameLayout, "rotation", smallFrameLayout.getRotation(), 0.0f)
                                    .setDuration(500);

                            ObjectAnimator translationAnimator = ObjectAnimator
                                    .ofFloat(smallFrameLayout, "translationX", smallFrameLayout.getTranslationX(), 0.0f)
                                    .setDuration(500);

                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(translationAnimator)
                                    .with(rotatingAnimator);
                            animatorSet.start();
                        } else {
                            view.findViewById(R.id.end_text_view).setVisibility(View.VISIBLE);
                        }
                        /*
                        backgroundImageView.setAlpha(0.0f);
                        backgroundImageView.setTranslationX(0);
                        frameLayout.setAlpha(1.0f);
                        isAnimated = false;
                        */
                        break;
                }

                return true;
            }
        });

        return view;
    }
}
