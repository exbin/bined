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
package org.exbin.framework.bined.search.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionMenuCreation;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationManager;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.search.BinEdComponentSearch;
import org.exbin.framework.bined.search.gui.BinarySearchPanel;
import org.exbin.framework.file.api.FileHandler;

/**
 * Find/replace actions for binary search.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    public static final String FIND_ACTION_ID = "binarySearchFindAction";
    public static final String FIND_AGAIN_ACTION_ID = "binarySearchFindAgainAction";
    public static final String REPLACE_ACTION_ID = "binarySearchReplaceAction";

    private ResourceBundle resourceBundle;

    private final List<FindAgainListener> findAgainListeners = new ArrayList<>();

    public FindReplaceActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public Action getEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAction, resourceBundle, FIND_ACTION_ID);
        editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
        editFindAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        editFindAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant == BinedModule.PopupMenuVariant.EDITOR && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        editFindAction.putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, editFindAction);
        return editFindAction;
    }

    @Nonnull
    public Action getEditFindAgainAction() {
        EditFindAgainAction editFindAgainAction = new EditFindAgainAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editFindAgainAction, resourceBundle, FIND_AGAIN_ACTION_ID);
        editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        editFindAgainAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant == BinedModule.PopupMenuVariant.EDITOR && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return editFindAgainAction;
    }

    @Nonnull
    public Action getEditReplaceAction() {
        EditFindAgainAction editReplaceAction = new EditFindAgainAction();
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(editReplaceAction, resourceBundle, REPLACE_ACTION_ID);
        editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
        editReplaceAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        editReplaceAction.putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
            @Override
            public boolean shouldCreate(String menuId) {
                BinedModule binedModule = App.getModule(BinedModule.class);
                BinedModule.PopupMenuVariant menuVariant = binedModule.getPopupMenuVariant();
                BasicCodeAreaZone positionZone = binedModule.getPopupMenuPositionZone();
                return menuVariant == BinedModule.PopupMenuVariant.EDITOR && !(positionZone == BasicCodeAreaZone.TOP_LEFT_CORNER || positionZone == BasicCodeAreaZone.HEADER || positionZone == BasicCodeAreaZone.ROW_POSITIONS);
            }

            @Override
            public void onCreate(JMenuItem menuItem, String menuId) {
            }
        });
        return editReplaceAction;
    }

    public void addFindAgainListener(FindAgainListener findAgainListener) {
        findAgainListeners.add(findAgainListener);
    }

    public void removeFindAgainListener(FindAgainListener findAgainListener) {
        findAgainListeners.remove(findAgainListener);
    }

    public static interface FindAgainListener {

        void performed();
    }

    @ParametersAreNonnullByDefault
    public class EditFindAction extends AbstractAction implements ActionActiveComponent {

        private FileHandler fileHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentPanel activePanel = ((BinEdFileHandler) fileHandler).getComponent();
            BinEdComponentSearch componentExtension = activePanel.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.showSearchPanel(BinarySearchPanel.PanelMode.FIND);
        }

        @Override
        public void register(ComponentActivationManager manager) {
            manager.registerUpdateListener(FileHandler.class, (instance) -> {
                fileHandler = instance;
                setEnabled(instance != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditFindAgainAction extends AbstractAction implements ActionActiveComponent {

        private FileHandler fileHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentPanel activePanel = ((BinEdFileHandler) fileHandler).getComponent();
            BinEdComponentSearch componentExtension = activePanel.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.performFindAgain();

            for (FindAgainListener findAgainListener : findAgainListeners) {
                findAgainListener.performed();
            }
        }

        @Override
        public void register(ComponentActivationManager manager) {
            manager.registerUpdateListener(FileHandler.class, (instance) -> {
                fileHandler = instance;
                setEnabled(instance != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditReplaceAction extends AbstractAction implements ActionActiveComponent {

        private FileHandler fileHandler;

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentPanel activePanel = ((BinEdFileHandler) fileHandler).getComponent();
            BinEdComponentSearch componentExtension = activePanel.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.showSearchPanel(BinarySearchPanel.PanelMode.REPLACE);
        }

        @Override
        public void register(ComponentActivationManager manager) {
            manager.registerUpdateListener(FileHandler.class, (instance) -> {
                fileHandler = instance;
                setEnabled(instance != null);
            });
        }
    }
}
