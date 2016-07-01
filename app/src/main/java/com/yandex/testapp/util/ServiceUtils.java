package com.yandex.testapp.util;

import android.app.ActivityManager;
import android.content.Context;

import com.yandex.testapp.service.GeoService;

public class ServiceUtils {

    public static boolean isGeoServiceRunning(Context context) {
        String serviceName = GeoService.class.getName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
