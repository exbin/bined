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

import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.theme.settings.BinaryThemeOptions;
import org.exbin.framework.options.api.OptionsStorage;

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
            BinedModule binedModule = App.getModule(BinedModule.class);
            binedThemeManager.setEditorProvider(binedModule.getEditorProvider());
        }
        return binedThemeManager;
    }

    public void registerSettings() {
        getThemeManager().registerSettings();
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                return Optional.of(new BinEdComponentPanel.BinEdComponentExtension() {
                    @Override
                    public void onCreate(BinEdComponentPanel componentPanel) {
                    }

                    @Override
                    public void onInitFromOptions(OptionsStorage options) {
                        SectCodeArea codeArea = component.getCodeArea();
                        getThemeManager().applyProfileFromPreferences(codeArea, new BinaryThemeOptions(options));
                    }

                    @Override
                    public void onDataChange() {
                    }

                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onUndoHandlerChange() {
                    }
                });
            }
        });
    }

    @Nonnull
    public SectCodeArea getActiveCodeArea() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        Optional<FileHandler> activeFile = binedModule.getEditorProvider().getActiveFile();
        if (activeFile.isPresent()) {
            return ((BinEdFileHandler) activeFile.get()).getComponent().getCodeArea();
        }
        throw new IllegalStateException("No active file");
    }

    public void loadFromPreferences(OptionsStorage preferences) {
    }
}
