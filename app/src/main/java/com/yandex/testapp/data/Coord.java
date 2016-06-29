package com.yandex.testapp.data;

import com.google.common.base.Objects;;

/**
 * Immutable model class for a Coord.
 */
public final class Coord {
    private final String mId;

    private final double mLongitude;

    private final double mLatitude;

    public Coord(String itemId, double longitude, double latitude) {
        mId = itemId;
        mLongitude = longitude;
        mLatitude = latitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Objects.equal(mId, coord.mId) &&
                Objects.equal(mLongitude, coord.mLongitude) &&
                Objects.equal(mLatitude, coord.mLatitude);
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
