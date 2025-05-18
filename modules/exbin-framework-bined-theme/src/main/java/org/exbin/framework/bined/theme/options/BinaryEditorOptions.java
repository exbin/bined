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
package org.exbin.framework.bined.theme.options;

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
public class BinaryEditorOptions {

    public static final String ENCODING_UTF8 = "UTF-8";

    private final OptionsStorage storage;

    private final CodeAreaOptions codeAreaOptions;
    private final TextEncodingOptions encodingOptions;
    private final TextFontOptions fontOptions;
    private final CodeAreaLayoutOptions layoutOptions;
    private final CodeAreaThemeOptions themeOptions;
    private final CodeAreaColorOptions colorOptions;

    public BinaryEditorOptions(OptionsStorage storage) {
        this.storage = storage;

        codeAreaOptions = new CodeAreaOptions(storage);
        encodingOptions = new TextEncodingOptions(storage);
        fontOptions = new TextFontOptions(storage);
        layoutOptions = new CodeAreaLayoutOptions(storage);
        themeOptions = new CodeAreaThemeOptions(storage);
        colorOptions = new CodeAreaColorOptions(storage);
    }

    @Nonnull
    public OptionsStorage getPreferences() {
        return storage;
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

    @Nonnull
    public CodeAreaLayoutOptions getLayoutOptions() {
        return layoutOptions;
    }

    @Nonnull
    public CodeAreaThemeOptions getThemeOptions() {
        return themeOptions;
    }

    @Nonnull
    public CodeAreaColorOptions getColorOptions() {
        return colorOptions;
    }
}
