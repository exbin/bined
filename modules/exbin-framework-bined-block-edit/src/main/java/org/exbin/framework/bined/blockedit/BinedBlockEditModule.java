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
package org.exbin.framework.bined.blockedit;

import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.blockedit.action.InsertDataAction;
import org.exbin.framework.bined.blockedit.action.ModifyDataAction;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;

/**
 * Binary data editor block editation operations module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedBlockEditModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(BinedBlockEditModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private EditorProvider editorProvider;

    private InsertDataAction insertDataAction;
    private ModifyDataAction modifyDataAction;

    public BinedBlockEditModule() {
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
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    private AbstractAction getInsertDataAction() {
        if (insertDataAction == null) {
            ensureSetup();
            insertDataAction = new InsertDataAction();
            insertDataAction.setup(application, resourceBundle);

            insertDataAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
                @Override
                public boolean shouldCreate(String menuId) {
                    BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
                    BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                    BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                    return menuVariant != BinedModule.PopupMenuVariant.BASIC && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
                }

                @Override
                public void onCreate(JMenuItem menuItem, String menuId) {
                }
            });
        }

        return insertDataAction;
    }

    @Nonnull
    private AbstractAction getModifyDataAction() {
        if (modifyDataAction == null) {
            ensureSetup();
            modifyDataAction = new ModifyDataAction();
            modifyDataAction.setup(application, resourceBundle);

            modifyDataAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
                @Override
                public boolean shouldCreate(String menuId) {
                    BinedModule binedModule = application.getModuleRepository().getModuleByInterface(BinedModule.class);
                    BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                    BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                    return menuVariant != BinedModule.PopupMenuVariant.BASIC && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
                }

                @Override
                public void onCreate(JMenuItem menuItem, String menuId) {
                }
            });
        }

        return modifyDataAction;
    }

    public void registerBlockEditActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getInsertDataAction(), new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(FrameModuleApi.EDIT_MENU_ID, MODULE_ID, getModifyDataAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerBlockEditPopupMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, getInsertDataAction(), new MenuPosition(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
        actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID, getModifyDataAction(), new MenuPosition(BinedModule.CODE_AREA_POPUP_OPERATION_GROUP_ID));
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        if (insertDataAction != null) {
            insertDataAction.updateForActiveCodeArea(codeArea);
        }
        if (modifyDataAction != null) {
            modifyDataAction.updateForActiveCodeArea(codeArea);
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedBlockEditModule.class);
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
