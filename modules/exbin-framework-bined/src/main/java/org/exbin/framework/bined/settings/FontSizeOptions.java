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
package org.exbin.framework.bined.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Font size settings options.
 */
@ParametersAreNonnullByDefault
public class FontSizeOptions implements SettingsOptions {

    public static final String KEY_FONT_SIZE = "fontSize";
    public static final int DEFAULT_FONT_SIZE = 12;
    public static final int MIN_FONT_SIZE = 6;
    public static final int MAX_FONT_SIZE = 72;

    private final OptionsStorage storage;

    public FontSizeOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public int getFontSize() {
        int fontSize = storage.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        // Validate range
        if (fontSize < MIN_FONT_SIZE) {
            fontSize = MIN_FONT_SIZE;
        } else if (fontSize > MAX_FONT_SIZE) {
            fontSize = MAX_FONT_SIZE;
        }
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        // Validate range before saving
        if (fontSize < MIN_FONT_SIZE) {
            fontSize = MIN_FONT_SIZE;
        } else if (fontSize > MAX_FONT_SIZE) {
            fontSize = MAX_FONT_SIZE;
        }
        storage.putInt(KEY_FONT_SIZE, fontSize);
        storage.flush();
    }

    @Override
    public void copyTo(SettingsOptions options) {
        FontSizeOptions target = (FontSizeOptions) options;
        target.setFontSize(getFontSize());
    }
}
