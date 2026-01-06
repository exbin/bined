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
    public static final String KEY_INSPECTOR_PREFIX = "inspector.";
    public static final String KEY_INSPECTOR_HIDE_POSTFIX = ".hide";
    public static final String KEY_INSPECTOR_POSITION_POSTFIX = ".position";

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

    public boolean isInspectorHidden(String inspectorId) {
        return storage.getBoolean(KEY_INSPECTOR_PREFIX + inspectorId + KEY_INSPECTOR_HIDE_POSTFIX, false);
    }

    public void setInspectorHidden(String inspectorId, boolean hidden) {
        storage.putBoolean(KEY_INSPECTOR_PREFIX + inspectorId + KEY_INSPECTOR_HIDE_POSTFIX, hidden);
    }

    public int getInspectorPosition(String inspectorId) {
        return storage.getInt(KEY_INSPECTOR_PREFIX + inspectorId + KEY_INSPECTOR_POSITION_POSTFIX, -1);
    }

    public void setInspectorPosition(String inspectorId, int position) {
        storage.putInt(KEY_INSPECTOR_PREFIX + inspectorId + KEY_INSPECTOR_POSITION_POSTFIX, position);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        DataInspectorOptions with = (DataInspectorOptions) options;
        with.setShowParsingPanel(isShowParsingPanel());
    }
}
