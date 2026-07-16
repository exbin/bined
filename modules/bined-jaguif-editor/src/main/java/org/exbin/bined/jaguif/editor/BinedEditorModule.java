/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.editor;

import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.editor.action.EditModeActions;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.bined.jaguif.editor.action.EditSelectionAction;
import org.exbin.bined.jaguif.editor.contribution.EditSelectionContribution;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorOptions;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorSettingsApplier;
import org.exbin.bined.jaguif.editor.settings.CodeAreaEditingSettingsComponent;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Binary data editor module.
 */
@NullMarked
public class BinedEditorModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedEditorModule.class);
    public static final String SETTINGS_PAGE_ID = "codeAreaEditing";
    public static final String EDIT_MODE_MENU_ID = "editMode";
    public static final String BINARY_EDIT_MODE_MENU_ID = "binaryEditMode";

    private java.util.ResourceBundle resourceBundle = null;

    public BinedEditorModule() {
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedEditorModule.class);
        }

        return resourceBundle;
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(BinaryEditorOptions.class, (optionsStorage) -> new BinaryEditorOptions(optionsStorage));

        settingsManagement.registerApplySetting(BinaryEditorOptions.class, new ApplySettingsContribution(BinaryEditorSettingsApplier.APPLIER_ID, new BinaryEditorSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocument.class, new ApplySettingsContribution(BinaryEditorSettingsApplier.APPLIER_ID, new BinaryEditorSettingsApplier()));

        SettingsPageContribution settingsPage = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("binary"));
        SettingsComponentContribution registerComponent = settingsManagement.registerComponent(CodeAreaEditingSettingsComponent.COMPONENT_ID, new CodeAreaEditingSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new EditSelectionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public EditSelectionAction createEditSelectionAction() {
        EditSelectionAction editSelectionAction = new EditSelectionAction();
        editSelectionAction.init(getResourceBundle());
        return editSelectionAction;
    }

    public EditModeActions createEditModeActions() {
        EditModeActions editModeActions = new EditModeActions();
        editModeActions.init(getResourceBundle());
        return editModeActions;
    }

    public void registerEditSelectionAction() {
        createEditSelectionAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new EditSelectionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerCodeAreaPopupMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = new EditSelectionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_SELECTION_GROUP_ID));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditModeStatusMenu() {
        getResourceBundle();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(BINARY_EDIT_MODE_MENU_ID, BinedEditorModule.MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BINARY_EDIT_MODE_MENU_ID, BinedEditorModule.MODULE_ID);

        EditModeActions actions = createEditModeActions();
        actions.init(getResourceBundle());
        SequenceContribution contribution = actions.createInsertEditModeOperationContribution();
        mgmt.registerMenuContribution(contribution);
        contribution = actions.createOverwriteEditModeOperationContribution();
        mgmt.registerMenuContribution(contribution);
    }
}
