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
package org.exbin.framework.bined.inspector.table;

import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary editor data table inspector module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedInspectorTableModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedInspectorTableModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    public BinedInspectorTableModule() {
    }

    public void initEditorProvider(EditorProviderVariant variant) {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
//        fileManager.addPainterColorModifier(basicValuesColorModifier);
//        fileManager.addActionStatusUpdateListener(this::updateActionStatus);
//        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
//            @Nonnull
//            @Override
//            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
//                BinEdComponentInspector binEdComponentInspector = new BinEdComponentInspector();
//                binEdComponentInspector.setBasicValuesColorModifier(basicValuesColorModifier);
//                return Optional.of(binEdComponentInspector);
//            }
//
//            @Override
//            public void onPopupMenuCreation(JPopupMenu popupMenu, ExtCodeArea codeArea, String menuPostfix, BinedModule.PopupMenuVariant variant, int x, int y) {
//            }
//        });
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedInspectorTableModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
