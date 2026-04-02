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
package org.exbin.bined.jaguif.viewer.settings;

import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.BinEdDataComponent;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;

/**
 * Binary encoding settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEncodingSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryEncoding";

    @Override
    public void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        if (!(instance instanceof BinEdDataComponent)) {
            return;
        }

        TextEncodingOptions options = settingsProvider.getSettingsOptions(TextEncodingOptions.class);
        try {
            ((BinEdDataComponent) instance).setEncodings(options.getEncodings());
        } catch (UnsupportedCharsetException ex) {
            // ignore
        }

        String encoding = options.getSelectedEncoding();
        if (!encoding.isEmpty()) {
            try {
                ((BinEdDataComponent) instance).setEncoding(encoding);
            } catch (UnsupportedCharsetException ex) {
                // ignore
            }
        }
    }
}
