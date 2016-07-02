package com.yandex.testapp.ui.coords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yandex.testapp.data.Coord;

public class CoordsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (CoordsFragment.INSTANCE != null) {
            CoordsFragment coordsFragment = CoordsFragment.getInstance();

            Coord coord = intent.getParcelableExtra("coord");
            coordsFragment.eventDataNewItemAdded(coord);
        }
    }
}