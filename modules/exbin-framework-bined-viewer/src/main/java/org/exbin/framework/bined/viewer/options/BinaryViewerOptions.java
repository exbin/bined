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
package org.exbin.framework.bined.viewer.options;

import org.exbin.framework.bined.options.StatusOptions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.text.encoding.options.TextEncodingOptions;
import org.exbin.framework.text.font.options.TextFontOptions;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Binary editor options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryViewerOptions {

    private final OptionsStorage storage;

    private final StatusOptions statusOptions;
    private final CodeAreaOptions codeAreaOptions;
    private final TextEncodingOptions encodingOptions;
    private final TextFontOptions fontOptions;

    public BinaryViewerOptions(OptionsStorage storage) {
        this.storage = storage;

        statusOptions = new StatusOptions(storage);
        codeAreaOptions = new CodeAreaOptions(storage);
        encodingOptions = new TextEncodingOptions(storage);
        fontOptions = new TextFontOptions(storage);
    }

    @Nonnull
    public OptionsStorage getPreferences() {
        return storage;
    }

    @Nonnull
    public StatusOptions getStatusOptions() {
        return statusOptions;
    }

    @Nonnull
    public CodeAreaOptions getCodeAreaOptions() {
        return codeAreaOptions;
    }

    @Nonnull
    public TextEncodingOptions getEncodingOptions() {
        return encodingOptions;
    }

    @Nonnull
    public TextFontOptions getFontOptions() {
        return fontOptions;
    }
}
