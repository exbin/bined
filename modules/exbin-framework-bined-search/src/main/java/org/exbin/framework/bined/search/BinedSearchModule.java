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
package org.exbin.framework.bined.search;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.search.action.FindReplaceActions;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary editor search module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedSearchModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(BinedSearchModule.class);

    public static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    public static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private EditorProvider editorProvider;

    private FindReplaceActions findReplaceActions;

    public BinedSearchModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    public void initEditorProvider(EditorProviderVariant variant) {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;

        BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addActionStatusUpdateListener(this::updateActionStatus);
        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                return Optional.of(new BinEdComponentSearch());
            }
        });
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            ensureSetup();
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.setup(application, editorProvider, resourceBundle);
        }

        return findReplaceActions;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditFindAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditFindAgainAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, findReplaceActions.getEditReplaceAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditFindPopupMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, getFindReplaceActions().getEditFindAction(), new MenuPosition(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, getFindReplaceActions().getEditReplaceAction(), new MenuPosition(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
    }
    
    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerToolBarGroup(FrameModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerToolBarItem(FrameModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, findReplaceActions.getEditFindAction(), new ToolBarPosition(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        if (findReplaceActions != null) {
            findReplaceActions.updateForActiveFile();
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedSearchModule.class);
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
