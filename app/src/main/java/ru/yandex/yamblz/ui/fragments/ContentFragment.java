package ru.yandex.yamblz.ui.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.SimpleAnimator;
import ru.yandex.yamblz.ui.views.SwipeToRevealTouchListener;

import static ru.yandex.yamblz.App.displayWidth;

public class ContentFragment extends BaseFragment {
    @BindView(R.id.dismissable_view)
    View dismissable;
    @BindView(R.id.revealing_view)
    View revealView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeToRevealTouchListener listener = new SwipeToRevealTouchListener();
        listener.setRevealView(revealView);
        dismissable.setOnTouchListener(listener);
    }

    @OnClick(R.id.revealing_view)
    public void onDeleteClicked(View view) {
        view.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new SimpleAnimator() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animation.removeListener(this);
                        view.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .setInterpolator(new OvershootInterpolator())
                                .start();

                        dismissable.animate()
                                .alpha(0)
                                .translationXBy(getSwipeVelocity(dismissable, 500))
                                .setInterpolator(new AccelerateInterpolator())
                                .start();

                    }
                })
                .start();
    }

    private int getSwipeVelocity(View view, int speed) {
        return view.getX() > displayWidth / 2 ? speed : -speed;
    }

}
