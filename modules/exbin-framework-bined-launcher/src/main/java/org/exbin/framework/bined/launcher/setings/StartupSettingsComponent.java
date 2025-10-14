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
package org.exbin.framework.bined.launcher.setings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.launcher.setings.StartupOptions;
import org.exbin.framework.bined.launcher.setings.gui.StartupSettingsPanel;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;


@ParametersAreNonnullByDefault
public class StartupSettingsComponent implements SettingsComponentProvider<StartupOptions> {

    public static final String PAGE_ID = "startup";

    @Nonnull
    @Override
    public SettingsComponent<StartupOptions> createComponent() {
        return new StartupSettingsPanel();
    }

    /* @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(StartupOptionsPanel.class);
    }

    @Nonnull
    @Override
    public StartupOptions createOptions() {
        return new StartupOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, StartupOptions options) {
        new StartupOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, StartupOptions options) {
        options.copyTo(new StartupOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(StartupOptions options) {
    } */
}
