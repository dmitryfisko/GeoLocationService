package com.yandex.testapp.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.base.Objects;

/**
 * Immutable model class for a Coord.
 */
public final class Coord implements Parcelable {

    private final String mId;
    private final double mLongitude;
    private final double mLatitude;
    private final double mAltitude;

    public Coord(String itemId, double longitude, double latitude, double altitude) {
        mId = itemId;
        mLongitude = longitude;
        mLatitude = latitude;
        mAltitude = altitude;
    }

    public Coord(double longitude, double latitude, double altitude) {
        this("", longitude, latitude, altitude);
    }

    public Coord(Parcel parcel) {
        mId = parcel.readString();
        mLongitude = parcel.readDouble();
        mLatitude = parcel.readDouble();
        mAltitude = parcel.readDouble();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeDouble(mLongitude);
        parcel.writeDouble(mLatitude);
        parcel.writeDouble(mAltitude);
    }

    public static final Parcelable.Creator<Coord> CREATOR = new Parcelable.Creator<Coord>() {
        public Coord createFromParcel(Parcel in) {
            return new Coord(in);
        }

        public Coord[] newArray(int size) {
            return new Coord[size];
        }
    };
}
