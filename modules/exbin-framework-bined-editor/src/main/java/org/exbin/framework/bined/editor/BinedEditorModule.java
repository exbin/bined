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
package org.exbin.framework.bined.editor;

import org.exbin.framework.bined.editor.action.PropertiesAction;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.bined.editor.action.EditSelectionAction;
import org.exbin.framework.bined.editor.action.ReloadFileAction;
import org.exbin.framework.bined.editor.service.EditorOptionsService;
import org.exbin.framework.bined.editor.settings.BinaryEditorSettingsApplier;
import org.exbin.framework.bined.editor.settings.CodeAreaEditingSettingsComponent;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;

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
//        BinedModule binedModule = App.getModule(BinedModule.class);
//        EditorProvider editorProvider = binedModule.getEditorProvider();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        
        settingsManagement.registerApplySetting(EditorOptionsService.class, new ApplySettingsContribution(BinaryEditorSettingsApplier.APPLIER_ID, new BinaryEditorSettingsApplier()));
        
        SettingsPageContribution settingsPage = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("editorBinary"));
        SettingsComponentContribution registerComponent = settingsManagement.registerComponent(CodeAreaEditingSettingsComponent.COMPONENT_ID, new CodeAreaEditingSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));

        /* OptionsGroup binaryCodeAreaEditingGroup = settingsModule.createOptionsGroup("binaryEditorEditing", resourceBundle);
        settingsManagement.registerGroup(binaryCodeAreaEditingGroup);
        settingsManagement.registerGroupRule(binaryCodeAreaEditingGroup, new ParentOptionsGroupRule("binaryEditor"));
        CodeAreaEditingSettingsComponent codeAreaEditingOptionsPage = new CodeAreaEditingSettingsComponent();
        codeAreaEditingOptionsPage.setEditorOptionsService(new EditorOptionsService() {
            @Override
            public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                    if (!fileHandler.isModified() || editorProvider.releaseFile(fileHandler)) {
                        fileHandler.switchFileHandlingMode(fileHandlingMode);
                        ((BinEdEditorProvider) editorProvider).updateStatus();
                    }
                }
            }

            @Override
            public void setEnterKeyHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                        ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(enterKeyHandlingMode);
                    } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                        ((DefaultCodeAreaCommandHandler) commandHandler).setEnterKeyHandlingMode(enterKeyHandlingMode);
                    }
                }
            }

            @Override
            public void setTabKeyHandlingMode(TabKeyHandlingMode tabKeyHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                        ((CodeAreaOperationCommandHandler) commandHandler).setTabKeyHandlingMode(tabKeyHandlingMode);
                    } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                        ((DefaultCodeAreaCommandHandler) commandHandler).setTabKeyHandlingMode(tabKeyHandlingMode);
                    }
            }
            }
        });
        codeAreaEditingOptionsPage.setResourceBundle(resourceBundle);
        settingsManagement.registerPage(codeAreaEditingOptionsPage);
        settingsManagement.registerPageRule(codeAreaEditingOptionsPage, new GroupOptionsPageRule("binaryEditor"));
        BinEdFileManager fileManager = binedModule.getFileManager();
        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                return Optional.of(new BinEdComponentPanel.BinEdComponentExtension() {
                    @Override
                    public void onCreate(BinEdComponentPanel componentPanel) {
                    }

                    @Override
                    public void onInitFromOptions(OptionsStorage options) {
                        SectCodeArea codeArea = component.getCodeArea();
                        BinaryEditorOptions editorOptions = new BinaryEditorOptions(options);
                        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                        if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                            ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
                            ((CodeAreaOperationCommandHandler) commandHandler).setTabKeyHandlingMode(editorOptions.getTabKeyHandlingMode());
                        } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                            ((DefaultCodeAreaCommandHandler) commandHandler).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
                            ((DefaultCodeAreaCommandHandler) commandHandler).setTabKeyHandlingMode(editorOptions.getTabKeyHandlingMode());
                        }
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
        }); */
    }

    public void registerEditSelection() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createPropertiesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerReloadFileMenu() {
        createReloadFileAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createReloadFileAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerEditSelectionAction() {
        createEditSelectionAction();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerCodeAreaPopupMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createEditSelectionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_SELECTION_GROUP_ID));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }
}
