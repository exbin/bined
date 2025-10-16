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
package org.exbin.framework.bined.viewer;

import org.exbin.framework.bined.viewer.action.ShowRowPositionAction;
import org.exbin.framework.bined.viewer.action.CodeTypeActions;
import org.exbin.framework.bined.viewer.action.ViewModeHandlerActions;
import org.exbin.framework.bined.viewer.action.RowWrappingAction;
import org.exbin.framework.bined.viewer.action.HexCharactersCaseActions;
import org.exbin.framework.bined.viewer.action.PositionCodeTypeActions;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.bined.EditOperation;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.bined.viewer.action.ShowHeaderAction;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.viewer.settings.BinaryViewerOptions;
import org.exbin.framework.bined.viewer.service.BinaryAppearanceService;
import org.exbin.framework.bined.viewer.service.impl.BinaryAppearanceServiceImpl;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.BinedModule.PopupMenuVariant;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.viewer.settings.CodeAreaOptions;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.OptionsStorage;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.encoding.settings.TextEncodingOptions;
import org.exbin.framework.text.encoding.service.TextEncodingService;
import org.exbin.framework.text.font.action.TextFontAction;
import org.exbin.framework.text.font.settings.TextFontOptions;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Binary data editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedViewerModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedViewerModule.class);

    public static final String VIEW_MODE_SUBMENU_ID = MODULE_ID + ".viewModeSubMenu";
    public static final String CODE_TYPE_SUBMENU_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String POSITION_CODE_TYPE_SUBMENU_ID = MODULE_ID + ".positionCodeTypeSubMenu";
    public static final String HEX_CHARACTERS_CASE_SUBMENU_ID = MODULE_ID + ".hexCharactersCaseSubMenu";
    public static final String POSITION_CODE_TYPE_POPUP_SUBMENU_ID = MODULE_ID + ".positionCodeTypePopupSubMenu";
    public static final String SHOW_POPUP_SUBMENU_ID = MODULE_ID + ".showPopupSubMenu";

    private static final String BINED_TOOL_BAR_GROUP_ID = MODULE_ID + ".binedToolBarGroup";

    public static final String BINARY_STATUS_BAR_ID = "binaryStatusBar";

    private java.util.ResourceBundle resourceBundle = null;

    private BinedSettingsManager binedOptionsManager;

    private ViewModeHandlerActions viewModeActions;
    private CodeTypeActions codeTypeActions;
    private PositionCodeTypeActions positionCodeTypeActions;
    private HexCharactersCaseActions hexCharactersCaseActions;
    private EncodingsHandler encodingsHandler;

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

    @Nonnull
    public BinedSettingsManager getMainOptionsManager() {
        if (binedOptionsManager == null) {
            binedOptionsManager = new BinedSettingsManager();
            BinedModule binedModule = App.getModule(BinedModule.class);
            EditorProvider editorProvider = binedModule.getEditorProvider();
            binedOptionsManager.setEditorProvider(editorProvider);
        }
        return binedOptionsManager;
    }

    public void registerStatusBar() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        EditorProvider editorProvider = binedModule.getEditorProvider();
        fileManager.registerStatusBar();
        fileManager.setStatusControlHandler(new BinaryStatusController(editorProvider));

        if (encodingsHandler != null) {
            fileManager.updateTextEncodingStatus(encodingsHandler);
        }
    }

    public void registerEncodings() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(() -> encodingsHandler.getToolsEncodingMenu());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP_LAST));
    }

    public void registerSettings() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        EditorProvider editorProvider = binedModule.getEditorProvider();
        BinaryAppearanceService binaryAppearanceService = new BinaryAppearanceServiceImpl(this, editorProvider);

        getMainOptionsManager().registerSettings(getEncodingsHandler(), fileManager, binaryAppearanceService);
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
                        CodeAreaOptions.applyToCodeArea(new CodeAreaOptions(options), codeArea);

                        String encoding = new TextEncodingOptions(options).getSelectedEncoding();
                        if (!encoding.isEmpty()) {
                            codeArea.setCharset(Charset.forName(encoding));
                        }

                        TextFontOptions textFontOptions = new TextFontOptions(options);
                        ((FontCapable) codeArea).setCodeFont(textFontOptions.isUseDefaultFont() ? CodeAreaOptions.DEFAULT_FONT : textFontOptions.getFont(CodeAreaOptions.DEFAULT_FONT));
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
        });
    }

    public void registerWordWrapping() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createRowWrappingAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
    }

    @Nonnull
    private AbstractAction createShowHeaderAction() {
        ensureSetup();
        ShowHeaderAction showHeaderAction = new ShowHeaderAction();
        showHeaderAction.setup(resourceBundle);
        showHeaderAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return popupMenuVariant == PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                menuItem.setSelected(Objects.requireNonNull(getActiveCodeArea().getLayoutProfile()).isShowHeader());
            }
        });
        return showHeaderAction;
    }

    @Nonnull
    private AbstractAction createShowRowPositionAction() {
        ensureSetup();
        ShowRowPositionAction showRowPositionAction = new ShowRowPositionAction();
        showRowPositionAction.setup(resourceBundle);
        showRowPositionAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(subMenuId);
                return popupMenuVariant == PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                menuItem.setSelected(Objects.requireNonNull(getActiveCodeArea().getLayoutProfile()).isShowRowPosition());
            }
        });
        return showRowPositionAction;
    }

    @Nonnull
    public RowWrappingAction createRowWrappingAction() {
        ensureSetup();
        RowWrappingAction rowWrappingAction = new RowWrappingAction();
        rowWrappingAction.setup(resourceBundle);
        return rowWrappingAction;
    }

    @Nonnull
    public TextFontAction createCodeAreaFontAction() {
        ensureSetup();
        TextFontAction textFontAction = new TextFontAction();
        textFontAction.setup(resourceBundle);
        return textFontAction;
    }

    @Nonnull
    public EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            ensureSetup();
            BinedModule binedModule = App.getModule(BinedModule.class);
            BinEdFileManager fileManager = binedModule.getFileManager();
            encodingsHandler = new EncodingsHandler();
            fileManager.updateTextEncodingStatus(encodingsHandler);
            encodingsHandler.init();

            encodingsHandler.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
                @Override
                public void encodingListChanged() {
                    encodingsHandler.rebuildEncodings();
                }

                @Override
                public void selectedEncodingChanged() {
                    BinedModule binedModule = App.getModule(BinedModule.class);
                    EditorProvider editorProvider = binedModule.getEditorProvider();
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        ((BinEdFileHandler) activeFile.get()).getBinaryDataComponent().setCharset(Charset.forName(encodingsHandler.getSelectedEncoding()));
                    }
                }
            });
        }

        return encodingsHandler;
    }

    @Nonnull
    public ViewModeHandlerActions getViewModeActions() {
        if (viewModeActions == null) {
            ensureSetup();
            viewModeActions = new ViewModeHandlerActions();
            viewModeActions.setup(resourceBundle);
        }

        return viewModeActions;
    }

    @Nonnull
    public CodeTypeActions getCodeTypeActions() {
        if (codeTypeActions == null) {
            ensureSetup();
            codeTypeActions = new CodeTypeActions();
            codeTypeActions.setup(resourceBundle);
        }

        return codeTypeActions;
    }

    @Nonnull
    public PositionCodeTypeActions getPositionCodeTypeActions() {
        if (positionCodeTypeActions == null) {
            ensureSetup();
            positionCodeTypeActions = new PositionCodeTypeActions();
            positionCodeTypeActions.setup(resourceBundle);
        }

        return positionCodeTypeActions;
    }

    @Nonnull
    public HexCharactersCaseActions getHexCharactersCaseActions() {
        if (hexCharactersCaseActions == null) {
            ensureSetup();
            hexCharactersCaseActions = new HexCharactersCaseActions();
            hexCharactersCaseActions.setup(resourceBundle);
        }

        return hexCharactersCaseActions;
    }

    public void registerCodeTypeToolBarActions() {
        getCodeTypeActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerToolBarRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.ABOVE));
        contribution = mgmt.registerToolBarItem(codeTypeActions.createCycleCodeTypesAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createCodeAreaFontAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(VIEW_MODE_SUBMENU_ID, viewSubMenuAction);
        mgmt = mgmt.getSubMenu(VIEW_MODE_SUBMENU_ID);
        contribution = mgmt.registerMenuItem(viewModeActions.createDualModeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(viewModeActions.createCodeMatrixModeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(viewModeActions.createTextPreviewModeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerLayoutMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createShowHeaderAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(createShowRowPositionAction());
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(CODE_TYPE_SUBMENU_ID);
        contribution = mgmt.registerMenuItem(codeTypeActions.createBinaryCodeTypeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(codeTypeActions.createOctalCodeTypeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(codeTypeActions.createDecimalCodeTypeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(codeTypeActions.createHexadecimalCodeTypeAction());
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, positionCodeTypeSubMenuAction);
        mgmt = mgmt.getSubMenu(POSITION_CODE_TYPE_SUBMENU_ID);
        contribution = mgmt.registerMenuItem(positionCodeTypeActions.createOctalCodeTypeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(positionCodeTypeActions.createDecimalCodeTypeAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(positionCodeTypeActions.createHexadecimalCodeTypeAction());
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, hexCharsCaseSubMenuAction);
        mgmt = mgmt.getSubMenu(HEX_CHARACTERS_CASE_SUBMENU_ID);
        contribution = mgmt.registerMenuItem(hexCharactersCaseActions.createUpperHexCharsAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(hexCharactersCaseActions.createLowerHexCharsAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    public void registerCodeAreaPopupMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, MODULE_ID);

        SequenceContribution contribution = mgmt.registerMenuItem(createShowHeaderAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        contribution = mgmt.registerMenuItem(createShowRowPositionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));

        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        contribution = mgmt.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, positionCodeTypeSubMenuAction);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        MenuManagement subMgmt = mgmt.getSubMenu(POSITION_CODE_TYPE_POPUP_SUBMENU_ID);
        contribution = subMgmt.registerMenuItem(createOctalPositionTypeAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = subMgmt.registerMenuItem(createDecimalPositionTypeAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = subMgmt.registerMenuItem(createHexadecimalPositionTypeAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));

        Action popupShowSubMenuAction = new AbstractAction(resourceBundle.getString("popupShowSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        popupShowSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("popupShowSubMenu.shortDescription"));
        contribution = mgmt.registerMenuItem(SHOW_POPUP_SUBMENU_ID, popupShowSubMenuAction);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(BinedModule.CODE_AREA_POPUP_VIEW_GROUP_ID));
        subMgmt = mgmt.getSubMenu(SHOW_POPUP_SUBMENU_ID);
        contribution = subMgmt.registerMenuItem(createShowHeaderAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = subMgmt.registerMenuItem(createShowRowPositionAction());
        subMgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    @Nonnull
    private Action createOctalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.OCTAL);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.OctalPositionCodeTypeAction.ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                menuItem.setSelected(getActiveCodeArea().getPositionCodeType() == PositionCodeType.OCTAL);
            }
        });

        return action;
    }

    @Nonnull
    private Action createDecimalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.DecimalPositionCodeTypeAction.ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                menuItem.setSelected(getActiveCodeArea().getPositionCodeType() == PositionCodeType.DECIMAL);
            }
        });

        return action;
    }

    @Nonnull
    private Action createHexadecimalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.HexadecimalPositionCodeTypeAction.ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                PopupMenuVariant popupMenuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone popupMenuPositionZone = binedModule.getPopupMenuPositionZone();
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
                menuItem.setSelected(getActiveCodeArea().getPositionCodeType() == PositionCodeType.HEXADECIMAL);
            }
        });

        return action;
    }

    @Nonnull
    public SectCodeArea getActiveCodeArea() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        Optional<FileHandler> activeFile = binedModule.getEditorProvider().getActiveFile();
        if (activeFile.isPresent()) {
            return ((BinEdFileHandler) activeFile.get()).getComponent().getCodeArea();
        }
        throw new IllegalStateException("No active file");
    }

    public void dropBinEdComponentPopupMenu() {
        dropCodeAreaPopupMenu("");
    }

    private void dropCodeAreaPopupMenu(String menuPostfix) {
//        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
//        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID);
//        mgmt.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }

    public void loadFromPreferences(OptionsStorage options) {
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        encodingsHandler.loadFromOptions(new TextEncodingOptions(options));
        fileManager.loadFromOptions(options);
    }

    @ParametersAreNonnullByDefault
    private class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {

        private final EditorProvider editorProvider;

        public BinaryStatusController(EditorProvider editorProvider) {
            this.editorProvider = editorProvider;
        }

        @Override
        public void changeEditOperation(EditOperation editOperation) {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (activeFile.isPresent()) {
                ((BinEdFileHandler) activeFile.get()).getCodeArea().setEditOperation(editOperation);
            }
        }

        @Override
        public void changeCursorPosition() {
            GoToPositionAction action = new GoToPositionAction();
            action.setCodeArea(getActiveCodeArea());
            action.actionPerformed(null);
        }

        @Override
        public void cycleNextEncoding() {
            if (encodingsHandler != null) {
                encodingsHandler.cycleNextEncoding();
            }
        }

        @Override
        public void cyclePreviousEncoding() {
            if (encodingsHandler != null) {
                encodingsHandler.cyclePreviousEncoding();
            }
        }

        @Override
        public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
            if (encodingsHandler != null) {
                encodingsHandler.popupEncodingsMenu(mouseEvent);
            }
        }

        @Override
        public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (activeFile.isPresent()) {
                BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                FileHandlingMode fileHandlingMode = fileHandler.getFileHandlingMode();
                FileHandlingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY;
                if (newHandlingMode != fileHandlingMode) {
                    OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                    BinaryViewerOptions optionsStorage = new BinaryViewerOptions(optionsModule.getAppOptions());
                    if (editorProvider.releaseFile(fileHandler)) {
                        fileHandler.switchFileHandlingMode(newHandlingMode);
                        // TODO preferences.getEditorOptions().setFileHandlingMode(newHandlingMode);
                    }
                    ((BinEdEditorProvider) editorProvider).updateStatus();
                }
            }
        }
    }
}
