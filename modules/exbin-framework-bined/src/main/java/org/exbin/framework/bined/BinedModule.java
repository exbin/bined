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

import org.exbin.framework.bined.action.ClipboardCodeActions;
import org.exbin.framework.bined.action.ShowNonprintablesActions;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.api.EditorProviderVariant;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.toolbar.api.GroupToolBarContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.toolbar.api.PositionToolBarContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.menu.api.RelativeMenuContributionRule;
import org.exbin.framework.toolbar.api.ToolBarContribution;
import org.exbin.framework.toolbar.api.ToolBarManagement;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.menu.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
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

    private static final String BINED_TOOL_BAR_GROUP_ID = MODULE_ID + ".binedToolBarGroup";

    public static final String BINARY_STATUS_BAR_ID = "binaryStatusBar";
    
    private java.util.ResourceBundle resourceBundle = null;

    private EditorProvider editorProvider;
    private BinEdFileManager fileManager = new BinEdFileManager();

    private ShowNonprintablesActions showNonprintablesActions;
    private ClipboardCodeActions clipboardCodeActions;
    private PopupMenuVariant popupMenuVariant = PopupMenuVariant.NORMAL;
    private BasicCodeAreaZone popupMenuPositionZone = BasicCodeAreaZone.UNKNOWN;

    public BinedModule() {
    }

    public void initEditorProvider(EditorProviderVariant variant) {
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
//            editorFile.onInitFromPreferences(new BinaryViewerOptions(preferencesModule.getAppPreferences()));
//            EditorOptions editorPreferences = new EditorOptions(preferencesModule.getAppPreferences());
//            FileHandlingMode fileHandlingMode = editorPreferences.getFileHandlingMode();
//            editorFile.setNewData(fileHandlingMode);
            editorFile.setNewData(FileHandlingMode.MEMORY);

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
//            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
//            EditorOptions editorPreferences = new EditorOptions(preferencesModule.getAppPreferences());
//            FileHandlingMode fileHandlingMode = editorPreferences.getFileHandlingMode();
//            ((BinaryMultiEditorProvider) editorProvider).setDefaultFileHandlingMode(fileHandlingMode);
            ((BinaryMultiEditorProvider) editorProvider).setDefaultFileHandlingMode(FileHandlingMode.MEMORY);
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

    public void registerUndoHandler() {
        ((BinEdEditorProvider) editorProvider).registerUndoHandler();
    }

    public void registerGoToPosition() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(EDIT_OPERATION_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(EDIT_FIND_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuItem(createGoToPositionAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(EDIT_FIND_MENU_GROUP_ID));
    }

    @Nullable
    public BinaryStatusPanel getBinaryStatusPanel() {
        return fileManager.getBinaryStatusPanel();
    }

    @Nonnull
    private Action getOptionsAction() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        Action optionsAction = optionsModule.createOptionsAction();
        optionsAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });
        return optionsAction;
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
    public GoToPositionAction createGoToPositionAction() {
        ensureSetup();
        GoToPositionAction goToPositionAction = new GoToPositionAction();
        goToPositionAction.setup(resourceBundle);
        goToPositionAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                return popupMenuVariant != PopupMenuVariant.BASIC;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });
        return goToPositionAction;
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

    public void registerShowNonprintablesToolBarActions() {
        getShowNonprintablesActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarManagement mgmt = toolBarModule.getMainToolBarManagement(MODULE_ID);
        ToolBarContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionToolBarContributionRule(PositionToolBarContributionRule.PositionMode.MIDDLE));
        contribution = mgmt.registerToolBarItem(showNonprintablesActions.createViewNonprintablesToolbarAction());
        mgmt.registerToolBarRule(contribution, new GroupToolBarContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewNonprintablesMenuActions() {
        getShowNonprintablesActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(VIEW_NONPRINTABLES_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(showNonprintablesActions.createViewNonprintablesAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(VIEW_NONPRINTABLES_MENU_GROUP_ID));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(clipboardCodeActions.createCopyAsCodeAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeMenuContributionRule(RelativeMenuContributionRule.NextToMode.AFTER, "copyAction"));
        contribution = mgmt.registerMenuItem(clipboardCodeActions.createPasteFromCodeAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeMenuContributionRule(RelativeMenuContributionRule.NextToMode.AFTER, "pasteAction"));
    }

    public void registerCodeAreaPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        MenuManagement mgmt = menuModule.getMenuManagement(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();

        MenuContribution contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_VIEW_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_EDIT_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_SELECTION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_OPERATION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_FIND_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_TOOLS_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.AROUND));

        contribution = mgmt.registerMenuItem(clipboardActions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(getClipboardCodeActions().createCopyAsCodeAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(getClipboardCodeActions().createPasteFromCodeAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));

        contribution = mgmt.registerMenuItem(clipboardActions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_SELECTION_GROUP_ID));

        contribution = mgmt.registerMenuItem(createGoToPositionAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_FIND_GROUP_ID));

        contribution = mgmt.registerMenuItem(getOptionsAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(CODE_AREA_POPUP_TOOLS_GROUP_ID));
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
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
//        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
//        MenuManagement mgmt = menuModule.getMenuManagement(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);

        popupMenuVariant = variant;
        popupMenuPositionZone = codeArea.getPainter().getPositionZone(x, y);

        final JPopupMenu popupMenu = UiUtils.createPopupMenu();
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActionContextService actionContextService = frameModule.getFrameHandler().getActionContextService();
        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, actionContextService);
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
//        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
//        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID);
//        mgmt.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
    }

    public void loadFromOptions(OptionsStorage options) {
        // TODO encodingsHandler.loadFromOptions(new TextEncodingOptions(options));
        fileManager.loadFromOptions(options);
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
        MenuPopupModuleApi menuPopupModule = App.getModule(MenuPopupModuleApi.class);
        menuPopupModule.addComponentPopupEventDispatcher(new ComponentPopupEventDispatcher() {

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
