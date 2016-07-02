package com.yandex.testapp.ui.coords;

import android.content.Context;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.data.source.CoordsDataSource;
import com.yandex.testapp.data.source.CoordsProvider;
import com.yandex.testapp.data.source.local.CoordsLocalDataSource;
import com.yandex.testapp.util.ServiceUtils;

import java.util.List;

public class CoordsPresenter implements CoordsContract.Presenter, CoordsDataSource.LoadCoordsCallback {

    private CoordsContract.View mCoordsView;
    private CoordsProvider mCoordsProvider;
    private Context mContext;

    private boolean isDataWasNotAvaliable;

    public CoordsPresenter(Context context, CoordsContract.View coordsView) {
        mContext = context;
        mCoordsView = coordsView;
        mCoordsView.setPresenter(this);

        CoordsDataSource dataSource = CoordsLocalDataSource.getInstance(context);
        mCoordsProvider = CoordsProvider.getInstance(dataSource);

        isDataWasNotAvaliable = false;
    }

    @Override
    public void start() {
        mCoordsView.showLoadingFooter();
        mCoordsProvider.getCoords(this);
    }

    @Override
    public void onCoordsLoaded(List<Coord> coords) {
        mCoordsView.showCoords(coords);
        if (!ServiceUtils.isGeoServiceRunning(mContext)) {
            mCoordsView.hideLoadingFooter();
        }
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

}
