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
package org.exbin.bined.jaguif.search.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.CodeAreaZone;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.utils.ActionUtils;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.search.BinEdComponentSearch;
import org.exbin.bined.jaguif.search.gui.BinarySearchPanel;
import org.exbin.jaguif.context.api.ContextStateProvider;
import org.exbin.jaguif.document.api.ContextDocument;

/**
 * Find/replace actions for binary search.
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions {

    private ResourceBundle resourceBundle;

    private final List<FindAgainListener> findAgainListeners = new ArrayList<>();

    public FindReplaceActions() {
    }

    public void init(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public EditFindAction createEditFindAction() {
        EditFindAction editFindAction = new EditFindAction();
        editFindAction.init(resourceBundle);
        return editFindAction;
    }

    @Nonnull
    public EditFindAgainAction createEditFindAgainAction() {
        EditFindAgainAction editFindAgainAction = new EditFindAgainAction();
        editFindAgainAction.init(resourceBundle);
        return editFindAgainAction;
    }

    @Nonnull
    public EditReplaceAction createEditReplaceAction() {
        EditReplaceAction editReplaceAction = new EditReplaceAction();
        editReplaceAction.init(resourceBundle);
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
    public class EditFindAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "binarySearchFind";

        private BinaryFileDocument binaryDocument;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
                @Override
                public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                    CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                    ContextDocument contextDocument = contextState.getActiveState(ContextDocument.class);
                    return contextDocument instanceof BinaryFileDocument && !(codeAreaZone == BasicCodeAreaZone.TOP_LEFT_CORNER || codeAreaZone == BasicCodeAreaZone.HEADER || codeAreaZone == BasicCodeAreaZone.ROW_POSITIONS);
                }
            });
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.showSearchPanel(BinarySearchPanel.PanelMode.FIND);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                binaryDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                setEnabled(binaryDocument != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditFindAgainAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "binarySearchFindAgain";

        private BinaryFileDocument binaryDocument;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
            putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
                @Override
                public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                    CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                    ContextDocument contextDocument = contextState.getActiveState(ContextDocument.class);
                    return contextDocument instanceof BinaryFileDocument && !(codeAreaZone == BasicCodeAreaZone.TOP_LEFT_CORNER || codeAreaZone == BasicCodeAreaZone.HEADER || codeAreaZone == BasicCodeAreaZone.ROW_POSITIONS);
                }
            });
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.performFindAgain();

            for (FindAgainListener findAgainListener : findAgainListeners) {
                findAgainListener.performed();
            }
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                binaryDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                setEnabled(binaryDocument != null);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public class EditReplaceAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "binarySearchReplace";

        private BinaryFileDocument binaryDocument;

        public void init(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            setEnabled(false);
            putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            putValue(ActionConsts.ACTION_DIALOG_MODE, true);
            putValue(ActionConsts.ACTION_MENU_CREATION, new ActionMenuCreation() {
                @Override
                public boolean shouldCreate(String menuId, String subMenuId, ContextStateProvider contextState) {
                    CodeAreaZone codeAreaZone = contextState.getActiveState(CodeAreaZone.class);
                    ContextDocument contextDocument = contextState.getActiveState(ContextDocument.class);
                    return contextDocument instanceof BinaryFileDocument && !(codeAreaZone == BasicCodeAreaZone.TOP_LEFT_CORNER || codeAreaZone == BasicCodeAreaZone.HEADER || codeAreaZone == BasicCodeAreaZone.ROW_POSITIONS);
                }
            });
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BinEdComponentSearch componentExtension = binaryDocument.getComponentExtension(BinEdComponentSearch.class);
            componentExtension.showSearchPanel(BinarySearchPanel.PanelMode.REPLACE);
        }

        @Override
        public void register(ContextChangeRegistration registrar) {
            registrar.registerChangeListener(ContextDocument.class, (instance) -> {
                binaryDocument = instance instanceof BinaryFileDocument ? (BinaryFileDocument) instance : null;
                setEnabled(binaryDocument != null);
            });
        }
    }
}
