package com.yandex.testapp.ui.coords;

import android.content.Context;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.data.source.CoordsDataSource;
import com.yandex.testapp.data.source.CoordsProvider;
import com.yandex.testapp.data.source.local.CoordsLocalDataSource;

import java.util.List;

public class CoordsPresenter implements CoordsContract.Presenter,
        CoordsDataSource.LoadCoordsCallback,
        CoordsDataSource.DataNewItemAddedCallback {

    private CoordsContract.View mCoordsView;
    private CoordsProvider mCoordsProvider;

    private boolean isDataWasNotAvaliable;

    public CoordsPresenter(Context context, CoordsContract.View coordsView) {
        mCoordsView = coordsView;
        mCoordsView.setPresenter(this);

        CoordsDataSource dataSource = CoordsLocalDataSource.getInstance(context);
        mCoordsProvider = CoordsProvider.getInstance(dataSource);
        mCoordsProvider.addChangesCallback(this);
        isDataWasNotAvaliable = false;
    }

    @Override
    public void start() {
        mCoordsProvider.getCoords(this);
        mCoordsView.showLoadingFooter();
    }

    @Override
    public void onCoordsLoaded(List<Coord> coords) {
        mCoordsView.showCoords(coords);
        mCoordsView.hideLoadingFooter();

    }

    @Override
    public void onDataNewItemAdded(Coord coord) {
        mCoordsView.showNewCoord(coord);
        if (isDataWasNotAvaliable) {
            mCoordsView.hideDataNotAvaliable();
        }
    }

    @Override
    public void onDataNotAvailable() {
        isDataWasNotAvaliable = true;
        mCoordsView.showDataNotAvaliable();
    }

    @Override
    public void removeCallback() {
        mCoordsProvider.removeChangesCallback(this);
    }
}
