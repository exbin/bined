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
package org.exbin.framework.bined.theme.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.bined.theme.BinedThemeModule;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Theme profiles settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaThemeSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "codeAreaThemeApplier";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        if (!(instance instanceof BinaryDataComponent)) {
            return;
        }

        CodeAreaCore codeArea = ((BinaryDataComponent) instance).getCodeArea();
        if (!(codeArea instanceof SectCodeArea)) {
            return;
        }

        CodeAreaThemeOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaThemeOptions.class);
        SectionCodeAreaThemeProfile profile;
        int selectedProfile = options.getSelectedProfile();
        if (selectedProfile < 0) {
            profile = options.getThemeProfile(selectedProfile);
        } else {
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            profile = binedThemeModule.getDefaultThemeProfile();
        }

        ((SectCodeArea) codeArea).setThemeProfile(profile);
    }
}
