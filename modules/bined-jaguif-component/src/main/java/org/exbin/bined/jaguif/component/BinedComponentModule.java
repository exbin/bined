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
package org.exbin.bined.jaguif.component;

import org.exbin.bined.jaguif.component.action.ClipboardCodeActions;
import org.exbin.bined.jaguif.component.action.ShowNonprintablesActions;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import org.exbin.bined.CodeAreaZone;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.action.api.clipboard.ClipboardActionsApi;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.component.contribution.CopyAsCodeContribution;
import org.exbin.bined.jaguif.component.contribution.GoToPositionContribution;
import org.exbin.bined.jaguif.component.contribution.PasteFromCodeContribution;
import org.exbin.bined.jaguif.component.contribution.ToggleNonprintablesContribution;
import org.exbin.bined.jaguif.component.contribution.ViewNonprintablesContribution;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextStateProvider;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.RelativeSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.menu.popup.api.MenuPopupModuleApi;
import org.exbin.jaguif.menu.popup.api.ComponentPopupEventDispatcher;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Binary data component module.
 */
@ParametersAreNonnullByDefault
public class BinedComponentModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedComponentModule.class);

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

    private ShowNonprintablesActions showNonprintablesActions;
    private ClipboardCodeActions clipboardCodeActions;

    public BinedComponentModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedComponentModule.class);
        }

        return resourceBundle;
    }

    public void registerGoToPosition() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(EDIT_OPERATION_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = mgmt.registerMenuGroup(EDIT_FIND_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.AROUND));
        contribution = new GoToPositionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(EDIT_FIND_MENU_GROUP_ID));
    }

    @Nonnull
    public ShowNonprintablesActions getShowNonprintablesActions() {
        if (showNonprintablesActions == null) {
            showNonprintablesActions = new ShowNonprintablesActions();
            showNonprintablesActions.init(getResourceBundle());
        }

        return showNonprintablesActions;
    }

    @Nonnull
    public GoToPositionAction createGoToPositionAction() {
        GoToPositionAction goToPositionAction = new GoToPositionAction();
        goToPositionAction.init(getResourceBundle());
        goToPositionAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                ContextComponent contextComponent = contextState.getActiveState(ContextComponent.class);
                return contextComponent instanceof BinaryDataComponent;
            }
        });
        return goToPositionAction;
    }

    @Nonnull
    public ClipboardCodeActions getClipboardCodeActions() {
        if (clipboardCodeActions == null) {
            clipboardCodeActions = new ClipboardCodeActions();
            clipboardCodeActions.init(getResourceBundle());
        }

        return clipboardCodeActions;
    }

    public void registerShowNonprintablesToolBarActions() {
        getShowNonprintablesActions();
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(BINED_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.MIDDLE));
        contribution = new ToggleNonprintablesContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(BINED_TOOL_BAR_GROUP_ID));
    }

    public void registerViewNonprintablesMenuActions() {
        getShowNonprintablesActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_NONPRINTABLES_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM));
        contribution = new ViewNonprintablesContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_NONPRINTABLES_MENU_GROUP_ID));
    }

    public void registerClipboardCodeActions() {
        getClipboardCodeActions();
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.EDIT_SUBMENU_ID);
        SequenceContribution contribution = new CopyAsCodeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "copy"));
        contribution = new PasteFromCodeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(MenuModuleApi.CLIPBOARD_ACTIONS_MENU_GROUP_ID));
        mgmt.registerMenuRule(contribution, new RelativeSequenceContributionRule(RelativeSequenceContributionRule.NextToMode.AFTER, "paste"));
    }

    public void registerCodeAreaPopupMenu() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        menuModule.registerMenu(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(CODE_AREA_POPUP_MENU_ID, MODULE_ID);
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

        contribution = clipboardActions.createCutContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = clipboardActions.createCopyContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = new CopyAsCodeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = clipboardActions.createPasteContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = new PasteFromCodeContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));
        contribution = clipboardActions.createDeleteContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_EDIT_GROUP_ID));

        contribution = clipboardActions.createSelectAllContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_SELECTION_GROUP_ID));

        contribution = new GoToPositionContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(CODE_AREA_POPUP_FIND_GROUP_ID));
    }

    @Nonnull
    public JPopupMenu createCodeAreaPopupMenu(final SectCodeArea codeArea, int x, int y) {
        CodeAreaZone codeAreaZone = codeArea.getPainter().getPositionZone(x, y);

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        final JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();

        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ActiveContextManagement contextManager = contextModule.createContextManager();
        BinEdDataComponent dataComponent = new BinEdDataComponent(codeArea);
        dataComponent.setContextManager(contextManager);

        contextManager.changeActiveState(ContextComponent.class, dataComponent);
        contextManager.changeActiveState(DialogParentComponent.class, () -> codeArea);
        contextManager.changeActiveState(CodeAreaZone.class, codeAreaZone);

        ContextRegistration contextRegistrar = contextModule.createContextRegistrator(contextManager);
        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, contextRegistrar, contextManager);
        return popupMenu;
    }

    @Nonnull
    public JPopupMenu createBinaryComponentPopupMenu(final BinEdDataComponent dataComponent, int x, int y) {
        CodeAreaZone codeAreaZone = ((SectCodeArea) dataComponent.getCodeArea()).getPainter().getPositionZone(x, y);

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        final JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();

        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ActiveContextManagement contextManager = contextModule.createContextManager();

        contextManager.changeActiveState(ContextComponent.class, dataComponent);
        contextManager.changeActiveState(DialogParentComponent.class, () -> dataComponent.getCodeArea());
        contextManager.changeActiveState(CodeAreaZone.class, codeAreaZone);

        ContextRegistration contextRegistrar = contextModule.createContextRegistrator(contextManager);
        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, contextRegistrar, contextManager);
        return popupMenu;
    }

    @Nonnull
    public JPopupMenu createBinaryDocumentPopupMenu(final SectCodeArea codeArea, int x, int y) {
        CodeAreaZone codeAreaZone = codeArea.getPainter().getPositionZone(x, y);

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        final JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();

        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement frameContextManager = frameModule.getFrameController().getContextManager();
        ActiveContextManagement contextManager = contextModule.createChildContextManager(frameContextManager);
        contextManager.changeActiveState(CodeAreaZone.class, codeAreaZone);
        ContextRegistration contextRegistrar = contextModule.createContextRegistrator(contextManager);

        menuModule.buildMenu(popupMenu, CODE_AREA_POPUP_MENU_ID, contextRegistrar, contextManager);
        return popupMenu;
    }

    @Nonnull
    public JPopupMenu createCodeAreaPopupMenu() {
        return new JPopupMenu() {
            @Override
            public void show(@Nonnull Component invoker, int x, int y) {
                SectCodeArea codeArea;
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                    codeArea = (SectCodeArea) ((JViewport) invoker).getParent().getParent();
                } else {
                    codeArea = (SectCodeArea) invoker;
                }

                JPopupMenu popupMenu = createCodeAreaPopupMenu(codeArea, clickedX, clickedY);
                popupMenu.show(invoker, x, y);
            }
        };
    }

    @Nonnull
    public JPopupMenu createBinaryComponentPopupMenu(BinEdDataComponent dataComponent) {
        return new JPopupMenu() {
            @Override
            public void show(@Nonnull Component invoker, int x, int y) {
                SectCodeArea codeArea;
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                    codeArea = (SectCodeArea) ((JViewport) invoker).getParent().getParent();
                } else {
                    codeArea = (SectCodeArea) invoker;
                }

                if (dataComponent.getCodeArea() == codeArea) {
                    JPopupMenu popupMenu = createBinaryComponentPopupMenu(dataComponent, clickedX, clickedY);
                    popupMenu.show(invoker, x, y);
                }
            }
        };
    }

    @Nonnull
    public JPopupMenu createBinaryDocumentPopupMenu() {
        return new JPopupMenu() {
            @Override
            public void show(@Nonnull Component invoker, int x, int y) {
                SectCodeArea codeArea;
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                    codeArea = (SectCodeArea) ((JViewport) invoker).getParent().getParent();
                } else {
                    codeArea = (SectCodeArea) invoker;
                }

                JPopupMenu popupMenu = createBinaryDocumentPopupMenu(codeArea, clickedX, clickedY);
                popupMenu.show(invoker, x, y);
            }
        };
    }

    public void registerCodeAreaPopupEventDispatcher() {
        MenuPopupModuleApi menuPopupModule = App.getModule(MenuPopupModuleApi.class);
        menuPopupModule.addComponentPopupEventDispatcher(new ComponentPopupEventDispatcher() {

            private JPopupMenu popupMenu = null;

            @Override
            public boolean dispatchMouseEvent(MouseEvent mouseEvent) {
                Component component = getSource(mouseEvent);
                if (component instanceof SectCodeArea) {
                    if (((SectCodeArea) component).getComponentPopupMenu() == null) {
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

                        popupMenu = createCodeAreaPopupMenu((SectCodeArea) component, x, y);

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
                        Point point = new Point(component.getWidth() / 2, component.getHeight() / 2);
                        int x = (int) point.getX();
                        int y = (int) point.getY();
                        popupMenu = createCodeAreaPopupMenu((SectCodeArea) component, x, y);

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
}
