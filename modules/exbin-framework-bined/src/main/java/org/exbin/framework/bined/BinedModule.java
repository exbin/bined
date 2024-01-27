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
import org.exbin.framework.bined.action.ShowUnprintablesActions;
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
import java.util.ArrayList;
import java.util.List;
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
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
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
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.bined.action.CodeAreaAction;
import org.exbin.framework.bined.action.EditSelectionAction;
import org.exbin.framework.bined.action.ReloadFileAction;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ObjectUtils;
import org.exbin.framework.window.api.WindowModuleApi;

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
    public static final String VIEW_UNPRINTABLES_MENU_GROUP_ID = MODULE_ID + ".viewUnprintablesMenuGroup";

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

    private ShowUnprintablesActions showUnprintablesActions;
    private CodeAreaFontAction codeAreaFontAction;
    private RowWrappingAction rowWrappingAction;
    private GoToPositionAction goToPositionAction;
    private EditSelectionAction editSelectionAction;
    private PropertiesAction propertiesAction;
    private ReloadFileAction reloadFileAction;
    private PrintAction printAction;
    private ViewModeHandlerActions viewModeActions;
    private ShowRowPositionAction showRowPositionAction;
    private ShowHeaderAction showHeaderAction;
    private CodeTypeActions codeTypeActions;
    private PositionCodeTypeActions positionCodeTypeActions;
    private HexCharactersCaseActions hexCharactersCaseActions;
    private ClipboardCodeActions clipboardCodeActions;
    private EncodingsHandler encodingsHandler;
    private PopupMenuVariant popupMenuVariant = PopupMenuVariant.NORMAL;
    private BasicCodeAreaZone popupMenuPositionZone = BasicCodeAreaZone.UNKNOWN;

    private final List<CodeAreaAction> codeAreaActions = new ArrayList<>();

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
            fileManager.initFileHandler(editorFile);

            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            EditorPreferences editorPreferences = new EditorPreferences(preferencesModule.getAppPreferences());
            FileHandlingMode fileHandlingMode = editorPreferences.getFileHandlingMode();
            editorFile.setNewData(fileHandlingMode);

            editorProvider = new BinaryEditorProvider(editorFile);
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            fileModule.setFileOperations(editorProvider);

            BinEdComponentPanel componentPanel = editorFile.getComponent();
            ExtCodeArea codeArea = editorFile.getComponent().getCodeArea();
            codeArea.addSelectionChangedListener(this::updateClipboardActionStatus);
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
            fileModule.setFileOperations(editorProvider);
            ((BinaryMultiEditorProvider) editorProvider).setCodeAreaPopupMenuHandler(createCodeAreaPopupMenuHandler(PopupMenuVariant.EDITOR));

            ((MultiEditorProvider) editorProvider).addActiveFileChangeListener(e -> {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                CodeAreaCore codeArea = activeFile.isPresent() ? ((BinEdFileHandler) activeFile.get()).getCodeArea() : null;
                updateActionStatus(codeArea);
            });
            ((BinaryMultiEditorProvider) editorProvider).setClipboardActionsUpdateListener(() -> {
                updateClipboardActionStatus();
            });
            fileModule.setFileOperations(editorProvider);
        }

        return editorProvider;
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
        editorModule.updateActionStatus();
        FileDependentAction[] fileDepActions = new FileDependentAction[]{
            codeAreaFontAction, propertiesAction, reloadFileAction
        };
        for (FileDependentAction fileDepAction : fileDepActions) {
            if (fileDepAction != null) {
                fileDepAction.updateForActiveFile();
            }
        }

        CodeAreaAction[] binedCodeAreaActions = new CodeAreaAction[]{
            goToPositionAction, editSelectionAction,
            hexCharactersCaseActions, codeTypeActions, positionCodeTypeActions,
            rowWrappingAction, viewModeActions, showHeaderAction,
            showRowPositionAction, showUnprintablesActions,
            clipboardCodeActions, printAction
        };
        for (CodeAreaAction codeAreaAction : binedCodeAreaActions) {
            if (codeAreaAction != null) {
                codeAreaAction.updateForActiveCodeArea(codeArea);
            }
        }

        for (CodeAreaAction codeAreaAction : codeAreaActions) {
            if (codeAreaAction != null) {
                codeAreaAction.updateForActiveCodeArea(codeArea);
            }
        }

        fileManager.updateActionStatus(codeArea);

        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        fileModule.updateForFileOperations();
    }

    public void updateClipboardActionStatus() {
        if (clipboardCodeActions != null) {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            CodeAreaCore codeArea = null;
            if (activeFile.isPresent()) {
                FileHandler fileHandler = activeFile.get();
                if (fileHandler instanceof BinEdFileHandler) {
                    codeArea = ((BinEdFileHandler) fileHandler).getCodeArea();
                }
            }
            clipboardCodeActions.updateForActiveCodeArea(codeArea);
        }
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
                if (goToPositionAction != null) {
                    goToPositionAction.actionPerformed(null);
                }
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
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, encodingsHandler.getToolsEncodingMenu(), new MenuPosition(PositionMode.TOP_LAST));
    }

    public void registerOptionsPanels() {
        BinaryAppearanceService binaryAppearanceService = new BinaryAppearanceServiceImpl(this, editorProvider);
        getMainOptionsManager().registerOptionsPanels(getEncodingsHandler(), fileManager, binaryAppearanceService, getCodeTypeActions(), getShowUnprintablesActions(), getHexCharactersCaseActions(), getPositionCodeTypeActions(), getViewModeActions());
    }

    public void registerWordWrapping() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, getRowWrappingAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerGoToPosition() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(WindowModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_OPERATION_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuGroup(WindowModuleApi.EDIT_MENU_ID, new MenuGroup(EDIT_FIND_MENU_GROUP_ID, new MenuPosition(PositionMode.MIDDLE), SeparationMode.AROUND));
        actionModule.registerMenuItem(WindowModuleApi.EDIT_MENU_ID, MODULE_ID, getGoToPositionAction(), new MenuPosition(EDIT_FIND_MENU_GROUP_ID));
    }

    public void registerEditSelection() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.EDIT_MENU_ID, MODULE_ID, getEditSelectionAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    @Nullable
    public BinaryStatusPanel getBinaryStatusPanel() {
        return fileManager.getBinaryStatusPanel();
    }

    @Nonnull
    private AbstractAction getShowHeaderAction() {
        if (showHeaderAction == null) {
            ensureSetup();
            showHeaderAction = new ShowHeaderAction();
            showHeaderAction.setup(resourceBundle);
            showHeaderAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
        }

        return showHeaderAction;
    }

    @Nonnull
    private AbstractAction getShowRowPositionAction() {
        if (showRowPositionAction == null) {
            ensureSetup();
            showRowPositionAction = new ShowRowPositionAction();
            showRowPositionAction.setup(resourceBundle);
            showRowPositionAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
        }

        return showRowPositionAction;
    }

    @Nonnull
    private Action getOptionsAction() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        Action optionsAction = optionsModule.getOptionsAction();
        optionsAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
    public RowWrappingAction getRowWrappingAction() {
        if (rowWrappingAction == null) {
            ensureSetup();
            rowWrappingAction = new RowWrappingAction();
            rowWrappingAction.setup(resourceBundle);
        }

        return rowWrappingAction;
    }

    @Nonnull
    public ShowUnprintablesActions getShowUnprintablesActions() {
        if (showUnprintablesActions == null) {
            ensureSetup();
            showUnprintablesActions = new ShowUnprintablesActions();
            showUnprintablesActions.setup(resourceBundle);
        }

        return showUnprintablesActions;
    }

    @Nonnull
    public CodeAreaFontAction getCodeAreaFontAction() {
        if (codeAreaFontAction == null) {
            ensureSetup();
            codeAreaFontAction = new CodeAreaFontAction();
            codeAreaFontAction.setup(editorProvider, resourceBundle);
        }

        return codeAreaFontAction;
    }

    @Nonnull
    public GoToPositionAction getGoToPositionAction() {
        if (goToPositionAction == null) {
            ensureSetup();
            goToPositionAction = new GoToPositionAction();
            goToPositionAction.setup(resourceBundle);
            goToPositionAction.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
                @Override
                public boolean shouldCreate(String menuId) {
                    return popupMenuVariant != PopupMenuVariant.BASIC;
                }

                @Override
                public void onCreate(JMenuItem menuItem, String menuId) {
                }
            });
        }

        return goToPositionAction;
    }

    @Nonnull
    public EditSelectionAction getEditSelectionAction() {
        if (editSelectionAction == null) {
            ensureSetup();
            editSelectionAction = new EditSelectionAction();
            editSelectionAction.setup(resourceBundle);
        }

        return editSelectionAction;
    }

    @Nonnull
    public PropertiesAction getPropertiesAction() {
        if (propertiesAction == null) {
            ensureSetup();
            propertiesAction = new PropertiesAction();
            propertiesAction.setup(editorProvider, resourceBundle);
        }

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
    private ReloadFileAction getReloadFileAction() {
        if (reloadFileAction == null) {
            ensureSetup();
            reloadFileAction = new ReloadFileAction();
            reloadFileAction.setup(editorProvider, resourceBundle);
        }

        return reloadFileAction;
    }

    @Nonnull
    public PrintAction getPrintAction() {
        if (printAction == null) {
            ensureSetup();
            printAction = new PrintAction();
            printAction.setup(resourceBundle);
        }

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
        actionModule.registerToolBarGroup(WindowModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(BINED_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.ABOVE));
        actionModule.registerToolBarItem(WindowModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, codeTypeActions.getCycleCodeTypesAction(), new ToolBarPosition(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerShowUnprintablesToolBarActions() {
        getShowUnprintablesActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerToolBarGroup(WindowModuleApi.MAIN_TOOL_BAR_ID, new ToolBarGroup(BINED_TOOL_BAR_GROUP_ID, new ToolBarPosition(PositionMode.MIDDLE), SeparationMode.NONE));
        actionModule.registerToolBarItem(WindowModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID, showUnprintablesActions.getViewUnprintablesToolbarAction(), new ToolBarPosition(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewUnprintablesMenuActions() {
        getShowUnprintablesActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuGroup(WindowModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_UNPRINTABLES_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM), SeparationMode.NONE));
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, showUnprintablesActions.getViewUnprintablesAction(), new MenuPosition(VIEW_UNPRINTABLES_MENU_GROUP_ID));
    }

    public void registerToolsOptionsMenuActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, getCodeAreaFontAction(), new MenuPosition(PositionMode.BOTTOM_LAST));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.EDIT_MENU_ID, MODULE_ID, clipboardCodeActions.getCopyAsCodeAction(), new MenuPosition(NextToMode.AFTER, (String) actionModule.getClipboardActions().getCopyAction().getValue(Action.NAME)));
        actionModule.registerMenuItem(WindowModuleApi.EDIT_MENU_ID, MODULE_ID, clipboardCodeActions.getPasteFromCodeAction(), new MenuPosition(NextToMode.AFTER, (String) actionModule.getClipboardActions().getPasteAction().getValue(Action.NAME)));
    }

    public void registerPropertiesMenu() {
        getPropertiesAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.FILE_MENU_ID, MODULE_ID, getPropertiesAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerReloadFileMenu() {
        getReloadFileAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.FILE_MENU_ID, MODULE_ID, getReloadFileAction(), new MenuPosition(PositionMode.BOTTOM));
    }

    public void registerPrintMenu() {
        getPrintAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.FILE_MENU_ID, MODULE_ID, getPrintAction(), new MenuPosition(PositionMode.BOTTOM));
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
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, VIEW_MODE_SUBMENU_ID, viewSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(VIEW_MODE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.getDualModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.getCodeMatrixModeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(VIEW_MODE_SUBMENU_ID, MODULE_ID, viewModeActions.getTextPreviewModeAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerLayoutMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, getShowHeaderAction(), new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, getShowRowPositionAction(), new MenuPosition(PositionMode.BOTTOM));
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
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, CODE_TYPE_SUBMENU_ID, codeTypeSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(CODE_TYPE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.getBinaryCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(CODE_TYPE_SUBMENU_ID, MODULE_ID, codeTypeActions.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
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
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_SUBMENU_ID, positionCodeTypeSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.getOctalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.getDecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_SUBMENU_ID, MODULE_ID, positionCodeTypeActions.getHexadecimalCodeTypeAction(), new MenuPosition(PositionMode.TOP));
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
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, HEX_CHARACTERS_CASE_SUBMENU_ID, hexCharsCaseSubMenuAction, new MenuPosition(PositionMode.BOTTOM));
        actionModule.registerMenu(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID);
        actionModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseActions.getUpperHexCharsAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(HEX_CHARACTERS_CASE_SUBMENU_ID, MODULE_ID, hexCharactersCaseActions.getLowerHexCharsAction(), new MenuPosition(PositionMode.TOP));
    }

    public void registerEditSelectionAction() {
        getEditSelectionAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenuItem(WindowModuleApi.EDIT_MENU_ID, MODULE_ID, getEditSelectionAction(), new MenuPosition(ActionModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
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

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getShowHeaderAction(), new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getShowRowPositionAction(), new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));

        actionModule.registerMenu(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID);
        Action positionCodeTypeSubMenuAction = new AbstractAction(resourceBundle.getString("positionCodeTypeSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, POSITION_CODE_TYPE_POPUP_SUBMENU_ID, positionCodeTypeSubMenuAction, new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, getOctalPositionTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, getDecimalPositionTypeAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(POSITION_CODE_TYPE_POPUP_SUBMENU_ID, MODULE_ID, getHexadecimalPositionTypeAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.getCutAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.getCopyAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getClipboardCodeActions().getCopyAsCodeAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.getPasteAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getClipboardCodeActions().getPasteFromCodeAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.getDeleteAction(), new MenuPosition(CODE_AREA_POPUP_EDIT_GROUP_ID));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, clipboardActions.getSelectAllAction(), new MenuPosition(CODE_AREA_POPUP_SELECTION_GROUP_ID));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getEditSelectionAction(), new MenuPosition(CODE_AREA_POPUP_SELECTION_GROUP_ID));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getGoToPositionAction(), new MenuPosition(CODE_AREA_POPUP_FIND_GROUP_ID));

        actionModule.registerMenu(SHOW_POPUP_SUBMENU_ID, MODULE_ID);
        Action popupShowSubMenuAction = new AbstractAction(resourceBundle.getString("popupShowSubMenu.text")) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        popupShowSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("popupShowSubMenu.shortDescription"));
        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, SHOW_POPUP_SUBMENU_ID, popupShowSubMenuAction, new MenuPosition(CODE_AREA_POPUP_VIEW_GROUP_ID));
        actionModule.registerMenuItem(SHOW_POPUP_SUBMENU_ID, MODULE_ID, getShowHeaderAction(), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(SHOW_POPUP_SUBMENU_ID, MODULE_ID, getShowRowPositionAction(), new MenuPosition(PositionMode.TOP));

        actionModule.registerMenuItem(CODE_AREA_POPUP_MENU_ID, MODULE_ID, getOptionsAction(), new MenuPosition(CODE_AREA_POPUP_TOOLS_GROUP_ID));
    }

    @Nonnull
    private Action getOctalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.OCTAL);
            }
        };
        ActionUtils.setupAction(action, resourceBundle, PositionCodeTypeActions.OCTAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        action.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
    private Action getDecimalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        };
        ActionUtils.setupAction(action, resourceBundle, PositionCodeTypeActions.DECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        action.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
    private Action getHexadecimalPositionTypeAction() {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActiveCodeArea().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        };
        ActionUtils.setupAction(action, resourceBundle, PositionCodeTypeActions.HEXADECIMAL_POSITION_CODE_TYPE_ACTION_ID);
        action.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.RADIO);
        action.putValue(ActionUtils.ACTION_MENU_CREATION, new ActionUtils.MenuCreation() {
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
    public ExtCodeArea getActiveCodeArea() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            return ((BinEdFileHandler) activeFile.get()).getComponent().getCodeArea();
        }
        throw new IllegalStateException("No active file");
    }

    @Nonnull
    private JPopupMenu createPopupMenu(int postfix, ExtCodeArea codeArea) {
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
    private JPopupMenu createCodeAreaPopupMenu(final ExtCodeArea codeArea, String menuPostfix, PopupMenuVariant variant, int x, int y) {
        getClipboardCodeActions();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.registerMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);

        popupMenuVariant = variant;
        popupMenuPositionZone = codeArea.getPainter().getPositionZone(x, y);

        final JPopupMenu popupMenu = new JPopupMenu();
        actionModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID);
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
            @Override
            public JPopupMenu createPopupMenu(ExtCodeArea codeArea, String menuPostfix, int x, int y) {
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
                if (component instanceof ExtCodeArea) {
                    if (((ExtCodeArea) component).getComponentPopupMenu() == null) {
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

                        popupMenu = handler.createPopupMenu((ExtCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

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

                if (component instanceof ExtCodeArea) {
                    if (((ExtCodeArea) component).getComponentPopupMenu() == null) {
                        CodeAreaPopupMenuHandler handler = createCodeAreaPopupMenuHandler(PopupMenuVariant.NORMAL);
                        if (popupMenu != null) {
                            handler.dropPopupMenu(DEFAULT_MENU_POSTFIX);
                        }

                        Point point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                        int x = (int) point.getX();
                        int y = (int) point.getY();
                        popupMenu = handler.createPopupMenu((ExtCodeArea) component, DEFAULT_MENU_POSTFIX, x, y);

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

    public void addCodeAreaAction(CodeAreaAction codeAreaAction) {
        codeAreaActions.add(codeAreaAction);
    }

    public void removeCodeAreaAction(CodeAreaAction codeAreaAction) {
        codeAreaActions.remove(codeAreaAction);
    }

    public enum PopupMenuVariant {
        BASIC, NORMAL, EDITOR
    }
}
