/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yandex.testapp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.yandex.testapp.data.Coord;
import com.yandex.testapp.data.source.CoordsDataSource;
import com.yandex.testapp.data.source.local.CoordsPersistenceContract.CoordEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class CoordsLocalDataSource implements CoordsDataSource {

    private static CoordsLocalDataSource INSTANCE;
    private static final String[] PROJECTION = {
        CoordEntry._ID,
        CoordEntry.COLUMN_NAME_LONGITUDE,
        CoordEntry.COLUMN_NAME_LATITUDE,
        CoordEntry.COLUMN_NAME_ALTITUDE,
        CoordEntry.COLUMN_NAME_TIMESTAMP,
    };

    private CoordsDbHelper mDbHelper;

    private CoordsLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new CoordsDbHelper(context);
    }

    public static CoordsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CoordsLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getCoords(@NonNull LoadCoordsCallback callback) {
        List<Coord> coords = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor c = db.query(CoordEntry.TABLE_NAME, PROJECTION, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                Coord coord = readCoord(c);
                coords.add(coord);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (coords.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onCoordsLoaded(coords);
        }

    }

    @Override
    public void getCoord(long coordId, GetCoordCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = CoordEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(coordId) };

        Cursor c = db.query(
                CoordEntry.TABLE_NAME, PROJECTION, selection, selectionArgs, null, null, null);

        Coord coord = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            coord = readCoord(c);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (coord != null) {
            callback.onCoordLoaded(coord);
        } else {
            callback.onDataNotAvailable();
        }
    }

    private Coord readCoord(Cursor c) {
        long itemId =
                c.getLong(c.getColumnIndexOrThrow(CoordEntry._ID));
        double longitude =
                c.getDouble(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_LONGITUDE));
        double latitude =
                c.getDouble(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_LATITUDE));
        double altitude =
                c.getDouble(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_ALTITUDE));
        long time =
                c.getLong(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_TIMESTAMP));
        return new Coord(itemId, longitude, latitude, altitude, time);
    }

    @Override
    public void saveCoord(Context context, Coord coord) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CoordEntry.COLUMN_NAME_LONGITUDE, coord.getLongitude());
        values.put(CoordEntry.COLUMN_NAME_LATITUDE, coord.getLatitude());
        values.put(CoordEntry.COLUMN_NAME_ALTITUDE, coord.getAltitude());
        values.put(CoordEntry.COLUMN_NAME_TIMESTAMP, coord.getTimestamp());

        db.insert(CoordEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void deleteAllCoords() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(CoordEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteCoord(long coordId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = CoordEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(coordId) };

        db.delete(CoordEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
