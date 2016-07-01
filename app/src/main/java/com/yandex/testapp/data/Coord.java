package com.yandex.testapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.yandex.testapp.util.ActivityUtils;

/**
 * Immutable model class for a Coord.
 */
public final class Coord implements Parcelable {

    private final long mId;
    private final double mLongitude;
    private final double mLatitude;
    private final double mAltitude;
    private final long mTimestamp;

    public Coord(long itemId, double longitude, double latitude, double altitude, long timestamp) {
        mId = itemId;
        mLongitude = longitude;
        mLatitude = latitude;
        mAltitude = altitude;
        mTimestamp = timestamp;
    }

    public Coord(double longitude, double latitude, double altitude, long timestamp) {
        this(0, longitude, latitude, altitude, timestamp);
    }

    public Coord(Parcel parcel) {
        mId = parcel.readLong();
        mLongitude = parcel.readDouble();
        mLatitude = parcel.readDouble();
        mAltitude = parcel.readDouble();
        mTimestamp = parcel.readLong();
    }

    public Coord changeTimestamp(long timestamp) {
        return new Coord(mId, mLongitude, mLatitude,
                mAltitude, timestamp);
    }

    public long getId() {
        return mId;
    }

    public String getIdString() {
        return String.valueOf(mId);
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

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coord coord = (Coord) o;
        return Objects.equal(mId, coord.mId) &&
                Objects.equal(mLongitude, coord.mLongitude) &&
                Objects.equal(mLatitude, coord.mLatitude) &&
                Objects.equal(mAltitude, coord.mAltitude) &&
                Objects.equal(mTimestamp, coord.mTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mLongitude, mLatitude,
                mAltitude, mTimestamp);
    }

    @Override
    public String toString() {
        return "Coordinate " + mLongitude + ", " +
                mLatitude + ", " + mAltitude + ", " +
                ActivityUtils.getReadableDate(mTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeDouble(mLongitude);
        parcel.writeDouble(mLatitude);
        parcel.writeDouble(mAltitude);
        parcel.writeLong(mTimestamp);
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
