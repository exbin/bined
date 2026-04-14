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
package org.exbin.bined.jaguif.theme.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.theme.BinedThemeModule;
import org.exbin.jaguif.context.api.ActiveContextProvider;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;

/**
 * Layout profiles settings applier.
 */
@ParametersAreNonnullByDefault
public class CodeAreaLayoutSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "codeAreaLayoutApplier";

    @Override
    public void applySettings(ActiveContextProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextComponent instance = contextProvider.getActiveState(ContextComponent.class);
        if (!(instance instanceof BinaryDataComponent)) {
            return;
        }

        CodeAreaCore codeArea = ((BinaryDataComponent) instance).getCodeArea();
        if (!(codeArea instanceof SectCodeArea)) {
            return;
        }

        CodeAreaLayoutOptions options = settingsProvider.getSettingsOptions(CodeAreaLayoutOptions.class);
        SectionCodeAreaLayoutProfile profile;
        int selectedProfile = options.getSelectedProfile();
        if (selectedProfile >= 0) {
            profile = options.getLayoutProfile(selectedProfile);
        } else {
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            profile = binedThemeModule.getDefaultLayoutProfile();
        }

        ((SectCodeArea) codeArea).setLayoutProfile(profile);
    }
}
