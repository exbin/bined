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
package org.exbin.bined.jaguif.viewer;

import java.awt.BorderLayout;
import org.exbin.bined.jaguif.viewer.action.ShowRowPositionAction;
import org.exbin.bined.jaguif.viewer.action.CodeTypeActions;
import org.exbin.bined.jaguif.viewer.action.CodeAreaViewModeActions;
import org.exbin.bined.jaguif.viewer.action.RowWrappingAction;
import org.exbin.bined.jaguif.viewer.action.HexCharactersCaseActions;
import org.exbin.bined.jaguif.viewer.action.PositionCodeTypeActions;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.bined.jaguif.viewer.action.ShowHeaderAction;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinEdFileManager;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.BinedComponentModule.PopupMenuVariant;
import org.exbin.bined.jaguif.component.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.viewer.contribution.RowWrappingContribution;
import org.exbin.bined.jaguif.viewer.contribution.ShowHeaderContribution;
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
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
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
import org.exbin.jaguif.text.encoding.EncodingsManager;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.encoding.settings.TextEncodingSettingsComponent;
import org.exbin.jaguif.text.font.action.TextFontAction;
import org.exbin.jaguif.text.font.contribution.TextFontContribution;
import org.exbin.jaguif.text.font.settings.TextFontOptions;
import org.exbin.jaguif.text.font.settings.TextFontSettingsComponent;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Binary data viewer module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
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

    private static final String BINED_TOOL_BAR_GROUP_ID = MODULE_ID + ".binedToolBarGroup";

    private java.util.ResourceBundle resourceBundle = null;

    private CodeAreaViewModeActions viewModeActions;
    private CodeTypeActions codeTypeActions;
    private PositionCodeTypeActions positionCodeTypeActions;
    private HexCharactersCaseActions hexCharactersCaseActions;
    private EncodingsManager encodingsManager;

    public BinedViewerModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedViewerModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerStatusBar() {
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ContextRegistration contextRegistrar = contextModule.createContextRegistrator();
        StatusBar statusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, contextRegistrar);
        javax.swing.JPanel test = new javax.swing.JPanel(new BorderLayout());
        // StatusBarDefinitionManagement statusBarManager = statusBarModule.getMainStatusBarManager();
        // StatusBar statusBar = statusBarManager.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID);
        //test.add(new javax.swing.JLabel("TEST"), BorderLayout.CENTER);
        test.add(statusBar.getComponent(), BorderLayout.CENTER);
        frameModule.registerStatusBar(MODULE_ID, BinedComponentModule.BINARY_STATUS_BAR_ID, test);
        frameModule.switchStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID);
    }

    public void registerEncodings() {
        getEncodingsManager();
        encodingsManager.rebuildEncodings();

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> encodingsManager.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP_LAST));
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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new RowWrappingContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    @Nonnull
    private AbstractAction createShowHeaderAction() {
        ensureSetup();
        ShowHeaderAction showHeaderAction = new ShowHeaderAction();
        showHeaderAction.init(resourceBundle);
        return showHeaderAction;
    }

    @Nonnull
    private AbstractAction createShowRowPositionAction() {
        ensureSetup();
        ShowRowPositionAction showRowPositionAction = new ShowRowPositionAction();
        showRowPositionAction.init(resourceBundle);
        return showRowPositionAction;
    }

    @Nonnull
    public RowWrappingAction createRowWrappingAction() {
        ensureSetup();
        RowWrappingAction rowWrappingAction = new RowWrappingAction();
        rowWrappingAction.init(resourceBundle);
        return rowWrappingAction;
    }

    @Nonnull
    public TextFontAction createCodeAreaFontAction() {
        ensureSetup();
        TextFontAction textFontAction = new TextFontAction();
        textFontAction.init(resourceBundle);
        return textFontAction;
    }

    @Nonnull
    public EncodingsManager getEncodingsManager() {
        if (encodingsManager == null) {
            ensureSetup();
            BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
            BinEdFileManager fileManager = binedModule.getFileManager();
            encodingsManager = new EncodingsManager();
            // TODO fileManager.updateTextEncodingStatus(encodingsManager);
            encodingsManager.init();
        }

        return encodingsManager;
    }

    @Nonnull
    public CodeAreaViewModeActions getViewModeActions() {
        if (viewModeActions == null) {
            ensureSetup();
            viewModeActions = new CodeAreaViewModeActions();
            viewModeActions.init(resourceBundle);
        }

        return viewModeActions;
    }

    @Nonnull
    public CodeTypeActions getCodeTypeActions() {
        if (codeTypeActions == null) {
            ensureSetup();
            codeTypeActions = new CodeTypeActions();
            codeTypeActions.init(resourceBundle);
        }

        return codeTypeActions;
    }

    @Nonnull
    public PositionCodeTypeActions getPositionCodeTypeActions() {
        if (positionCodeTypeActions == null) {
            ensureSetup();
            positionCodeTypeActions = new PositionCodeTypeActions();
            positionCodeTypeActions.init(resourceBundle);
        }

        return positionCodeTypeActions;
    }

    @Nonnull
    public HexCharactersCaseActions getHexCharactersCaseActions() {
        if (hexCharactersCaseActions == null) {
            ensureSetup();
            hexCharactersCaseActions = new HexCharactersCaseActions();
            hexCharactersCaseActions.init(resourceBundle);
        }

        return hexCharactersCaseActions;
    }

    public void registerCodeTypeToolBarActions() {
        getCodeTypeActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.ABOVE));
        contribution = codeTypeActions.createCycleCodeTypesContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new TextFontContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(BinedComponentModule.VIEW_FONT_SUB_MENU_ID));
    }

    public void registerViewModeMenu() {
        getViewModeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action viewSubMenuAction = new AbstractAction(resourceBundle.getString("viewModeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        viewSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("viewModeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(VIEW_MODE_SUBMENU_ID, viewSubMenuAction);
        mgmt = mgmt.getSubMenu(VIEW_MODE_SUBMENU_ID);
        contribution = viewModeActions.createDualViewModeContribution();
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
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new ShowHeaderContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = new ShowRowPositionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    public void registerCodeTypeMenu() {
        getCodeTypeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action codeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("codeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        codeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("codeTypeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        GroupSequenceContribution groupContribution = mgmt.registerMenuGroup(CODE_TYPE_MENU_GROUP_ID);
        mgmt.registerMenuRule(groupContribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = codeTypeActions.createBinaryCodeTypeContribution();
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
        getPositionCodeTypeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, positionCodeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(POSITION_CODE_TYPE_SUBMENU_ID);
        contribution = positionCodeTypeActions.createPositionCodeTypeContribution(PositionCodeType.OCTAL, null);
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
        getHexCharactersCaseActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        Action hexCharsCaseSubMenuAction = new AbstractAction(resourceBundle.getString("hexCharsCaseSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        hexCharsCaseSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexCharsCaseSubMenu.shortDescription"));
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, hexCharsCaseSubMenuAction);
        mgmt = mgmt.getSubMenu(HEX_CHARACTERS_CASE_SUBMENU_ID);
        contribution = hexCharactersCaseActions.createUpperHexCharsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = hexCharactersCaseActions.createLowerHexCharsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerCodeAreaPopupMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(BinedComponentModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);

        ActionMenuCreation showPositionCreating = new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return popupMenuVariant == PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
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
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedComponentModule binedModule = App.getModule(BinedComponentModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
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

    public void dropBinEdComponentPopupMenu() {
        dropCodeAreaPopupMenu("");
    }

    private void dropCodeAreaPopupMenu(String menuPostfix) {
//        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
//        MenuManagement mgmt = menuModule.getMainMenuManager(MODULE_ID);
//        mgmt.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }
}
