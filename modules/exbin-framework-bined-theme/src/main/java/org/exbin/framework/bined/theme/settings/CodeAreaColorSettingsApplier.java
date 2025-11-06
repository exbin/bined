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
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Color profiles settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaColorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "codeAreaColorApplier";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        if (!(instance instanceof BinEdFileHandler)) {
            return;
        }

        CodeAreaColorOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaColorOptions.class);

        int selectedProfile = options.getSelectedProfile();
        if (selectedProfile >= 0) {
            SectCodeArea codeArea = ((BinEdFileHandler) instance).getCodeArea();
            SectionCodeAreaColorProfile profile = options.getColorsProfile(selectedProfile);
            codeArea.setColorsProfile(profile);
        }
    }
}
