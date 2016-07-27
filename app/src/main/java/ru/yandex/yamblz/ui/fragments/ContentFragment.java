package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.yandex.yamblz.CustomOnTouchListener;
import ru.yandex.yamblz.R;

public class ContentFragment extends BaseFragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        view.findViewById(R.id.card).setOnTouchListener(new CustomOnTouchListener(view.findViewById(R.id.fl_delete)));

        return view;
    }

}
