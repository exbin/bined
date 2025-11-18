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
package org.exbin.framework.bined.viewer.settings;

import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.BinEdDataComponent;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.text.encoding.settings.TextEncodingOptions;

/**
 * Binary encoding settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEncodingSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryEncoding";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        if (!(instance instanceof BinEdDataComponent)) {
            return;
        }

        TextEncodingOptions options = settingsOptionsProvider.getSettingsOptions(TextEncodingOptions.class);
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
