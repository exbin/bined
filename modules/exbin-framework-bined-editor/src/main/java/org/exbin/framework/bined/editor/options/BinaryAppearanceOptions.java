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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Binary appearance options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceOptions implements OptionsData {

    public static final String KEY_TEXT_WORD_WRAPPING = "textAppearance.wordWrap";
    public static final String KEY_MULTIFILE_MODE = "multiFileMode";

    private final OptionsStorage storage;

    public BinaryAppearanceOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    public boolean isLineWrapping() {
        return storage.getBoolean(KEY_TEXT_WORD_WRAPPING, false);
    }

    public boolean isMultiFileMode() {
        return storage.getBoolean(KEY_MULTIFILE_MODE, true);
    }

    public void setLineWrapping(boolean wrapping) {
        storage.putBoolean(KEY_TEXT_WORD_WRAPPING, wrapping);
    }

    public void setMultiFileMode(boolean mode) {
        storage.putBoolean(KEY_MULTIFILE_MODE, mode);
    }

    @Override
    public void copyTo(OptionsData options) {
        BinaryAppearanceOptions with = (BinaryAppearanceOptions) options;
        with.setLineWrapping(isLineWrapping());
        with.setMultiFileMode(isMultiFileMode());
    }
}
