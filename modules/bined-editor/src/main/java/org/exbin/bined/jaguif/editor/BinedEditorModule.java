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
package org.exbin.bined.jaguif.editor;

import org.exbin.bined.jaguif.editor.action.PropertiesAction;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.bined.jaguif.editor.action.EditSelectionAction;
import org.exbin.bined.jaguif.editor.action.ReloadFileAction;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorOptions;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorSettingsApplier;
import org.exbin.bined.jaguif.editor.settings.BinaryFileProcessingOptions;
import org.exbin.bined.jaguif.editor.settings.BinaryFileProcessingSettingsApplier;
import org.exbin.bined.jaguif.editor.settings.CodeAreaEditingSettingsComponent;
import org.exbin.bined.jaguif.editor.settings.CodeAreaFileProcessingSettingsComponent;
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
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedEditorModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedEditorModule.class);
    public static final String SETTINGS_PAGE_ID = "codeAreaEditing";

    private java.util.ResourceBundle resourceBundle = null;

    public BinedEditorModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedEditorModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(BinaryEditorOptions.class, (optionsStorage) -> new BinaryEditorOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(BinaryFileProcessingOptions.class, (optionsStorage) -> new BinaryFileProcessingOptions(optionsStorage));

        settingsManagement.registerApplySetting(BinaryEditorOptions.class, new ApplySettingsContribution(BinaryEditorSettingsApplier.APPLIER_ID, new BinaryEditorSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocument.class, new ApplySettingsContribution(BinaryEditorSettingsApplier.APPLIER_ID, new BinaryEditorSettingsApplier()));
        settingsManagement.registerApplySetting(BinaryFileProcessingOptions.class, new ApplySettingsContribution(BinaryFileProcessingSettingsApplier.APPLIER_ID, new BinaryFileProcessingSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocument.class, new ApplySettingsContribution(BinaryFileProcessingSettingsApplier.APPLIER_ID, new BinaryFileProcessingSettingsApplier()));

        SettingsPageContribution settingsPage = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("binary"));
        SettingsComponentContribution registerComponent = settingsManagement.registerComponent(CodeAreaFileProcessingSettingsComponent.COMPONENT_ID, new CodeAreaFileProcessingSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));
        registerComponent = settingsManagement.registerComponent(CodeAreaEditingSettingsComponent.COMPONENT_ID, new CodeAreaEditingSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    @Nonnull
    public EditSelectionAction createEditSelectionAction() {
        ensureSetup();
        EditSelectionAction editSelectionAction = new EditSelectionAction();
        editSelectionAction.setup(resourceBundle);
        return editSelectionAction;
    }

    @Nonnull
    public PropertiesAction createPropertiesAction() {
        ensureSetup();
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.setup(resourceBundle);
        return propertiesAction;
    }

    @Nonnull
    private ReloadFileAction createReloadFileAction() {
        ensureSetup();
        ReloadFileAction reloadFileAction = new ReloadFileAction();
        reloadFileAction.setup(resourceBundle);
        return reloadFileAction;
    }

    public void registerPropertiesMenu() {
        createPropertiesAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPropertiesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerReloadFileMenu() {
        createReloadFileAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createReloadFileAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditSelectionAction() {
        createEditSelectionAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerCodeAreaPopupMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_SELECTION_GROUP_ID));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }
}
