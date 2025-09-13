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
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.search.action.FindReplaceActions;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Binary editor search module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedSearchModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedSearchModule.class);

    public static final String EDIT_FIND_TOOL_BAR_GROUP_ID = MODULE_ID + ".editFindToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;

    private FindReplaceActions findReplaceActions;

    public BinedSearchModule() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerSearchComponent() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addBinEdComponentExtension((BinEdComponentPanel component) -> Optional.of(new DefaultBinEdComponentSearch()));
    }

    @Nonnull
    public FindReplaceActions getFindReplaceActions() {
        if (findReplaceActions == null) {
            ensureSetup();
            findReplaceActions = new FindReplaceActions();
            findReplaceActions.setup(resourceBundle);
        }

        return findReplaceActions;
    }

    public void registerEditFindMenuActions() {
        getFindReplaceActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        // TODO SearchModule
        String groupId = BinedModule.EDIT_FIND_MENU_GROUP_ID;
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(findReplaceActions.createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(groupId));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditFindAgainAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(groupId));
        contribution = mgmt.registerMenuItem(findReplaceActions.createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(groupId));
    }

    public void registerEditFindPopupMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(getFindReplaceActions().createEditFindAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
        contribution = mgmt.registerMenuItem(getFindReplaceActions().createEditReplaceAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(BinedModule.CODE_AREA_POPUP_FIND_GROUP_ID));
    }

    public void registerEditFindToolBarActions() {
        getFindReplaceActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(EDIT_FIND_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerToolBarItem(findReplaceActions.createEditFindAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_TOOL_BAR_GROUP_ID));
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedSearchModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    public EditorProvider getEditorProvider() {
        return Objects.requireNonNull(editorProvider, "Editor provider was not yet initialized");
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }
}
