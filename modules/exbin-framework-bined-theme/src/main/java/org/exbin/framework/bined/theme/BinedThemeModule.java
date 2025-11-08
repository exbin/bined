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
package org.exbin.framework.bined.theme;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary data editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedThemeModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedThemeModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private BinedThemeManager binedThemeManager;

    public BinedThemeModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedThemeModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public BinedThemeManager getThemeManager() {
        if (binedThemeManager == null) {
            binedThemeManager = new BinedThemeManager();
            // Possibly try to optimize this later
            binedThemeManager.loadDefaults(new SectCodeArea());
        }
        return binedThemeManager;
    }

    public void registerSettings() {
        getThemeManager().registerSettings();
    }
}
