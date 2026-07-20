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
package org.exbin.bined.jaguif.viewer;

import org.exbin.bined.jaguif.viewer.action.ShowRowPositionAction;
import org.exbin.bined.jaguif.viewer.action.CodeTypeActions;
import org.exbin.bined.jaguif.viewer.action.CodeAreaViewModeActions;
import org.exbin.bined.jaguif.viewer.action.RowWrappingAction;
import org.exbin.bined.jaguif.viewer.action.HexCharactersCaseActions;
import org.exbin.bined.jaguif.viewer.action.PositionCodeTypeActions;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeAreaZone;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.bined.jaguif.viewer.action.ShowHeaderAction;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.contribution.GoToPositionContribution;
import org.exbin.bined.jaguif.viewer.action.CursorPositionCodeTypeActions;
import org.exbin.bined.jaguif.viewer.action.DataSizeCodeTypeActions;
import org.exbin.bined.jaguif.viewer.contribution.CopyDataSizeContribution;
import org.exbin.bined.jaguif.viewer.contribution.CopyCursorPositionContribution;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.viewer.contribution.RowWrappingContribution;
import org.exbin.bined.jaguif.viewer.contribution.ShowCursorPositionOffsetContribution;
import org.exbin.bined.jaguif.viewer.contribution.ShowHeaderContribution;
import org.exbin.bined.jaguif.viewer.contribution.ShowRelativeDataSizeContribution;
import org.exbin.bined.jaguif.viewer.contribution.ShowRowPositionContribution;
import org.exbin.bined.jaguif.viewer.settings.BinaryAppearanceOptions;
import org.exbin.bined.jaguif.viewer.settings.BinaryAppearanceSettingsApplier;
import org.exbin.bined.jaguif.viewer.settings.BinaryAppearanceSettingsComponent;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaSettingsComponent;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusSettingsApplier;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusSettingsComponent;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaViewerSettingsApplier;
import org.exbin.bined.jaguif.viewer.settings.GoToPositionOptions;
import org.exbin.bined.jaguif.viewer.settings.BinaryEncodingSettingsApplier;
import org.exbin.bined.jaguif.viewer.settings.BinaryEncodingSettingsComponent;
import org.exbin.bined.jaguif.viewer.settings.BinaryFontSettingsApplier;
import org.exbin.bined.jaguif.viewer.status.StatusCursorPositionFormat;
import org.exbin.bined.jaguif.viewer.status.StatusDataSizeFormat;
import org.exbin.bined.jaguif.viewer.status.StatusNumericGrouping;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextStateProvider;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContributionRule;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.encoding.settings.TextEncodingSettingsComponent;
import org.exbin.jaguif.text.font.action.TextFontAction;
import org.exbin.jaguif.text.font.contribution.TextFontContribution;
import org.exbin.jaguif.text.font.settings.TextFontOptions;
import org.exbin.jaguif.text.font.settings.TextFontSettingsComponent;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.frame.api.FrameController;
import org.exbin.jaguif.menu.api.SubMenuContribution;
import org.exbin.jaguif.text.encoding.contribution.ManageEncodingsContribution;
import org.jspecify.annotations.Nullable;

/**
 * Binary data viewer module.
 */
