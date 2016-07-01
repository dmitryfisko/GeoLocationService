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

package com.yandex.testapp.data.source;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.yandex.testapp.data.Coord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


public class CoordsProvider implements CoordsDataSource {

    public static String EVENT_NEW_DATA_ITEM_ADDED = "EVENT_NEW_DATA_ITEM_ADDED";
    private static CoordsProvider INSTANCE = null;

    private final CoordsDataSource mCoordsLocalDataSource;

    Map<String, Coord> mCachedCoords;

    boolean mCacheIsDirty = false;

    private CoordsProvider(@NonNull CoordsDataSource coordsLocalDataSource) {
        mCoordsLocalDataSource = coordsLocalDataSource;
    }

    public static CoordsProvider getInstance(@NonNull CoordsDataSource coordsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CoordsProvider(coordsLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getCoords(@NonNull final LoadCoordsCallback callback) {
        checkNotNull(callback);

        if (mCachedCoords != null && !mCacheIsDirty) {
            callback.onCoordsLoaded(new ArrayList<>(mCachedCoords.values()));
            return;
        }

        mCoordsLocalDataSource.getCoords(new LoadCoordsCallback() {
            @Override
            public void onCoordsLoaded(List<Coord> coords) {
                refreshCache(coords);
                callback.onCoordsLoaded(new ArrayList<>(mCachedCoords.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveCoord(Context context, Coord coord) {
        if (coord == null) {
            return;
        }

        mCoordsLocalDataSource.saveCoord(context, coord);

        if (mCachedCoords == null) {
            mCachedCoords = new LinkedHashMap<>();
        }
        Coord coordWithID = new Coord(mCachedCoords.size() + 1, coord.getLongitude(),
                coord.getLatitude(), coord.getAltitude(), coord.getTimestamp());
        mCachedCoords.put(coordWithID.getIdString(), coordWithID);

        sendMessageNewDataItem(context, coordWithID);
    }

    private void sendMessageNewDataItem(Context context, Coord coord) {
        Intent intent = new Intent(EVENT_NEW_DATA_ITEM_ADDED);
        intent.putExtra("coord", coord);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void getCoord(final long coordId, @NonNull final GetCoordCallback callback) {
        checkNotNull(coordId);
        checkNotNull(callback);

        Coord cachedCoord = getCoordWithId(coordId);

        // Respond immediately with cache if available
        if (cachedCoord != null) {
            callback.onCoordLoaded(cachedCoord);
            return;
        }

        mCoordsLocalDataSource.getCoord(coordId, new GetCoordCallback() {
            @Override
            public void onCoordLoaded(Coord coord) {
                callback.onCoordLoaded(coord);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }


    @Override
    public void deleteAllCoords() {
        mCoordsLocalDataSource.deleteAllCoords();

        if (mCachedCoords == null) {
            mCachedCoords = new LinkedHashMap<>();
        }
        mCachedCoords.clear();
    }

    @Override
    public void deleteCoord(long coordId) {
        mCoordsLocalDataSource.deleteCoord(checkNotNull(coordId));
        mCachedCoords.remove(String.valueOf(coordId));
    }

    private void refreshCache(List<Coord> coords) {
        if (mCachedCoords == null) {
            mCachedCoords = new LinkedHashMap<>();
        }
        mCachedCoords.clear();
        for (Coord coord : coords) {
            mCachedCoords.put(coord.getIdString(), coord);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Coord getCoordWithId(long id) {
        checkNotNull(id);
        if (mCachedCoords == null || mCachedCoords.isEmpty()) {
            return null;
        } else {
            return mCachedCoords.get(String.valueOf(id));
        }
    }
}
