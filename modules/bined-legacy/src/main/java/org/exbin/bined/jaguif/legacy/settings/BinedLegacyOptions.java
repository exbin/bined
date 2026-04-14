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
package org.exbin.bined.jaguif.legacy.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.settings.api.SettingsOptions;

/**
 * BinEd legacy options.
 */
@ParametersAreNonnullByDefault
public class BinedLegacyOptions implements SettingsOptions {

    public static final String KEY_LEGACY_IMPORTED = "legacy.imported";

    protected final OptionsStorage storage;

    public BinedLegacyOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getLegacyImported() {
        return storage.get(KEY_LEGACY_IMPORTED, "");
    }

    public void setLegacyImported(String activatedVersion) {
        storage.put(KEY_LEGACY_IMPORTED, activatedVersion);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        BinedLegacyOptions with = (BinedLegacyOptions) options;
        with.setLegacyImported(getLegacyImported());
    }
}
