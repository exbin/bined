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
package org.exbin.framework.bined.launcher.settings;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;


@ParametersAreNonnullByDefault
public class StartupOptions implements SettingsOptions {

    public static final String KEY_STARTUP_BEHAVIOR = "startup.behavior";
    public static final String KEY_SESSION_FILE_COUNT = "session.fileCount";
    public static final String KEY_SESSION_FILE_PREFIX = "session.file.";

    private final OptionsStorage storage;

    public StartupOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public StartupBehavior getStartupBehavior() {
        StartupBehavior defaultBehavior = StartupBehavior.NEW_FILE;
        try {
            return StartupBehavior.valueOf(storage.get(KEY_STARTUP_BEHAVIOR, defaultBehavior.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(StartupOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultBehavior;
        }
    }

    public void setStartupBehavior(StartupBehavior behavior) {
        storage.put(KEY_STARTUP_BEHAVIOR, behavior.name());
    }

    @Nonnull
    public List<URI> getLastSessionFiles() {
        int count = storage.getInt(KEY_SESSION_FILE_COUNT, 0);
        List<URI> fileUris = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String uriString = storage.get(KEY_SESSION_FILE_PREFIX + i, null);
            if (uriString != null) {
                try {
                    fileUris.add(new URI(uriString));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(StartupOptions.class.getName()).log(Level.WARNING,
                        "Invalid URI in session files: " + uriString, ex);
                }
            }
        }
        return fileUris;
    }

    public void setLastSessionFiles(List<URI> fileUris) {
        clearLastSessionFiles();

        if (fileUris != null && !fileUris.isEmpty()) {
            storage.putInt(KEY_SESSION_FILE_COUNT, fileUris.size());
            for (int i = 0; i < fileUris.size(); i++) {
                storage.put(KEY_SESSION_FILE_PREFIX + i, fileUris.get(i).toString());
            }
        }
    }

    public void clearLastSessionFiles() {
        int count = storage.getInt(KEY_SESSION_FILE_COUNT, 0);
        for (int i = 0; i < count; i++) {
            storage.remove(KEY_SESSION_FILE_PREFIX + i);
        }
        storage.remove(KEY_SESSION_FILE_COUNT);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        StartupOptions target = (StartupOptions) options;
        target.setStartupBehavior(getStartupBehavior());
        target.setLastSessionFiles(getLastSessionFiles());
    }

    /**
     * Startup behavior enumeration.
     */
    public enum StartupBehavior {
        /** Start with empty editor */
        START_EMPTY,
        /** Start with single new file */
        NEW_FILE,
        /** Reopen last session files */
        REOPEN_SESSION
    }
}
