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
package org.exbin.framework.bined.tool.content;

import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.bined.tool.content.action.ClipboardContentAction;
import org.exbin.framework.bined.tool.content.action.DragDropContentAction;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;

/**
 * Binary editor clipboard support module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedToolContentModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedToolContentModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    public BinedToolContentModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedToolContentModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (editorProvider == null) {
            getEditorProvider();
        }

        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    @Nonnull
    public ClipboardContentAction createClipboardContentAction() {
        ensureSetup();
        ClipboardContentAction clipboardContentAction = new ClipboardContentAction();
        clipboardContentAction.setup(resourceBundle);
        return clipboardContentAction;
    }

    @Nonnull
    public DragDropContentAction createDragDropContentAction() {
        ensureSetup();
        DragDropContentAction dragDropContentAction = new DragDropContentAction();
        dragDropContentAction.setup(resourceBundle);
        return dragDropContentAction;
    }

    public void registerClipboardContentMenu() {
        createClipboardContentAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.TOOLS_MENU_ID, MODULE_ID, createClipboardContentAction(), new MenuPosition(PositionMode.MIDDLE));
    }

    public void registerDragDropContentMenu() {
        createDragDropContentAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.TOOLS_MENU_ID, MODULE_ID, createDragDropContentAction(), new MenuPosition(PositionMode.MIDDLE));
    }
}
