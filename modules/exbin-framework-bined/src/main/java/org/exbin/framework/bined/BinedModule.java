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
package org.exbin.framework.bined;

import org.exbin.framework.bined.action.ShowRowPositionAction;
import org.exbin.framework.bined.action.ClipboardCodeActions;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.action.CodeTypeActions;
import org.exbin.framework.bined.action.CodeAreaFontAction;
import org.exbin.framework.bined.action.ViewModeHandlerActions;
import org.exbin.framework.bined.action.PrintAction;
import org.exbin.framework.bined.action.ShowNonprintablesActions;
import org.exbin.framework.bined.action.RowWrappingAction;
import org.exbin.framework.bined.action.PropertiesAction;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.action.HexCharactersCaseActions;
import org.exbin.framework.bined.action.PositionCodeTypeActions;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.bined.EditOperation;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionMenuCreation;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.bined.action.ShowHeaderAction;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.NextToMode;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.action.api.ToolBarGroup;
import org.exbin.framework.action.api.ToolBarPosition;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.bined.preferences.EditorPreferences;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;
import org.exbin.framework.bined.service.BinaryAppearanceService;
import org.exbin.framework.bined.service.impl.BinaryAppearanceServiceImpl;
import org.exbin.framework.editor.text.service.TextEncodingService;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ComponentActivationService;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.bined.action.EditSelectionAction;
import org.exbin.framework.bined.action.ReloadFileAction;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.utils.UiUtils;