@NullMarked
public class BinedViewerModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedViewerModule.class);

    public static final String SETTINGS_PAGE_ID = "binary";
    public static final String SETTINGS_STATUS_PAGE_ID = "codeAreaStatusBar";
    public static final String SETTINGS_ENCODING_PAGE_ID = "codeAreaEncoding";
    public static final String SETTINGS_FONT_PAGE_ID = "codeAreaFont";
    public static final String VIEW_MODE_SUBMENU_ID = MODULE_ID + ".viewModeSubMenu";
    public static final String CODE_TYPE_SUBMENU_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String CODE_TYPE_MENU_GROUP_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String POSITION_CODE_TYPE_SUBMENU_ID = MODULE_ID + ".positionCodeTypeSubMenu";
    public static final String HEX_CHARACTERS_CASE_SUBMENU_ID = MODULE_ID + ".hexCharactersCaseSubMenu";
    public static final String POSITION_CODE_TYPE_POPUP_SUBMENU_ID = MODULE_ID + ".positionCodeTypePopupSubMenu";
    public static final String SHOW_POPUP_SUBMENU_ID = MODULE_ID + ".showPopupSubMenu";

    public static final String BINARY_CURSOR_POSITION_MENU_ID = "binaryCursorPosition";
    public static final String BINARY_CURSOR_POSITION_SETTINGS_GROUP_ID = "cursorPositionSettings";
    public static final String BINARY_CURSOR_POSITION_CODE_TYPE_SUBMENU_ID = "positionCodeType";
    public static final String BINARY_DATA_SIZE_MENU_ID = "binaryDataSize";
    public static final String BINARY_DATA_SIZE_SETTINGS_GROUP_ID = "dataSizeSettings";
    public static final String BINARY_DATA_SIZE_CODE_TYPE_SUBMENU_ID = "dataSizeCodeType";
    public static final String BINARY_ENCODING_MENU_ID = "binaryEncoding";
    public static final String BINARY_ENCODING_SETTINGS_GROUP_ID = "binaryEncodingSettings";

    private static final String BINED_TOOL_BAR_GROUP_ID = MODULE_ID + ".binedToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private CodeAreaViewModeActions viewModeActions;
    private CodeTypeActions codeTypeActions;
    private PositionCodeTypeActions positionCodeTypeActions;
    private HexCharactersCaseActions hexCharactersCaseActions;
    private StatusBar frameStatusBar;

    public BinedViewerModule() {
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedViewerModule.class);
        }

        return resourceBundle;
    }

    public void registerFrameStatusBar() {
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        FrameController frameHandler = frameModule.getFrameController();
        ActiveContextManagement contextManager = frameHandler.getContextManager();
        ContextUpdateManagement updateManager = frameHandler.getUpdateManager();
        updateManager.addGroup(FrameModuleApi.MAIN_STATUS_BAR_ID);
        ContextRegistration contextRegistrar = contextModule.createContextRegistrator(FrameModuleApi.MAIN_STATUS_BAR_ID, updateManager, contextManager);
        frameStatusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, contextRegistrar);
        frameModule.registerStatusBar(MODULE_ID, BinedComponentModule.BINARY_STATUS_BAR_ID, frameStatusBar.getComponent());
        frameModule.switchStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID);
    }

    public Optional<StatusBar> getFrameStatusBar() {
        return Optional.ofNullable(frameStatusBar);
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(CodeAreaOptions.class, (optionsStorage) -> new CodeAreaOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaStatusOptions.class, (optionsStorage) -> new CodeAreaStatusOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(BinaryAppearanceOptions.class, (optionsStorage) -> new BinaryAppearanceOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(GoToPositionOptions.class, (optionsStorage) -> new GoToPositionOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(TextEncodingOptions.class, (optionsStorage) -> new TextEncodingOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(TextFontOptions.class, (optionsStorage) -> new TextFontOptions(optionsStorage));

        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(CodeAreaViewerSettingsApplier.APPLIER_ID, new CodeAreaViewerSettingsApplier()));
        settingsManagement.registerApplySetting(CodeAreaOptions.class, new ApplySettingsContribution(CodeAreaViewerSettingsApplier.APPLIER_ID, new CodeAreaViewerSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocking.class, new ApplySettingsContribution(BinaryAppearanceSettingsApplier.APPLIER_ID, new BinaryAppearanceSettingsApplier()));
        settingsManagement.registerApplySetting(BinaryAppearanceOptions.class, new ApplySettingsContribution(BinaryAppearanceSettingsApplier.APPLIER_ID, new BinaryAppearanceSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(BinaryEncodingSettingsApplier.APPLIER_ID, new BinaryEncodingSettingsApplier()));
        settingsManagement.registerApplySetting(TextEncodingOptions.class, new ApplySettingsContribution(BinaryEncodingSettingsApplier.APPLIER_ID, new BinaryEncodingSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(BinaryFontSettingsApplier.APPLIER_ID, new BinaryFontSettingsApplier()));
        settingsManagement.registerApplySetting(TextFontOptions.class, new ApplySettingsContribution(BinaryFontSettingsApplier.APPLIER_ID, new BinaryFontSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextDocument.class, new ApplySettingsContribution(CodeAreaStatusSettingsApplier.APPLIER_ID, new CodeAreaStatusSettingsApplier()));
        settingsManagement.registerApplySetting(CodeAreaStatusOptions.class, new ApplySettingsContribution(CodeAreaStatusSettingsApplier.APPLIER_ID, new CodeAreaStatusSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(SETTINGS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(pageContribution);
        settingsManagement.registerSettingsRule(pageContribution, new SettingsPageContributionRule(DocumentModuleApi.SETTINGS_PAGE_ID));

        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(BinaryAppearanceSettingsComponent.COMPONENT_ID, new BinaryAppearanceSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(DocumentModuleApi.SETTINGS_PAGE_ID));

        settingsComponent = settingsManagement.registerComponent(CodeAreaSettingsComponent.COMPONENT_ID, new CodeAreaSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));

        SettingsPageContribution statusPageContribution = new SettingsPageContribution(SETTINGS_STATUS_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(statusPageContribution);
        settingsManagement.registerSettingsRule(statusPageContribution, new SettingsPageContributionRule(pageContribution));

        settingsComponent = settingsManagement.registerComponent(CodeAreaStatusSettingsComponent.COMPONENT_ID, new CodeAreaStatusSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(statusPageContribution));

        SettingsPageContribution encodingPageContribution = new SettingsPageContribution(SETTINGS_ENCODING_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(encodingPageContribution);
        settingsManagement.registerSettingsRule(encodingPageContribution, new SettingsPageContributionRule(pageContribution));

        settingsComponent = settingsManagement.registerComponent(TextEncodingSettingsComponent.COMPONENT_ID, new BinaryEncodingSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(encodingPageContribution));

        SettingsPageContribution fontPageContribution = new SettingsPageContribution(SETTINGS_FONT_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(fontPageContribution);
        settingsManagement.registerSettingsRule(fontPageContribution, new SettingsPageContributionRule(pageContribution));

        settingsComponent = settingsManagement.registerComponent(TextFontSettingsComponent.COMPONENT_ID, new TextFontSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(fontPageContribution));
    }

    public void registerWordWrapping() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new RowWrappingContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public ShowHeaderAction createShowHeaderAction() {
        ShowHeaderAction showHeaderAction = new ShowHeaderAction();
        showHeaderAction.init(getResourceBundle());
        return showHeaderAction;
    }

    public ShowRowPositionAction createShowRowPositionAction() {
        ShowRowPositionAction showRowPositionAction = new ShowRowPositionAction();
        showRowPositionAction.init(getResourceBundle());
        return showRowPositionAction;
    }

    public RowWrappingAction createRowWrappingAction() {
        RowWrappingAction rowWrappingAction = new RowWrappingAction();
        rowWrappingAction.init(getResourceBundle());
        return rowWrappingAction;
    }

    public TextFontAction createCodeAreaFontAction() {
        TextFontAction textFontAction = new TextFontAction();
        textFontAction.init(getResourceBundle());
        return textFontAction;
    }

    public CodeAreaViewModeActions getViewModeActions() {
        if (viewModeActions == null) {
            viewModeActions = new CodeAreaViewModeActions();
            viewModeActions.init(getResourceBundle());
        }

        return viewModeActions;
    }

    public CodeTypeActions getCodeTypeActions() {
        if (codeTypeActions == null) {
            codeTypeActions = new CodeTypeActions();
            codeTypeActions.init(getResourceBundle());
        }

        return codeTypeActions;
    }

    public PositionCodeTypeActions getPositionCodeTypeActions() {
        if (positionCodeTypeActions == null) {
            positionCodeTypeActions = new PositionCodeTypeActions();
            positionCodeTypeActions.init(getResourceBundle());
        }

        return positionCodeTypeActions;
    }

    public HexCharactersCaseActions getHexCharactersCaseActions() {
        if (hexCharactersCaseActions == null) {
            hexCharactersCaseActions = new HexCharactersCaseActions();
            hexCharactersCaseActions.init(getResourceBundle());
        }

        return hexCharactersCaseActions;
    }

    public void registerCodeTypeToolBarActions() {
        getCodeTypeActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.ABOVE));
        contribution = codeTypeActions.createCycleCodeTypesContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new TextFontContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(BinedComponentModule.VIEW_FONT_SUB_MENU_ID));
    }

    public void registerViewModeMenu() {
        getResourceBundle();
        getViewModeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action viewSubMenuAction = new AbstractAction(resourceBundle.getString("viewModeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        viewSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("viewModeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt.registerMenuItem(VIEW_MODE_SUBMENU_ID, viewSubMenuAction);
        mgmt = mgmt.getSubMenu(VIEW_MODE_SUBMENU_ID);
        SequenceContribution contribution = viewModeActions.createDualViewModeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = viewModeActions.createMatrixModeViewModeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = viewModeActions.createTextPreviewViewModeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerLayoutMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new ShowHeaderContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = new ShowRowPositionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerCodeTypeMenu() {
        getResourceBundle();
        getCodeTypeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action codeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("codeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        codeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("codeTypeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt.registerMenuItem(CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        GroupSequenceContribution groupContribution = mgmt.registerMenuGroup(CODE_TYPE_MENU_GROUP_ID);
        mgmt.registerMenuRule(groupContribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        SequenceContribution contribution = codeTypeActions.createBinaryCodeTypeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = codeTypeActions.createOctalCodeTypeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = codeTypeActions.createDecimalCodeTypeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = codeTypeActions.createHexadecimalCodeTypeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerPositionCodeTypeMenu() {
        getResourceBundle();
        getPositionCodeTypeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        mgmt.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, positionCodeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(POSITION_CODE_TYPE_SUBMENU_ID);
        SequenceContribution contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.OCTAL, null);
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.DECIMAL, null);
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.HEXADECIMAL, null);
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerHexCharactersCaseHandlerMenu() {
        getResourceBundle();
        getHexCharactersCaseActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action hexCharsCaseSubMenuAction = new AbstractAction(resourceBundle.getString("hexCharsCaseSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        hexCharsCaseSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexCharsCaseSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        mgmt.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, hexCharsCaseSubMenuAction);
        mgmt = mgmt.getSubMenu(HEX_CHARACTERS_CASE_SUBMENU_ID);
        SequenceContribution contribution = hexCharactersCaseActions.createUpperHexCharsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = hexCharactersCaseActions.createLowerHexCharsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerCodeAreaPopupMenu() {
        getResourceBundle();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);

        ActionMenuCreation showPositionCreating = new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                ContextComponent contextComponent = contextState.getActiveState(ContextComponent.class);
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return contextComponent instanceof BinaryDataComponent && ((inShowSubmenu && codeAreaZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && codeAreaZone != BasicCodeAreaZone.CODE_AREA));
            }
        };

        SequenceContribution contribution = new ShowHeaderContribution(showPositionCreating);
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        contribution = new ShowRowPositionContribution(showPositionCreating);
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_VIEW_GROUP_ID));

        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        getPositionCodeTypeActions();
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        contribution = mgmt.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, positionCodeTypeSubMenuAction);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        MenuDefinitionManagement subMgmt = mgmt.getSubMenu(POSITION_CODE_TYPE_POPUP_SUBMENU_ID);
        ActionMenuCreation positionCodeTypeCreating = new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                ContextComponent contextComponent = contextState.getActiveState(ContextComponent.class);
                return contextComponent instanceof BinaryDataComponent && (codeAreaZone == BasicCodeAreaZone.TOP_LEFT_CORNER || codeAreaZone == BasicCodeAreaZone.HEADER || codeAreaZone == BasicCodeAreaZone.ROW_POSITIONS);
            }
        };
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.OCTAL, positionCodeTypeCreating);
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.DECIMAL, positionCodeTypeCreating);
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.HEXADECIMAL, positionCodeTypeCreating);
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));

        Action popupShowSubMenuAction = new AbstractAction(resourceBundle.getString("popupShowSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        popupShowSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("popupShowSubMenu.shortDescription"));
        contribution = mgmt.registerMenuItem(SHOW_POPUP_SUBMENU_ID, popupShowSubMenuAction);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedComponentModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        subMgmt = mgmt.getSubMenu(SHOW_POPUP_SUBMENU_ID);
        contribution = new ShowHeaderContribution(showPositionCreating);
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = new ShowRowPositionContribution(showPositionCreating);
        subMgmt.registerMenuContribution(contribution);
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerCursorPositionStatusMenu() {
        getResourceBundle();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(BINARY_CURSOR_POSITION_MENU_ID, BinedViewerModule.MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BINARY_CURSOR_POSITION_MENU_ID, BinedViewerModule.MODULE_ID);

        GroupSequenceContribution groupContribution = mgmt.registerMenuGroup(BINARY_CURSOR_POSITION_SETTINGS_GROUP_ID);
        mgmt.registerMenuRule(groupContribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        Action codeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("cursorPositionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        codeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("cursorPositionCodeTypeSubMenu.shortDescription"));
        SubMenuContribution subMenu = mgmt.registerMenuItem(CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction);
        mgmt.registerMenuRule(subMenu, new GroupSequenceContributionRule(groupContribution));
        MenuDefinitionManagement subMgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);

        CursorPositionCodeTypeActions actions = new CursorPositionCodeTypeActions();
        actions.init(getResourceBundle());
        SequenceContribution contribution = actions.createPositionCodeTypeContribution(PositionCodeType.OCTAL);
        subMgmt.registerMenuContribution(contribution);
        contribution = actions.createPositionCodeTypeContribution(PositionCodeType.DECIMAL);
        subMgmt.registerMenuContribution(contribution);
        contribution = actions.createPositionCodeTypeContribution(PositionCodeType.HEXADECIMAL);
        subMgmt.registerMenuContribution(contribution);

        contribution = new ShowCursorPositionOffsetContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));

        contribution = new CopyCursorPositionContribution();
        mgmt.registerMenuContribution(contribution);

        contribution = new GoToPositionContribution();
        mgmt.registerMenuContribution(contribution);
    }

    public void registerDataSizeStatusMenu() {
        getPositionCodeTypeActions();
        getResourceBundle();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(BINARY_DATA_SIZE_MENU_ID, BinedViewerModule.MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BINARY_DATA_SIZE_MENU_ID, BinedViewerModule.MODULE_ID);

        GroupSequenceContribution groupContribution = mgmt.registerMenuGroup(BINARY_DATA_SIZE_SETTINGS_GROUP_ID);
        mgmt.registerMenuRule(groupContribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        Action codeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("dataSizeCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        codeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("dataSizeCodeTypeSubMenu.shortDescription"));
        SubMenuContribution subMenu = mgmt.registerMenuItem(CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction);
        mgmt.registerMenuRule(subMenu, new GroupSequenceContributionRule(groupContribution));
        MenuDefinitionManagement subMgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);

        DataSizeCodeTypeActions actions = new DataSizeCodeTypeActions();
        actions.init(getResourceBundle());
        SequenceContribution contribution = actions.createDataSizeCodeTypeContribution(PositionCodeType.OCTAL);
        subMgmt.registerMenuContribution(contribution);
        contribution = actions.createDataSizeCodeTypeContribution(PositionCodeType.DECIMAL);
        subMgmt.registerMenuContribution(contribution);
        contribution = actions.createDataSizeCodeTypeContribution(PositionCodeType.HEXADECIMAL);
        subMgmt.registerMenuContribution(contribution);

        contribution = new ShowRelativeDataSizeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));

        contribution = new CopyDataSizeContribution();
        mgmt.registerMenuContribution(contribution);
    }

    public void registerBinaryEncodingStatusMenu() {
        getPositionCodeTypeActions();
        getResourceBundle();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(BINARY_ENCODING_MENU_ID, BinedViewerModule.MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(BINARY_ENCODING_MENU_ID, BinedViewerModule.MODULE_ID);

        GroupSequenceContribution groupContribution = mgmt.registerMenuGroup(BINARY_ENCODING_SETTINGS_GROUP_ID);
        mgmt.registerMenuRule(groupContribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));

        ActionSequenceContribution contribution = new ManageEncodingsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(groupContribution));
    }

    public String getCaretPositionAsText(@Nullable CodeAreaCore codeArea, StatusNumericGrouping numericGrouping, StatusCursorPositionFormat cursorPositionFormat) {
        if (codeArea == null) {
            return "-";
        }

        CodeAreaCaretPosition caretPosition = ((CaretCapable) codeArea).getActiveCaretPosition();
        SelectionRange selectionRange = ((SelectionCapable) codeArea).getSelection();
        StringBuilder labelBuilder = new StringBuilder();
        if (!selectionRange.isEmpty()) {
            long first = selectionRange.getFirst();
            long last = selectionRange.getLast();
            labelBuilder.append(String.format(
                    resourceBundle.getString("cursorPositionFormat.withSelection"),
                    getPositionAsText(first, cursorPositionFormat.getCodeType(), numericGrouping),
                    getPositionAsText(last, cursorPositionFormat.getCodeType(), numericGrouping)
            ));
        } else {
            String basePosition = getPositionAsText(caretPosition.getDataPosition(), cursorPositionFormat.getCodeType(), numericGrouping);
            if (cursorPositionFormat.isShowOffset()) {
                labelBuilder.append(String.format(
                        resourceBundle.getString("cursorPositionFormat.withOffset"),
                        basePosition,
                        getPositionAsText(caretPosition.getCodeOffset(), cursorPositionFormat.getCodeType(), numericGrouping)
                ));
            } else {
                labelBuilder.append(basePosition);
            }
        }
        return labelBuilder.toString();
    }

    public String getDataSizeAsText(long dataSize, long originalDataSize, SelectionRange selectionRange, StatusNumericGrouping numericGrouping, StatusDataSizeFormat dataSizeFormat) {
        if (dataSize == -1) {
            return "-";
        }

        StringBuilder labelBuilder = new StringBuilder();
        if (selectionRange != null && !selectionRange.isEmpty()) {
            labelBuilder.append(String.format(resourceBundle.getString("dataSizeFormat.withSelection"),
                    getPositionAsText(selectionRange.getLength(), dataSizeFormat.getCodeType(), numericGrouping),
                    getPositionAsText(dataSize, dataSizeFormat.getCodeType(), numericGrouping)
            ));
        } else {
            String baseDataSize = getPositionAsText(dataSize, dataSizeFormat.getCodeType(), numericGrouping);
            if (dataSizeFormat.isShowRelative()) {
                long difference = dataSize - originalDataSize;
                labelBuilder.append(String.format(
                        resourceBundle.getString("dataSizeFormat.withDifference"),
                        baseDataSize,
                        (difference > 0 ? "+" : "") + getPositionAsText(difference, dataSizeFormat.getCodeType(), numericGrouping)
                ));
            } else {
                labelBuilder.append(baseDataSize);
            }
        }

        return labelBuilder.toString();
    }

    public String getPositionAsText(long position, PositionCodeType codeType, StatusNumericGrouping numericGrouping) {
        if (position == 0) {
            return "0";
        }

        int spaceGroupSize = 0;
        switch (codeType) {
            case OCTAL: {
                spaceGroupSize = numericGrouping.getOctalSpaceGroupSize();
                break;
            }
            case DECIMAL: {
                spaceGroupSize = numericGrouping.getDecimalSpaceGroupSize();
                break;
            }
            case HEXADECIMAL: {
                spaceGroupSize = numericGrouping.getHexadecimalSpaceGroupSize();
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(codeType);
        }

        long remainder = position > 0 ? position : -position;
        StringBuilder builder = new StringBuilder();
        int base = codeType.getBase();
        int groupSize = spaceGroupSize == 0 ? -1 : spaceGroupSize;
        while (remainder > 0) {
            if (groupSize >= 0) {
                if (groupSize == 0) {
                    builder.insert(0, ' ');
                    groupSize = spaceGroupSize - 1;
                } else {
                    groupSize--;
                }
            }

            int digit = (int) (remainder % base);
            remainder = remainder / base;
            builder.insert(0, CodeAreaUtils.UPPER_HEX_CODES[digit]);
        }

        if (position < 0) {
            builder.insert(0, "-");
        }
        return builder.toString();
    }
}
