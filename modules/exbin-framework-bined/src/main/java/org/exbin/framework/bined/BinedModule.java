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
import org.exbin.framework.bined.action.ViewFontActions;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.array.paged.ByteArrayPagedData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.action.api.ActionManagement;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.action.api.clipboard.ClipboardActionsApi;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.context.api.ContextModuleApi;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.RelativeSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContributionRule;
import org.exbin.framework.docking.api.ContextDocking;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentSource;
import org.exbin.framework.document.api.DocumentManagement;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.menu.popup.api.ComponentPopupEventDispatcher;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.document.api.DocumentType;
import org.exbin.framework.file.api.FileDocumentSource;

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
    public static final String VIEW_FONT_SUB_MENU_ID = MODULE_ID + ".viewFontSubMenu";
    public static final String VIEW_FONT_ZOOM_MENU_GROUP_ID = MODULE_ID + ".viewZoomMenuGroup";

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

    private BinEdFileManager fileManager = new BinEdFileManager();

    private ShowNonprintablesActions showNonprintablesActions;
    private ClipboardCodeActions clipboardCodeActions;
    private ViewFontActions viewFontActions;
    private PopupMenuVariant popupMenuVariant = PopupMenuVariant.NORMAL;
    private BasicCodeAreaZone popupMenuPositionZone = BasicCodeAreaZone.UNKNOWN;

    public BinedModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerGoToPosition() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(EDIT_OPERATION_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(EDIT_FIND_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuItem(createGoToPositionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID));
    }

    @Nonnull
    private Action getSettingsAction() {
        OptionsSettingsModuleApi optionsModule = App.getModule(OptionsSettingsModuleApi.class);

        Action settingsAction = optionsModule.createSettingsAction();
        settingsAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId) {
                return popupMenuVariant == PopupMenuVariant.EDITOR;
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId, String subMenuId) {
            }
        });
        return settingsAction;
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

    @Nonnull
    public ViewFontActions getViewFontActions() {
        if (viewFontActions == null) {
            ensureSetup();
            viewFontActions = new ViewFontActions();
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            org.exbin.framework.bined.settings.FontSizeOptions fontSizeOptions
                    = new org.exbin.framework.bined.settings.FontSizeOptions(optionsModule.getAppOptions());
            viewFontActions.setup(resourceBundle, fontSizeOptions);
        }

        return viewFontActions;
    }

    public void registerShowNonprintablesToolBarActions() {
        getShowNonprintablesActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        contribution = mgmt.registerToolBarItem(showNonprintablesActions.createViewNonprintablesToolbarAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewNonprintablesMenuActions() {
        getShowNonprintablesActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_NONPRINTABLES_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = mgmt.registerMenuItem(showNonprintablesActions.createViewNonprintablesAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_NONPRINTABLES_MENU_GROUP_ID));
    }

    public void registerDocument() {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        DocumentManagement documentManager = documentModule.getMainDocumentManager();
        documentManager.registerDocumentType(new DocumentType() {
            @Nonnull
            @Override
            public String getTypeId() {
                return "binary";
            }

            @Nonnull
            @Override
            public BinaryFileDocument createDefaultDocument() {
                BinaryFileDocument binaryFileDocument = new BinaryFileDocument();
//                if (fileHandlingMode == FileProcessingMode.DELTA) {
//                    SegmentsRepository segmentsRepository = fileManager.getSegmentsRepository();
//                    binaryFileDocument.setContentData(segmentsRepository.createDocument());
//                } else {
                binaryFileDocument.setContentData(new ByteArrayPagedData());
//                }
                return binaryFileDocument;
            }

            @Nonnull
            @Override
            public Optional<Document> openDocument(DocumentSource documentSource) {
                if (documentSource instanceof FileDocumentSource) {
                    BinaryFileDocument document = createDefaultDocument();
                    File file = ((FileDocumentSource) documentSource).getFile();

                    try {
                        BinaryData oldData = document.getContentData();
                        /*if (fileHandlingMode == FileProcessingMode.DELTA) {
                            FileDataSource openFileSource = new FileDataSource(file);
                            segmentsRepository.addDataSource(openFileSource);
                            DeltaDocument document = segmentsRepository.createDocument(openFileSource);
                            editorComponent.setContentData(document);
                            this.fileUri = fileUri;
                            oldData.dispose();
                        } else { */
                        try (FileInputStream fileStream = new FileInputStream(file)) {
                            BinaryData data = oldData;
                            if (!(data instanceof PagedData)) {
                                data = new ByteArrayPagedData();
                                oldData.dispose();
                            }
                            ((EditableBinaryData) data).loadFromStream(fileStream);
                            document.setContentData(data);
                        }
//                        }
                    } catch (IOException ex) {
                        Logger.getLogger(BinedModule.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return Optional.of(document);
                }

                return Optional.empty();
            }
        });
    }

    public void registerViewZoomMenuActions() {
        getViewFontActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SubSequenceContribution subContribution = mgmt.registerMenuItem(VIEW_FONT_SUB_MENU_ID, resourceBundle.getString("viewFontSubMenu.name"));
        mgmt.registerMenuRule(subContribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));

        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_FONT_ZOOM_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        contribution = mgmt.registerMenuItem(viewFontActions.createZoomInAction());
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(viewFontActions.createZoomOutAction());
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(viewFontActions.createResetFontSizeAction());
        mgmt.registerMenuRule(contribution, new SubSequenceContributionRule(subContribution.getContributionId()));
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_FONT_ZOOM_MENU_GROUP_ID));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(clipboardCodeActions.createCopyAsCodeAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "copyAction"));
        contribution = mgmt.registerMenuItem(clipboardCodeActions.createPasteFromCodeAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "pasteAction"));
    }

    public void registerCodeAreaPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        ClipboardActionsApi clipboardActions = actionModule.getClipboardActions();

        SequenceContribution contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_VIEW_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_EDIT_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_SELECTION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_OPERATION_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_FIND_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(CODE_AREA_POPUP_TOOLS_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));

        contribution = mgmt.registerMenuItem(clipboardActions.createCutAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createCopyAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(getClipboardCodeActions().createCopyAsCodeAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createPasteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(getClipboardCodeActions().createPasteFromCodeAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = mgmt.registerMenuItem(clipboardActions.createDeleteAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));

        contribution = mgmt.registerMenuItem(clipboardActions.createSelectAllAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_SELECTION_GROUP_ID));

        contribution = mgmt.registerMenuItem(createGoToPositionAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_FIND_GROUP_ID));

        contribution = mgmt.registerMenuItem(getSettingsAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_TOOLS_GROUP_ID));
    }

    public void start() {
        // TODO
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextManager = frameModule.getFrameHandler().getContextManager();
        ContextDocking contextDocking = contextManager.getActiveState(ContextDocking.class);
        if (contextDocking instanceof DocumentDocking) {
            ((DocumentDocking) contextDocking).openNewDocument();
        }

//        if (editorProvider instanceof MultiEditorProvider) {
//            editorProvider.newFile();
//        }
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
    private JPopupMenu createPopupMenu(int postfix, SectCodeArea codeArea) {
        String popupMenuId = BINARY_POPUP_MENU_ID + "." + postfix;

        JPopupMenu popupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
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
//        MenuManagement mgmt = menuModule.getMenuManager(CODE_AREA_POPUP_MENU_ID + menuPostfix, MODULE_ID);

        popupMenuVariant = variant;
        popupMenuPositionZone = codeArea.getPainter().getPositionZone(x, y);

        final JPopupMenu popupMenu = UiUtils.createPopupMenu();
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ActiveContextManagement contextManager = contextModule.createContextManager();
        BinEdDataComponent binEdDataComponent = new BinEdDataComponent(codeArea);
        binEdDataComponent.setContextProvider(contextManager);
        contextManager.changeActiveState(ContextComponent.class, binEdDataComponent);
        // TODO
//        contextManager.changeActiveState(ContextDocument.class, editorProvider);
//        contextManager.changeActiveState(ContextDocking.class, FileHandler.class, editorProvider.getActiveFile().orElse(null));
        contextManager.changeActiveState(DialogParentComponent.class, () -> codeArea);

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        ActionManagement actionManager = actionModule.createActionManager(contextManager);
        ActionContextRegistration actionContextRegistrar = actionModule.createActionContextRegistrar(actionManager);
        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, actionContextRegistrar);
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
//        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID);
//        mgmt.unregisterMenu(CODE_AREA_POPUP_MENU_ID + menuPostfix);
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
