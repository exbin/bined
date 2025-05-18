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
package org.exbin.framework.bined.editor.options;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.basic.TabKeyHandlingMode;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Binary editor options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorOptions implements OptionsData {

    public static final String KEY_FILE_HANDLING_MODE = "fileHandlingMode";
    public static final String KEY_ENTER_KEY_HANDLING_MODE = "enterKeyHandlingMode";
    public static final String KEY_TAB_KEY_HANDLING_MODE = "tabKeyHandlingMode";

    private final OptionsStorage storage;

    public EditorOptions(OptionsStorage optionsStorage) {
        this.storage = optionsStorage;
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        FileHandlingMode defaultFileHandlingMode = FileHandlingMode.DELTA;
        try {
            return FileHandlingMode.valueOf(storage.get(KEY_FILE_HANDLING_MODE, defaultFileHandlingMode.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EditorOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultFileHandlingMode;
        }
    }

    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        storage.put(KEY_FILE_HANDLING_MODE, fileHandlingMode.name());
    }

    @Nonnull
    public EnterKeyHandlingMode getEnterKeyHandlingMode() {
        EnterKeyHandlingMode defaultValue = EnterKeyHandlingMode.PLATFORM_SPECIFIC;
        try {
            return EnterKeyHandlingMode.valueOf(storage.get(KEY_ENTER_KEY_HANDLING_MODE, defaultValue.name()));
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    public void setEnterKeyHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode) {
        storage.put(KEY_ENTER_KEY_HANDLING_MODE, enterKeyHandlingMode.name());
    }

    @Nonnull
    public TabKeyHandlingMode getTabKeyHandlingMode() {
        TabKeyHandlingMode defaultValue = TabKeyHandlingMode.PLATFORM_SPECIFIC;
        try {
            return TabKeyHandlingMode.valueOf(storage.get(KEY_TAB_KEY_HANDLING_MODE, defaultValue.name()));
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    public void setTabKeyHandlingMode(TabKeyHandlingMode tabKeyHandlingMode) {
        storage.put(KEY_TAB_KEY_HANDLING_MODE, tabKeyHandlingMode.name());
    }

    @Override
    public void copyTo(OptionsData options) {
        EditorOptions with = (EditorOptions) options;
        with.setEnterKeyHandlingMode(getEnterKeyHandlingMode());
        with.setFileHandlingMode(getFileHandlingMode());
        with.setTabKeyHandlingMode(getTabKeyHandlingMode());
    }
}
