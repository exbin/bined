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
package org.exbin.framework.bined.inspector.table.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Table inspector options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TableInspectorOptions implements SettingsOptions {

    public static final String KEY_ROWS_COUNT = "table_inspector.rowCount";
    public static final String KEY_ROW_PREFIX = "table_inspector.row.";
    public static final String KEY_ROW_TYPE = "type";

    private final OptionsStorage storage;

    public TableInspectorOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public int getRowsCount() {
        return storage.getInt(KEY_ROWS_COUNT, 0);
    }

    public void setRowsCount(int rowsCount) {
        storage.putInt(KEY_ROWS_COUNT, rowsCount);
    }

    @Nonnull
    public String getRowType(int rowIndex) {
        return storage.get(KEY_ROW_PREFIX + rowIndex + "." + KEY_ROW_TYPE, "");
    }

    public void setRowType(int rowIndex, String rowType) {
        storage.put(KEY_ROW_PREFIX + rowIndex + "." + KEY_ROW_TYPE, rowType);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        TableInspectorOptions with = (TableInspectorOptions) options;
        int rowsCount = getRowsCount();
        with.setRowsCount(rowsCount);
        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            with.setRowType(rowIndex, getRowType(rowIndex));
        }
    }
}
