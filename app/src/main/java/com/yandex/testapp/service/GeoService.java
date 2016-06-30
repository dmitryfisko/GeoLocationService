package com.yandex.testapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.data.source.CoordsDataSource;
import com.yandex.testapp.data.source.CoordsProvider;
import com.yandex.testapp.data.source.local.CoordsLocalDataSource;
import com.yandex.testapp.util.location.LocationTracker;
import com.yandex.testapp.util.location.LocationsListener;

public class GeoService extends Service implements LocationsListener {

    long DEFAULT_TIME_INTERVAL = 3000;

    private CoordsProvider mCoordsProvider;
    private LocationTracker mLocationTracker;

    @Override
    public void onCreate() {
        CoordsDataSource dataSource = CoordsLocalDataSource.getInstance(this);
        mCoordsProvider = CoordsProvider.getInstance(dataSource);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long timeInterval = DEFAULT_TIME_INTERVAL;
        if (intent != null) {
             timeInterval = intent.getLongExtra("time_interval", DEFAULT_TIME_INTERVAL);
        }

        mLocationTracker = new LocationTracker(timeInterval, this, this);
        mLocationTracker.startTracking();
        return START_REDELIVER_INTENT;
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
