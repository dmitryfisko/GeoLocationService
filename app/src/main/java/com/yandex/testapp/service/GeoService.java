package com.yandex.testapp.service;

import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.IBinder;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.data.source.CoordsDataSource;
import com.yandex.testapp.data.source.CoordsProvider;
import com.yandex.testapp.data.source.local.CoordsLocalDataSource;
import com.yandex.testapp.location.LocationTracker;
import com.yandex.testapp.location.LocationsListener;

public class GeoService extends Service implements LocationsListener {

    private CoordsProvider mCoordsProvider;
    private LocationTracker mLocationTracker;

    @Override
    public void onCreate() {
        CoordsDataSource dataSource = CoordsLocalDataSource.getInstance(this);
        mCoordsProvider = CoordsProvider.getInstance(dataSource);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationTracker = new LocationTracker(2000, this, this);
        mLocationTracker.startTracking();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Coord coord) {
        mCoordsProvider.saveCoord(coord);
    }

    @Override
    public void onDestroy() {
        mLocationTracker.stopTracking();
    }

}
