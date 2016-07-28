package ru.yandex.yamblz.ui.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.task.SwipeToDismissFrameLayout;

public class ContentFragment extends BaseFragment
        implements SwipeToDismissFrameLayout.OnSwipedListener {
    private int poems[];
    private int poemI = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poems = new int[3];
        poems[0] = R.string.poem_0;
        poems[1] = R.string.poem_1;
        poems[2] = R.string.poem_2;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ((SwipeToDismissFrameLayout) view).setOnSwipedListener(this);
        ((TextView) view.findViewById(R.id.text_view)).setText(poems[poemI]);
        return view;
    }

    @Override
    public void OnSwiped(SwipeToDismissFrameLayout swipeToDismissFrameLayout) {
        final int DURATION = 600;

        View view = swipeToDismissFrameLayout.findViewById(R.id.card_view);
        ((TextView) view.findViewById(R.id.text_view)).setText(poems[(++poemI) % poems.length]);
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, "translationX", swipeToDismissFrameLayout.getWidth(), 0)
                .setDuration(DURATION);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }
}
