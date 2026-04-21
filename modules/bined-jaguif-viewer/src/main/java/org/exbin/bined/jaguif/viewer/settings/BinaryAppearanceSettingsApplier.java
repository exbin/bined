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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.context.api.ContextStateProvider;

/**
 * Binary appearance settings applier.
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceSettingsApplier implements SettingsApplier {
    
    public static final String APPLIER_ID = "binaryAppearance";

    @Override
    public void applySettings(ContextStateProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextDocking instance = contextProvider.getActiveState(ContextDocking.class);
        BinaryAppearanceOptions options = settingsProvider.getSettingsOptions(BinaryAppearanceOptions.class);
        // TODO
        // binaryAppearanceService.setWordWrapMode(options.isLineWrapping());
    }
}
