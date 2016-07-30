package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.SwipeToRevealTouchListener;

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
}
