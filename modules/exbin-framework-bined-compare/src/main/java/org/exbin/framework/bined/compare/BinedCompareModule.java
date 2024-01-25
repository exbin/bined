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
package org.exbin.framework.bined.compare;

import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.bined.compare.action.CompareFilesAction;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Binary editor compare module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedCompareModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedCompareModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private CompareFilesAction compareFilesAction;

    public BinedCompareModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.TOOLS_MENU_ID, MODULE_ID, getCompareFilesAction(), new MenuPosition(PositionMode.TOP));
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedCompareModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public AbstractAction getCompareFilesAction() {
        if (compareFilesAction == null) {
            ensureSetup();
            compareFilesAction = new CompareFilesAction();
            compareFilesAction.setup(editorProvider, resourceBundle);
        }

        return compareFilesAction;
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
