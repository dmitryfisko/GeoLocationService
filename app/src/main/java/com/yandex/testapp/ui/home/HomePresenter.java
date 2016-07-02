package com.yandex.testapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import com.yandex.testapp.service.GeoService;
import com.yandex.testapp.util.ServiceUtils;

public class HomePresenter implements HomeContract.Presenter {

    static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
    };

    static final int GEO_SERVICE_PERMISSIONS_REQUEST = 100;
    static final long MAX_INTERVAL_SIZE_SECONDS = 100000;

    private HomeContract.View mHomeView;
    private Context mContext;

    public HomePresenter(Context context, HomeContract.View homeView) {
        mContext = context;
        mHomeView = homeView;
        mHomeView.setPresenter(this);
    }

    @Override
    public void serviceStateChanged(boolean isSwitchOn) {
        boolean isServiceOn = ServiceUtils.isGeoServiceRunning(mContext);
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
        if (ServiceUtils.isGeoServiceRunning(mContext)) {
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
        String textTimeInterval = mHomeView.getTimeInterval();
        double timeInterval;
        try {
            timeInterval = Double.parseDouble(textTimeInterval);
        } catch (NumberFormatException e) {
            timeInterval = 0;
        }
        if (timeInterval > MAX_INTERVAL_SIZE_SECONDS) {
            timeInterval = 0;
        }

        Intent intent = new Intent(mContext, GeoService.class);
        if (timeInterval > 0) {
            intent.putExtra("time_interval", (long)(timeInterval * 1000));
        }
        Context context = mContext.getApplicationContext();
        context.startService(intent);
    }

    private void stopGeoService() {
        Intent intent = new Intent(mContext, GeoService.class);
        mContext.stopService(intent);
    }

}
