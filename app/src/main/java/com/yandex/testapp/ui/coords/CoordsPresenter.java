package com.yandex.testapp.ui.coords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Coord coord = intent.getParcelableExtra("coord");
            onDataNewItemAdded(coord);
        }
    };

    public CoordsPresenter(Context context, CoordsContract.View coordsView) {
        mContext = context;
        mCoordsView = coordsView;
        mCoordsView.setPresenter(this);

        CoordsDataSource dataSource = CoordsLocalDataSource.getInstance(context);
        mCoordsProvider = CoordsProvider.getInstance(dataSource);
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter(CoordsProvider.EVENT_NEW_DATA_ITEM_ADDED));

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

    @Override
    public void unregisterReceiver() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }
}
