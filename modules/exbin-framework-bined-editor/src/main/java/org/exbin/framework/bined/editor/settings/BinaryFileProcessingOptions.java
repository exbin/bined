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
package org.exbin.framework.bined.editor.settings;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.FileProcessingMode;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Binary file processing options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryFileProcessingOptions implements SettingsOptions {

    public static final String KEY_FILE_PROCESSING_MODE = "fileHandlingMode";

    private final OptionsStorage storage;

    public BinaryFileProcessingOptions(OptionsStorage optionsStorage) {
        this.storage = optionsStorage;
    }

    @Nonnull
    public FileProcessingMode getFileProcessingMode() {
        FileProcessingMode defaultFileHandlingMode = FileProcessingMode.DELTA;
        try {
            return FileProcessingMode.valueOf(storage.get(KEY_FILE_PROCESSING_MODE, defaultFileHandlingMode.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BinaryFileProcessingOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultFileHandlingMode;
        }
    }

    public void setFileProcessingMode(FileProcessingMode fileProcessingMode) {
        storage.put(KEY_FILE_PROCESSING_MODE, fileProcessingMode.name());
    }

    @Override
    public void copyTo(SettingsOptions options) {
        BinaryFileProcessingOptions with = (BinaryFileProcessingOptions) options;
        with.setFileProcessingMode(getFileProcessingMode());
    }
}
