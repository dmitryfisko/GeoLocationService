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

    private final CoordsDataSource mTasksLocalDataSource;

    Map<String, Coord> mCachedTasks;

    boolean mCacheIsDirty = false;

    private CoordsProvider(@NonNull CoordsDataSource tasksLocalDataSource) {
        mTasksLocalDataSource = tasksLocalDataSource;
    }

    public static CoordsProvider getInstance(@NonNull CoordsDataSource coordsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CoordsProvider(coordsLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getCoords(@NonNull final LoadCoordsCallback callback) {
        checkNotNull(callback);

        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onCoordsLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        mTasksLocalDataSource.getCoords(new LoadCoordsCallback() {
            @Override
            public void onCoordsLoaded(List<Coord> coords) {
                refreshCache(coords);
                callback.onCoordsLoaded(new ArrayList<>(mCachedTasks.values()));
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

        mTasksLocalDataSource.saveCoord(context, coord);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(coord.getId(), coord);

        sendMessageNewDataItem(context, coord);
    }

    private void sendMessageNewDataItem(Context context, Coord coord) {
        Intent intent = new Intent(EVENT_NEW_DATA_ITEM_ADDED);
        intent.putExtra("coord", coord);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void getCoord(@NonNull final String taskId, @NonNull final GetCoordCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        Coord cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if (cachedTask != null) {
            callback.onCoordLoaded(cachedTask);
            return;
        }

        mTasksLocalDataSource.getCoord(taskId, new GetCoordCallback() {
            @Override
            public void onCoordLoaded(Coord task) {
                callback.onCoordLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }


    @Override
    public void deleteAllCoords() {
        mTasksLocalDataSource.deleteAllCoords();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteCoord(@NonNull String taskId) {
        mTasksLocalDataSource.deleteCoord(checkNotNull(taskId));
        mCachedTasks.remove(taskId);
    }

    private void refreshCache(List<Coord> coords) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Coord task : coords) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Coord getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }
}
