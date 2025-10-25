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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.text.encoding.settings.TextEncodingOptions;

/**
 * Text encoding settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "textEncoding";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        TextEncodingOptions options = settingsOptionsProvider.getSettingsOptions(TextEncodingOptions.class);
//        encodingsHandler.setSelectedEncoding(options.getSelectedEncoding());
//        encodingsHandler.setEncodings(options.getEncodings());
    }
}
