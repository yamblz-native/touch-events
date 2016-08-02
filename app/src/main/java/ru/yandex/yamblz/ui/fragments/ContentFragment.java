package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.OnImageTouchListener;

public class ContentFragment extends BaseFragment {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_content, container, false);


        ImageView imageView = (ImageView) rootView.findViewById(R.id.dummy_image);
        ImageView buttonView = (ImageView) rootView.findViewById(R.id.button_image);
        if (imageView == null) {
            Log.v("Touched", "imageView not found");
        } else {
            imageView.setOnTouchListener(
                    new OnImageTouchListener(imageView, buttonView)
            );
        }
        return rootView;
    }
}
