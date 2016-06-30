package com.yandex.testapp.data;

import com.google.common.base.Objects;

/**
 * Immutable model class for a Coord.
 */
public final class Coord {

    private final String mId;
    private final double mLongitude;
    private final double mLatitude;
    private final double mAltitude;


    public Coord(double longitude, double latitude, double altitude) {
        mId = "";
        mLongitude = longitude;
        mLatitude = latitude;
        mAltitude = altitude;
    }

    public Coord(String itemId, double longitude, double latitude, double altitude) {
        mId = itemId;
        mLongitude = longitude;
        mLatitude = latitude;
        mAltitude = altitude;
    }

    public String getId() {
        return mId;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Objects.equal(mId, coord.mId) &&
                Objects.equal(mLongitude, coord.mLongitude) &&
                Objects.equal(mLatitude, coord.mLatitude) &&
                Objects.equal(mAltitude, coord.mAltitude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mLongitude, mLatitude);
    }

    @Override
    public String toString() {
        return "Coordinate " + mLongitude + ", " + mLatitude;
    }
}
