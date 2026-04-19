/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.viewer.settings;

import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.text.font.settings.TextFontOptions;

/**
 * Binary font settings applier.
 */
@ParametersAreNonnullByDefault
public class BinaryFontSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryFont";

    @Override
    public void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        if (!(instance instanceof BinEdDataComponent)) {
            return;
        }

        TextFontOptions options = settingsProvider.getSettingsOptions(TextFontOptions.class);
        try {
            ((BinEdDataComponent) instance).setCurrentFont(options.isUseDefaultFont() ? CodeAreaOptions.DEFAULT_FONT : options.getFont(CodeAreaOptions.DEFAULT_FONT));
        } catch (UnsupportedCharsetException ex) {
            // ignore
        }
    }
}
