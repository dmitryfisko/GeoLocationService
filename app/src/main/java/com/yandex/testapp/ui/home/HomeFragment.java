package com.yandex.testapp.ui.home;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.yandex.testapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment implements HomeContract.View, CompoundButton.OnCheckedChangeListener {

    private HomeContract.Presenter mPresenter;
    private CompoundButton mServiceStateButton;


    public HomeFragment() {
        // Requires empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        mServiceStateButton = (CompoundButton) root.findViewById(R.id.geo_service_state);
        mServiceStateButton.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setSwitchState(boolean isOn) {
        mServiceStateButton.setChecked(isOn);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
        mPresenter.serviceStateChanged(isOn);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case HomePresenter.GEO_SERVICE_PERMISSIONS_REQUEST: {
                mPresenter.startGeoServiceManually();
            }
        }
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
