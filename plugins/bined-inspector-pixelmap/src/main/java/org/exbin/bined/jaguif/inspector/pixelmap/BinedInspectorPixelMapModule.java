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
package org.exbin.bined.jaguif.inspector.pixelmap;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.PluginModule;
import org.exbin.bined.jaguif.inspector.BinedInspectorModule;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.inspector.BinEdInspectorManager;

/**
 * Binary editor data pixel map inspector plugin.
 */
@ParametersAreNonnullByDefault
public class BinedInspectorPixelMapModule implements PluginModule {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorPixelMapModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public BinedInspectorPixelMapModule() {
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorPixelMapModule.class);
        }

        return resourceBundle;
    }

    @Override
    public void register() {
        BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
        BinEdInspectorManager inspectorManager = binedInspectorModule.getBinEdInspectorManager();
        inspectorManager.addInspector(new PixelMapInspectorProvider(getResourceBundle()));
    }
}
