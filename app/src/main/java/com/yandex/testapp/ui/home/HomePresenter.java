package com.yandex.testapp.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import com.yandex.testapp.service.GeoService;

public class HomePresenter implements HomeContract.Presenter {

    static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
    };

    static final int GEO_SERVICE_PERMISSIONS_REQUEST = 100;

    private HomeContract.View mHomeView;
    private Context mContext;

    public HomePresenter(Context context, HomeContract.View homeView) {
        mContext = context;
        mHomeView = homeView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void serviceStateChanged(boolean isSwitchOn) {
        boolean isServiceOn = isGeoServiceRunning();
        if(isServiceOn == isSwitchOn) {
            return;
        }

        if (isServiceOn) {
            stopGeoService();
        } else {
            checkPermissions();
        }
    }

    @Override
    public void start() {
        if (isGeoServiceRunning()) {
            mHomeView.setSwitchState(true);
        } else {
            mHomeView.setSwitchState(false);
        }

    }

    private void checkPermissions() {
        HomeFragment homeFragment = (HomeFragment) mHomeView;
        homeFragment.requestPermissions(PERMISSIONS,
                GEO_SERVICE_PERMISSIONS_REQUEST);
    }

    @Override
    public void startGeoServiceManually() {
        Intent intent = new Intent(mContext, GeoService.class);
        mContext.startService(intent);
    }

    private void stopGeoService() {
        Intent intent = new Intent(mContext, GeoService.class);
        mContext.stopService(intent);
    }

    private boolean isGeoServiceRunning() {
        String serviceName = GeoService.class.getName();
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
