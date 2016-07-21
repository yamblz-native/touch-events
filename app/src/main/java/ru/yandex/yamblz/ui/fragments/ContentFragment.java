package ru.yandex.yamblz.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.touch.AnimationTouchListener;

public class ContentFragment extends BaseFragment {
    private final String LOG_TAG = this.getClass().getName();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_content, container, false);
        View toDrag = result.findViewById(R.id.toDrag);
        View deleteButton = result.findViewById(R.id.btnDelete);

        toDrag.setOnTouchListener(
                new AnimationTouchListener((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE), deleteButton)
        );

        return result;
    }
}
