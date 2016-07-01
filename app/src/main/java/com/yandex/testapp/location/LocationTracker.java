package com.yandex.testapp.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.location.network.NetworkLocationInfo;
import com.yandex.testapp.location.network.NetworkLocationListener;
import com.yandex.testapp.location.network.WifiAndCellCollector;

import java.util.concurrent.TimeUnit;

public class LocationTracker implements NetworkLocationListener {

    private long mTimeInterval;
    private LocationTask mLocationTask;
    private LocationsListener mCallback;
    private Context mContext;

    private WifiAndCellCollector mNetworkTracker;
    private LocationManager mGPSTracker;
    private LocationHistory mLastNetLocation;
    private LocationHistory mLastGPSLocation;

    private LocationListener mGPSListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            long timestamp = System.currentTimeMillis();
            Coord coord = new Coord(longitude, latitude, altitude, timestamp);
            mLastGPSLocation = new LocationHistory(coord);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    };

    public LocationTracker(long timeIntervalMs, Context context, LocationsListener callback) {
        mTimeInterval = timeIntervalMs;
        mContext = context;
        mCallback = callback;
        mNetworkTracker = new WifiAndCellCollector(context, timeIntervalMs, this);
        mGPSTracker = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking() {
        if (isPermissionGranted()) {
            try {
                mGPSTracker.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGPSListener);
            } catch (SecurityException ignored) {}
        }
        mNetworkTracker.startCollect();

        mLocationTask = new LocationTask();
        mLocationTask.execute();
    }

    public void stopTracking() {
        mLocationTask.cancel(true);

        mNetworkTracker.stopCollect();
        if (isPermissionGranted()) {
            try {
                mGPSTracker.removeUpdates(mGPSListener);
            } catch (SecurityException ignored) {}
        }
    }

    @Override
    public void onNetworkLocationChanged(NetworkLocationInfo location) {
        if (location.lbsType == null) {
            return;
        }

        double longitude = Double.parseDouble(location.lbsLongtitude);
        double latitude = Double.parseDouble(location.lbsLatitude);
        double altitude = Double.parseDouble(location.lbsAltitude);
        long time = System.currentTimeMillis();
        Coord coord = new Coord(latitude, longitude, altitude, time);
        mLastNetLocation = new LocationHistory(coord);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            int fineLocationState = ContextCompat.checkSelfPermission(
                    mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);
            boolean isFineLocGranted = (fineLocationState == PackageManager.PERMISSION_GRANTED);

            int coarseLocationState = ContextCompat.checkSelfPermission(
                    mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
            boolean isCoarseLocGranted = (coarseLocationState == PackageManager.PERMISSION_GRANTED);

            return isFineLocGranted || isCoarseLocGranted;
        }
        return true;
    }

    class LocationTask extends AsyncTask<Void, Void, Void> {

        private Coord getLocation() {
            Coord coord;
            if (mLastGPSLocation != null && mLastGPSLocation.isValid(mTimeInterval)) {
                coord = mLastGPSLocation.getCoord();
            } else if (mLastNetLocation != null && mLastNetLocation.isValid(mTimeInterval)) {
                coord = mLastNetLocation.getCoord();
            } else if (mLastGPSLocation != null) {
                coord = mLastGPSLocation.getCoord();
            } else if (mLastNetLocation != null) {
                coord = mLastNetLocation.getCoord();
            } else {
                coord = null;
            }

            if (coord != null) {
                return coord.changeTimestamp(System.currentTimeMillis());
            } else {
                return null;
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(mTimeInterval);
                    Coord coord = getLocation();
                    mCallback.onLocationChanged(coord);
                } catch (InterruptedException ignored) {}
            }
            return null;
        }

    }

    private class LocationHistory {

        private Coord mCoord;

        public LocationHistory(Coord coord) {
            mCoord = coord;
        }

        public Coord getCoord() {
            return mCoord;
        }

        public boolean isValid(long timeInterval) {
            long currentTime = System.currentTimeMillis();
            return currentTime - mCoord.getTimestamp() <= timeInterval;
        }

    }


}
