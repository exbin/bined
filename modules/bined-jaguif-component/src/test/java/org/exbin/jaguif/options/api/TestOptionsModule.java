/*
 * Copyright (C) ExBin Project, https://exbin.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.jaguif.options.api;

import java.io.InputStream;
import org.jspecify.annotations.NullMarked;

/**
 * Test implementation of preferences module.
 */
@NullMarked
public class TestOptionsModule implements OptionsModuleApi {

    private final OptionsStorage appPreferences = new EmptyOptionsStorage();

    @Override
    public void setupAppOptions(Class clazz) {
    }

    @Override
    public void setupAppOptions(java.util.prefs.Preferences preferences) {
    }

    @Override
    public void setupAppOptions() {
    }

    @Override
    public OptionsStorage getAppOptions() {
        return appPreferences;
    }

    @Override
    public OptionsStorage createMemoryStorage() {
        return new EmptyOptionsStorage();
    }

    @Override
    public OptionsStorage createStreamPreferencesStorage(InputStream inputStream) {
        return new EmptyOptionsStorage();
    }
}
