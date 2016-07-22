package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.SwipeFrameLayout;

public class ContentFragment extends BaseFragment
{
    private Unbinder viewBinder;

    @BindView(R.id.swipe_view) View swipeView;
    @BindView(R.id.delete_view) View deleteView;
    @BindView(R.id.main_layout) SwipeFrameLayout swipeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle)
    {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        viewBinder = ButterKnife.bind(this, view);
        swipeLayout.addSwipeView(swipeView);
        swipeLayout.addDeleteView(deleteView);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        if (viewBinder != null)
        {
            viewBinder.unbind();
        }
        super.onDestroyView();
    }
}
