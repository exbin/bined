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
package org.exbin.framework.bined.inspector;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.action.ShowParsingPanelAction;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Binary editor data inspector module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedInspectorModule implements XBApplicationModule {

    public static final String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(BinedInspectorModule.class);

    private static final String VIEW_PARSING_PANEL_MENU_GROUP_ID = MODULE_ID + ".viewParsingPanelMenuGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private XBApplication application;
    private EditorProvider editorProvider;

    private ShowParsingPanelAction showParsingPanelAction;

    public BinedInspectorModule() {
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
        binedModule.addActionStatusUpdateListener(this::updateActionStatus);
        binedModule.addBinEdComponentExtension(new BinedModule.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                return Optional.of(new BinEdComponentInspector());
            }

            @Override
            public void onPopupMenuCreation(JPopupMenu popupMenu, ExtCodeArea codeArea, String menuPostfix, BinedModule.PopupMenuVariant variant, int x, int y) {
            }
        });
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        if (showParsingPanelAction != null) {
            showParsingPanelAction.updateForActiveFile();
        }
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(BinedInspectorModule.class);
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

    @Nonnull
    public ShowParsingPanelAction getShowParsingPanelAction() {
        if (showParsingPanelAction == null) {
            ensureSetup();
            showParsingPanelAction = new ShowParsingPanelAction();
            showParsingPanelAction.setup(application, editorProvider, resourceBundle);
        }

        return showParsingPanelAction;
    }

    public void registerViewValuesPanelMenuActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        actionModule.registerMenuGroup(FrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_PARSING_PANEL_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, getShowParsingPanelAction(), new MenuPosition(VIEW_PARSING_PANEL_MENU_GROUP_ID));
    }
}