/**
 * Binary data editor module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedModule.class);

    public static final String EDIT_FIND_MENU_GROUP_ID = MODULE_ID + ".editFindMenuGroup";
    public static final String EDIT_OPERATION_MENU_GROUP_ID = MODULE_ID + ".editOperationMenuGroup";
    public static final String VIEW_NONPRINTABLES_MENU_GROUP_ID = MODULE_ID + ".viewNonprintablesMenuGroup";

    public static final String BINARY_POPUP_MENU_ID = MODULE_ID + ".binaryPopupMenu";
    public static final String CODE_AREA_POPUP_MENU_ID = MODULE_ID + ".codeAreaPopupMenu";
    public static final String CODE_AREA_POPUP_VIEW_GROUP_ID = MODULE_ID + ".viewPopupMenuGroup";
    public static final String CODE_AREA_POPUP_EDIT_GROUP_ID = MODULE_ID + ".editPopupMenuGroup";
    public static final String CODE_AREA_POPUP_SELECTION_GROUP_ID = MODULE_ID + ".selectionPopupMenuGroup";
    public static final String CODE_AREA_POPUP_OPERATION_GROUP_ID = MODULE_ID + ".operationPopupMenuGroup";
    public static final String CODE_AREA_POPUP_FIND_GROUP_ID = MODULE_ID + ".findPopupMenuGroup";
    public static final String CODE_AREA_POPUP_TOOLS_GROUP_ID = MODULE_ID + ".toolsPopupMenuGroup";

    public static final String VIEW_MODE_SUBMENU_ID = MODULE_ID + ".viewModeSubMenu";
    public static final String CODE_TYPE_SUBMENU_ID = MODULE_ID + ".codeTypeSubMenu";
    public static final String POSITION_CODE_TYPE_SUBMENU_ID = MODULE_ID + ".positionCodeTypeSubMenu";
    public static final String HEX_CHARACTERS_CASE_SUBMENU_ID = MODULE_ID + ".hexCharactersCaseSubMenu";
    public static final String POSITION_CODE_TYPE_POPUP_SUBMENU_ID = MODULE_ID + ".positionCodeTypePopupSubMenu";
    public static final String SHOW_POPUP_SUBMENU_ID = MODULE_ID + ".showPopupSubMenu";

    private static final String BINED_TOOL_BAR_GROUP_ID = MODULE_ID + ".binedToolBarGroup";

    public static final String BINARY_STATUS_BAR_ID = "binaryStatusBar";

    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;
    private BinEdFileManager fileManager;
    private BinedOptionsManager binedOptionsManager;

    private ShowNonprintablesActions showNonprintablesActions;
    private ViewModeHandlerActions viewModeActions;
    private CodeTypeActions codeTypeActions;
    private PositionCodeTypeActions positionCodeTypeActions;
    private HexCharactersCaseActions hexCharactersCaseActions;
    private ClipboardCodeActions clipboardCodeActions;
    private EncodingsHandler encodingsHandler;
    private PopupMenuVariant popupMenuVariant = PopupMenuVariant.NORMAL;
    private BasicCodeAreaZone popupMenuPositionZone = BasicCodeAreaZone.UNKNOWN;

    public BinedModule() {
    }

    public void initEditorProvider(EditorProviderVariant variant) {
        fileManager = new BinEdFileManager();
        switch (variant) {
            case SINGLE: {
                editorProvider = createSingleEditorProvider();
                break;
            }
            case MULTI: {
                editorProvider = createMultiEditorProvider();
                break;
            }
            default:
                throw ObjectUtils.getInvalidTypeException(variant);
        }
        fileManager.setEditorProvider(editorProvider);
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        if (fileManager == null) {
            fileManager = new BinEdFileManager();
        }

        this.editorProvider = editorProvider;
        fileManager.setEditorProvider(editorProvider);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    private EditorProvider createSingleEditorProvider() {
        if (editorProvider == null) {

            BinEdFileHandler editorFile = new BinEdFileHandler();
            editorProvider = new BinaryEditorProvider(editorFile);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            fileModule.setFileOperations(editorProvider);

            fileManager.initFileHandler(editorFile);

            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            EditorPreferences editorPreferences = new EditorPreferences(preferencesModule.getAppPreferences());
            FileHandlingMode fileHandlingMode = editorPreferences.getFileHandlingMode();
            editorFile.setNewData(fileHandlingMode);

            BinEdComponentPanel componentPanel = editorFile.getComponent();
            SectCodeArea codeArea = editorFile.getComponent().getCodeArea();
            codeArea.addSelectionChangedListener(() -> {

            });
            componentPanel.setPopupMenu(createPopupMenu(editorFile.getId(), codeArea));
        }

        return editorProvider;
    }

    @Nonnull
    private EditorProvider createMultiEditorProvider() {
        if (editorProvider == null) {
            editorProvider = new BinaryMultiEditorProvider();
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            EditorPreferences editorPreferences = new EditorPreferences(preferencesModule.getAppPreferences());
            FileHandlingMode fileHandlingMode = editorPreferences.getFileHandlingMode();
            ((BinaryMultiEditorProvider) editorProvider).setDefaultFileHandlingMode(fileHandlingMode);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            ((BinaryMultiEditorProvider) editorProvider).setCodeAreaPopupMenuHandler(createCodeAreaPopupMenuHandler(PopupMenuVariant.EDITOR));

            fileModule.setFileOperations(editorProvider);
        }

        return editorProvider;
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
    public BinedOptionsManager getMainOptionsManager() {
        if (binedOptionsManager == null) {
            binedOptionsManager = new BinedOptionsManager();
            binedOptionsManager.setEditorProvider(editorProvider);
        }
        return binedOptionsManager;
    }

    public void registerStatusBar() {
        fileManager.registerStatusBar();
        fileManager.setStatusControlHandler(new BinaryStatusPanel.StatusControlHandler() {
            @Override
            public void changeEditOperation(EditOperation editOperation) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    ((BinEdFileHandler) activeFile.get()).getCodeArea().setEditOperation(editOperation);
                }
            }

            @Override
            public void changeCursorPosition() {
                // TODO
//                if (goToPositionAction != null) {
//                    goToPositionAction.actionPerformed(null);
//                }
            }

            @Override
            public void cycleEncodings() {
                if (encodingsHandler != null) {
                    encodingsHandler.cycleEncodings();
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
                        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                        BinaryEditorPreferences preferences = new BinaryEditorPreferences(preferencesModule.getAppPreferences());
                        if (editorProvider.releaseFile(fileHandler)) {
                            fileHandler.switchFileHandlingMode(newHandlingMode);
                            preferences.getEditorPreferences().setFileHandlingMode(newHandlingMode);
                        }
                        ((BinEdEditorProvider) editorProvider).updateStatus();
                    }
                }
            }
        });

        if (encodingsHandler != null) {
            fileManager.updateTextEncodingStatus(encodingsHandler);
        }
    }

    public void registerEncodings() {
        getEncodingsHandler();
        encodingsHandler.rebuildEncodings();

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        BinaryAppearanceService binaryAppearanceService = new BinaryAppearanceServiceImpl(this, editorProvider);
        getMainOptionsManager().registerOptionsPanels(getEncodingsHandler(), fileManager, binaryAppearanceService, getCodeTypeActions(), getShowNonprintablesActions(), getHexCharactersCaseActions(), getPositionCodeTypeActions(), getViewModeActions());
    }

    public void registerUndoHandler() {
        ((BinEdEditorProvider) editorProvider).registerUndoHandler();
    }

    public void registerWordWrapping() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, createRowWrappingAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToPosition() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(ActionConsts.EDIT_MENU_ID, new MenuGroup(EDIT_OPERATION_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(ActionConsts.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, createGoToPositionAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditSelection() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, createEditSelectionAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    @Nullable
    public BinaryStatusPanel getBinaryStatusPanel() {
        return fileManager.getBinaryStatusPanel();
    }

    @Nonnull
    private AbstractAction createShowHeaderAction() {
        ensureSetup();
        ShowHeaderAction showHeaderAction = new ShowHeaderAction();
        showHeaderAction.setup(resourceBundle);
        showHeaderAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(menuId);
                return popupMenuVariant == PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
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
            public boolean shouldCreate(String menuId) {
                boolean inShowSubmenu = SHOW_POPUP_SUBMENU_ID.equals(menuId);
                return popupMenuVariant == PopupMenuVariant.EDITOR && ((inShowSubmenu && popupMenuPositionZone == BasicCodeAreaZone.CODE_AREA) || (!inShowSubmenu && popupMenuPositionZone != BasicCodeAreaZone.CODE_AREA));
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
                menuItem.setSelected(Objects.requireNonNull(getActiveCodeArea().getLayoutProfile()).isShowRowPosition());
            }
        });
        return showRowPositionAction;
    }

    @Nonnull
    private Action getOptionsAction() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        Action optionsAction = optionsModule.createOptionsAction();
        optionsAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return optionsAction;
    }

    @Nonnull
    public RowWrappingAction createRowWrappingAction() {
        ensureSetup();
        RowWrappingAction rowWrappingAction = new RowWrappingAction();
        rowWrappingAction.setup(resourceBundle);
        return rowWrappingAction;
    }

    @Nonnull
    public ShowNonprintablesActions getShowNonprintablesActions() {
        if (showNonprintablesActions == null) {
            ensureSetup();
            showNonprintablesActions = new ShowNonprintablesActions();
            showNonprintablesActions.setup(resourceBundle);
        }

        return showNonprintablesActions;
    }

    @Nonnull
    public CodeAreaFontAction createCodeAreaFontAction() {
        ensureSetup();
        CodeAreaFontAction codeAreaFontAction = new CodeAreaFontAction();
        codeAreaFontAction.setup(editorProvider, resourceBundle);
        return codeAreaFontAction;
    }

    @Nonnull
    public GoToPositionAction createGoToPositionAction() {
        ensureSetup();
        GoToPositionAction goToPositionAction = new GoToPositionAction();
        goToPositionAction.setup(resourceBundle);
        goToPositionAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                return popupMenuVariant != PopupMenuVariant.BASIC;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return goToPositionAction;
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
    public EncodingsHandler getEncodingsHandler() {
        if (encodingsHandler == null) {
            ensureSetup();
            encodingsHandler = new EncodingsHandler();
            encodingsHandler.setParentComponent(editorProvider.getEditorComponent());
            fileManager.updateTextEncodingStatus(encodingsHandler);
            encodingsHandler.init();

            encodingsHandler.setEncodingChangeListener(new TextEncodingService.EncodingChangeListener() {
                @Override
                public void encodingListChanged() {
                    encodingsHandler.rebuildEncodings();
                }

                @Override
                public void selectedEncodingChanged() {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (activeFile.isPresent()) {
                        ((BinEdFileHandler) activeFile.get()).setCharset(Charset.forName(encodingsHandler.getSelectedEncoding()));
                    }
                }
            });
        }

        return encodingsHandler;
    }

    @Nonnull
    private ReloadFileAction createReloadFileAction() {
        ensureSetup();
        ReloadFileAction reloadFileAction = new ReloadFileAction();
        reloadFileAction.setup(resourceBundle);
        return reloadFileAction;
    }

    @Nonnull
    public PrintAction createPrintAction() {
        ensureSetup();
        PrintAction printAction = new PrintAction();
        printAction.setup(resourceBundle);
        return printAction;
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

    @Nonnull
    public ClipboardCodeActions getClipboardCodeActions() {
        if (clipboardCodeActions == null) {
            ensureSetup();
            clipboardCodeActions = new ClipboardCodeActions();
            clipboardCodeActions.setup(resourceBundle);
        }

        return clipboardCodeActions;
    }

    public void registerCodeTypeToolBarActions() {
        getCodeTypeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, new ToolBarGroup(BINED_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.ABOVE));
        actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, codeTypeActions.createCycleCodeTypesAction(), new ToolBarPosition(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerShowNonprintablesToolBarActions() {
        getShowNonprintablesActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerToolBarGroup(ActionConsts.MAIN_TOOL_BAR_ID, new ToolBarGroup(BINED_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.NONE));
        actionModule.registerToolBarItem(ActionConsts.MAIN_TOOL_BAR_ID, MODULE_ID, showNonprintablesActions.createViewNonprintablesToolbarAction(), new ToolBarPosition(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewNonprintablesMenuActions() {
        getShowNonprintablesActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(ActionConsts.VIEW_MENU_ID, new MenuGroup(VIEW_NONPRINTABLES_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, showNonprintablesActions.createViewNonprintablesAction(), new MenuPosition(VIEW_NONPRINTABLES_MENU_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, createCodeAreaFontAction(), new MenuPosition(PositionMode.BOTTOM_LAST));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, clipboardCodeActions.createCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) actionModule.getClipboardActions().createCopyAction().getValue(Action.NAME)));
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, clipboardCodeActions.createPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) actionModule.getClipboardActions().createPasteAction().getValue(Action.NAME)));
    }

    public void registerPropertiesMenu() {
        createPropertiesAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.FILE_MENU_ID, MODULE_ID, createPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerReloadFileMenu() {
        createReloadFileAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.FILE_MENU_ID, MODULE_ID, createReloadFileAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        createPrintAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.FILE_MENU_ID, MODULE_ID, createPrintAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerViewModeMenu() {
        getViewModeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        Action viewSubMenuAction = new AbstractAction(resourceBundle.getString("viewModeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        viewSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("viewModeSubMenu.shortDescription"));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, VIEW_MODE_SUBMENU_ID, viewSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(VIEW_MODE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.createDualModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.createCodeMatrixModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.createTextPreviewModeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerLayoutMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, createShowHeaderAction(), new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, createShowRowPositionAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerCodeTypeMenu() {
        getCodeTypeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        Action codeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("codeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        codeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("codeTypeSubMenu.shortDescription"));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(CODE_TYPE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.createBinaryCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.createOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.createDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.createHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerPositionCodeTypeMenu() {
        getPositionCodeTypeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_SUBMENU_ID, positionCodeTypeSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.createOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.createDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.createHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerHexCharactersCaseHandlerMenu() {
        getHexCharactersCaseActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        Action hexCharsCaseSubMenuAction = new AbstractAction(resourceBundle.getString("hexCharsCaseSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        hexCharsCaseSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexCharsCaseSubMenu.shortDescription"));
        actionModule.registerMenuItem(ActionConsts.VIEW_MENU_ID, MODULE_ID, HEX_CHARACTERS_CASE_SUBMENU_ID, hexCharsCaseSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseActions.createUpperHexCharsAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseActions.createLowerHexCharsAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerEditSelectionAction() {
        createEditSelectionAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(ActionConsts.EDIT_MENU_ID, MODULE_ID, createEditSelectionAction(), new MenuPosition(ActionModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
    }

    public void registerCodeAreaPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();

        actionModule.registerMenu(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_VIEW_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.AROUND));
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_EDIT_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_SELECTION_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_OPERATION_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_FIND_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(CODE_AREA_POPUP_MENU_ID, new MenuGroup(CODE_AREA_POPUP_TOOLS_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.AROUND));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, createShowHeaderAction(), new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, createShowRowPositionAction(), new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));

        actionModule.registerMenu(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID);
        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_POPUP_SUBMENU_ID, positionCodeTypeSubMenuAction, new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, createOctalPositionTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, createDecimalPositionTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, createHexadecimalPositionTypeAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.createCutAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.createCopyAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getClipboardCodeActions().createCopyAsCodeAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.createPasteAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getClipboardCodeActions().createPasteFromCodeAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.createDeleteAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.createSelectAllAction(), new MenuPosition(CODE_AREA_POPUP_SELECTION_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, createEditSelectionAction(), new MenuPosition(CODE_AREA_POPUP_SELECTION_GROUP_ID));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, createGoToPositionAction(), new MenuPosition(CODE_AREA_POPUP_FIND_GROUP_ID));

        actionModule.registerMenu(SHOW_POPUP_SUBMENU_ID, MODULE_ID);
        Action popupShowSubMenuAction = new AbstractAction(resourceBundle.getString("popupShowSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        popupShowSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("popupShowSubMenu.shortDescription"));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, SHOW_POPUP_SUBMENU_ID, popupShowSubMenuAction, new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(SHOW_POPUP_SUBMENU_ID, MODULE_ID, createShowHeaderAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SHOW_POPUP_SUBMENU_ID, MODULE_ID, createShowRowPositionAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getOptionsAction(), new MenuPosition(CODE_AREA_POPUP_TOOLS_GROUP_ID));
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
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.OCTAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
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
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.DECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
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
        actionModule.initAction(action, resourceBundle, PositionCodeTypeActions.HEXADECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionConsts.ACTION_TYPE, ActionType.RADIO);
        action.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR && (popupMenuPositionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || popupMenuPositionZone == BasicCodeAreaZone.HEADER || popupMenuPositionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
                menuItem.setSelected(getActiveCodeArea().getPositionCodeType() == PositionCodeType.HEXADECIMAL);
            }
        });

        return action;
    }

    public void start() {
        if (editorProvider instanceof MultiEditorProvider) {
            editorProvider.newFile();
        }
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }

    @Nonnull
    public BinEdFileManager getFileManager() {
        return fileManager;
    }

    @Nonnull
    public String getNewFileTitlePrefix() {
        return resourceBundle.getString("newFileTitlePrefix");
    }

    @Nonnull
    public SectCodeArea getActiveCodeArea() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            return ((BinEdFileHandler) activeFile.get()).getComponent().getCodeArea();
        }
        throw new IllegalStateException("No active file");
    }

    @Nonnull
    private JPopupMenu createPopupMenu(int postfix, SectCodeArea codeArea) {
        String popupMenuId = BINARY_POPUP_MENU_ID + "." + postfix;

        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += ((JViewport) invoker).getParent().getX();
                    clickedY += ((JViewport) invoker).getParent().getY();
                }
                CodeAreaPopupMenuHandler codeAreaPopupMenuHandler = createCodeAreaPopupMenuHandler(PopupMenuVariant.EDITOR);
                JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                popupMenu.show(invoker, x, y);
            }
        };
        return popupMenu;
    }

    @Nonnull
    private JPopupMenu createCodeAreaPopupMenu(final SectCodeArea codeArea, String menuPostfix, PopupMenuVariant variant, int x, int y) {
        getClipboardCodeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);

        popupMenuVariant = variant;
        popupMenuPositionZone = codeArea.getPainter().getPositionZone(x, y);

        final JPopupMenu popupMenu = UiUtils.createPopupMenu();
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ComponentActivationService componentActivationService = frameModule.getFrameHandler().getComponentActivationService();
        actionModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, componentActivationService);
        return popupMenu;
    }

    @Nonnull
    public PopupMenuVariant getPopupMenuVariant() {
        return popupMenuVariant;
    }

    @Nonnull
    public BasicCodeAreaZone getPopupMenuPositionZone() {
        return popupMenuPositionZone;
    }

    @Nonnull
    public JPopupMenu createBinEdComponentPopupMenu(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler, BinEdComponentPanel binaryPanel, int clickedX, int clickedY) {
        return codeAreaPopupMenuHandler.createPopupMenu(binaryPanel.getCodeArea(), "", clickedX, clickedY);
    }

    public void dropBinEdComponentPopupMenu() {
        dropCodeAreaPopupMenu("");
    }

    private void dropCodeAreaPopupMenu(String menuPostfix) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }

    public void loadFromPreferences(Preferences preferences) {
        encodingsHandler.loadFromPreferences(new TextEncodingPreferences(preferences));
        fileManager.loadFromPreferences(preferences);
    }

    @Nonnull
    public CodeAreaPopupMenuHandler createCodeAreaPopupMenuHandler(PopupMenuVariant variant) {
        return new CodeAreaPopupMenuHandler() {
            @Nonnull
            @Override
            public JPopupMenu createPopupMenu(SectCodeArea codeArea, String menuPostfix, int x, int y) {
                return createCodeAreaPopupMenu(codeArea, menuPostfix, variant, x, y);
            }

            @Override
            public void dropPopupMenu(String menuPostfix) {
                dropCodeAreaPopupMenu(menuPostfix);
            }
        };
    }

    public void registerCodeAreaPopupEventDispatcher() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.addComponentPopupEventDispatcher(new ComponentPopupEventDispatcher() {

            private static final String DEFAULT_MENU_POSTFIX = ".default";
            private JPopupMenu popupMenu = null;

            @Override
            public boolean dispatchMouseEvent(MouseEvent mouseEvent) {
                Component component = getSource(mouseEvent);
                if (component instanceof SectCodeArea) {
                    if (((SectCodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = createCodeAreaPopupMenuHandler(PopupMenuVariant.NORMAL);
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        int x;
                        int y;
                        Point point = component.getMousePosition();
                        if (point != null) {
                            x = (int) point.getX();
                            y = (int) point.getY();
                        } else {
                            x = mouseEvent.getX();
                            y = mouseEvent.getY();
                        }

                        popupMenu = handler.createPopupMenu((SectCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

                        if (point != null) {
                            popupMenu.show(component, x, y);
                        } else {
                            popupMenu.show(mouseEvent.getComponent(), x, y);
                        }
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if (component instanceof SectCodeArea) {
                    if (((SectCodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = createCodeAreaPopupMenuHandler(PopupMenuVariant.NORMAL);
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        Point point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                        int x = (int) point.getX();
                        int y = (int) point.getY();
                        popupMenu = handler.createPopupMenu((SectCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

                        popupMenu.show(component, x, y);
                        return true;
                    }
                }

                return false;
            }

            @Nullable
            private Component getSource(MouseEvent e) {
                return SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    public void registerCodeAreaCommandHandlerProvider(CodeAreaCommandHandlerProvider commandHandlerProvider) {
        fileManager.setCommandHandlerProvider(commandHandlerProvider);
    }

    public enum PopupMenuVariant {
        BASIC, NORMAL, EDITOR
    }
}
