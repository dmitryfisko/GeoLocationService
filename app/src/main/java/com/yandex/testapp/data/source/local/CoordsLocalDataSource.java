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
import com.yandex.testapp.data.source.local.CoordsPersistenceContract.CoordEntry;
import com.yandex.testapp.data.source.CoordsDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class CoordsLocalDataSource implements CoordsDataSource {

    private static CoordsLocalDataSource INSTANCE;

    private CoordsDbHelper mDbHelper;

    // Prevent direct instantiation.
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
        List<Coord> coords = new ArrayList<Coord>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CoordEntry.COLUMN_NAME_ENTRY_ID,
                CoordEntry.COLUMN_NAME_LONGITUDE,
                CoordEntry.COLUMN_NAME_LATITUDE,
        };

        Cursor c = db.query(CoordEntry.TABLE_NAME, projection, null, null, null, null, null);

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
    public void getCoord(String taskId, GetCoordCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CoordEntry.COLUMN_NAME_ENTRY_ID,
                CoordEntry.COLUMN_NAME_LONGITUDE,
                CoordEntry.COLUMN_NAME_LATITUDE,
        };

        String selection = CoordEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(
                CoordEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

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
        String itemId =
                c.getString(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_ENTRY_ID));
        double longitude =
                c.getDouble(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_LONGITUDE));
        double latitude =
                c.getDouble(c.getColumnIndexOrThrow(CoordEntry.COLUMN_NAME_LATITUDE));
        return new Coord(itemId, longitude, latitude);
    }

    @Override
    public void saveCoord(Coord coord) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CoordEntry.COLUMN_NAME_ENTRY_ID, coord.getId());
        values.put(CoordEntry.COLUMN_NAME_LONGITUDE, coord.getLongitude());
        values.put(CoordEntry.COLUMN_NAME_LATITUDE, coord.getLatitude());

        db.insert(CoordEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void refreshCoords() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllCoords() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(CoordEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteCoord(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = CoordEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        db.delete(CoordEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
