package com.yandex.testapp.ui.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.yandex.testapp.R;

public class HomeFragment extends Fragment implements HomeContract.View, CompoundButton.OnCheckedChangeListener {

    private HomeContract.Presenter mPresenter;
    private CompoundButton mServiceStateButton;
    private EditText mTimeIntervalEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        mTimeIntervalEdit = (EditText) root.findViewById(R.id.time_interval_edit_text);
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
    public String getTimeInterval() {
        return mTimeIntervalEdit.getText().toString();
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
