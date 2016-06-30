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

package com.yandex.testapp.ui.home;

import com.yandex.testapp.ui.BasePresenter;
import com.yandex.testapp.ui.BaseView;

public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void setSwitchState(boolean isOn);
    }

    interface Presenter extends BasePresenter {

        void serviceStateChanged(boolean isSwitchOn);

        void startGeoServiceManually();
    }
}
