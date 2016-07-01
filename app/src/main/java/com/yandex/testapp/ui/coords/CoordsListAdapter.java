package com.yandex.testapp.ui.coords;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yandex.testapp.R;
import com.yandex.testapp.data.Coord;
import com.yandex.testapp.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class CoordsListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Coord> mCoords;


    public CoordsListAdapter(Activity activity) {
        mInflater = LayoutInflater.from(activity);
        mCoords = new ArrayList<>();
    }

    public void replaceData(List<Coord> coords) {
        mCoords = new ArrayList<>(coords);
        notifyDataSetChanged();
    }

    public void addItem(Coord coord) {
        mCoords.add(coord);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCoords.size();
    }

    @Override
    public Coord getItem(int position) {
        return mCoords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        TextView longitudeText;
        TextView latitudeText;
        TextView altitudeText;
        TextView timestampText;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Coord coord = getItem(position);
        ViewHolder holder;

        if(view == null || view.getTag() == null) {
            view = mInflater.inflate(R.layout.coords_fragment_list_item, parent, false);
            holder = new ViewHolder();

            holder.longitudeText = (TextView) view.findViewById(R.id.longitude);
            holder.latitudeText = (TextView) view.findViewById(R.id.latitude);
            holder.altitudeText = (TextView) view.findViewById(R.id.altitude);
            holder.timestampText = (TextView) view.findViewById(R.id.timestamp);

            fillHolder(holder, coord);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            fillHolder(holder, coord);
        }

        return view;
    }

    private void fillHolder(ViewHolder holder, Coord coord) {
        double latitude = coord.getLatitude();
        double longitude = coord.getLongitude();
        double altitude = coord.getAltitude();
        long timestamp = coord.getTimestamp();
        holder.longitudeText.setText(String.valueOf(latitude));
        holder.latitudeText.setText(String.valueOf(longitude));
        holder.altitudeText.setText(String.valueOf(altitude));
        holder.timestampText.setText(ActivityUtils.getReadableDate(timestamp));
    }


}
