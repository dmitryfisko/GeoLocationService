package com.yandex.testapp.ui.home;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.yandex.testapp.service.GeoService;

public class HomePresenter implements HomeContract.Presenter {

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
            startGeoService();
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

    private void startGeoService() {
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
