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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.jaguif.component.settings.CodeAreaStatusOptions;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;

/**
 * Code area status settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaStatusSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryStatus";

    @Override
    public void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        /* BinaryStatus instance = contextProvider.getActiveState(BinaryStatus.class);
        if (!(instance instanceof BinaryStatus)) {
            return;
        }

        CodeAreaStatusOptions options = settingsProvider.getSettingsOptions(CodeAreaStatusOptions.class);
        BinaryStatusPanel binaryStatusPanel = ((BinaryStatus) instance).getBinaryStatusPanel();
        if (binaryStatusPanel != null) {
            binaryStatusPanel.loadFromOptions(options);
        } */
        // TODO binaryStatusPanel.setStatusOptions(options);
    }
}
