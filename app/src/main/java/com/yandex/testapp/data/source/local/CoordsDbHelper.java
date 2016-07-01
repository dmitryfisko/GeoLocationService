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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yandex.testapp.data.source.local.CoordsPersistenceContract.CoordEntry;

public class CoordsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Coordinates.db";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CoordEntry.TABLE_NAME + " (" +
                    CoordEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    CoordEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
                    CoordEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
                    CoordEntry.COLUMN_NAME_ALTITUDE + DOUBLE_TYPE + COMMA_SEP +
                    CoordEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE +
            " )";

    public CoordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}
