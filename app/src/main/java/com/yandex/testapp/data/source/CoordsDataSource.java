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

import com.yandex.testapp.data.Coord;

import java.util.List;

public interface CoordsDataSource {

    interface LoadCoordsCallback {

        void onCoordsLoaded(List<Coord> coords);

        void onDataNotAvailable();
    }

    interface GetCoordCallback {

        void onCoordLoaded(Coord coordinate);

        void onDataNotAvailable();
    }

    void getCoords(LoadCoordsCallback callback);

    void getCoord(String taskId, GetCoordCallback callback);

    void saveCoord(Context context, Coord coordinate);

    void deleteAllCoords();

    void deleteCoord(String taskId);
}
