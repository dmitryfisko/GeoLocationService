package com.yandex.testapp.ui.coords;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yandex.testapp.R;
import com.yandex.testapp.data.Coord;

import java.util.List;

public class CoordsFragment extends Fragment implements CoordsContract.View {

    private CoordsContract.Presenter mPresenter;

    private ListView mListView;
    private View mListFooterView;
    private View mDataNotAvaliableView;
    private FrameLayout mListFooterContainer;

    private CoordsListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.coords_fragment, container, false);

        mListView = (ListView) root.findViewById(R.id.coords_list);
        mDataNotAvaliableView = root.findViewById(R.id.data_not_avaliable);
        mListFooterContainer = new FrameLayout(getContext());
        mListFooterView = inflater.inflate(R.layout.coords_fragment_list_footer,
                new LinearLayout(getContext()), false);

        mListAdapter = new CoordsListAdapter(getActivity());
        mListView.addFooterView(mListFooterContainer);
        mListView.setAdapter(mListAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showCoords(List<Coord> coords) {
        mListAdapter.replaceData(coords);
    }

    @Override
    public void showNewCoord(Coord coord) {
        mListAdapter.addItem(coord);
    }

    @Override
    public void showLoadingFooter() {
        mListFooterContainer.addView(mListFooterView);
    }

    @Override
    public void hideLoadingFooter() {
        mListFooterContainer.removeAllViews();
    }

    @Override
    public void showDataNotAvaliable() {
        mListView.setVisibility(View.GONE);
        mDataNotAvaliableView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDataNotAvaliable() {
        mListView.setVisibility(View.VISIBLE);
        mDataNotAvaliableView.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(CoordsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unregisterReceiver();
    }
}
