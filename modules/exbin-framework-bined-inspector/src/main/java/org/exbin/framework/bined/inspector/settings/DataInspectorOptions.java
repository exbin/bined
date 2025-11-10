/*
 * Copyright (C) ExBin Project
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
package org.exbin.framework.bined.inspector.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Data inspector options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorOptions implements SettingsOptions {

    public static final String KEY_SHOW_PARSING_PANEL = "showValuesPanel";

    private final OptionsStorage storage;

    public DataInspectorOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isShowParsingPanel() {
        return storage.getBoolean(KEY_SHOW_PARSING_PANEL, true);
    }

    public void setShowParsingPanel(boolean show) {
        storage.putBoolean(KEY_SHOW_PARSING_PANEL, show);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        DataInspectorOptions with = (DataInspectorOptions) options;
        with.setShowParsingPanel(isShowParsingPanel());
    }
}
