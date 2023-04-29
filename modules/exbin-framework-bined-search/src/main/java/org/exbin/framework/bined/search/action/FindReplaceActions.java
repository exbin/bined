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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.search.BinEdComponentSearch;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Find/replace actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FindReplaceActions implements FileDependentAction {

    public static final String EDIT_FIND_ACTION_ID = "editFindAction";
    public static final String EDIT_FIND_AGAIN_ACTION_ID = "editFindAgainAction";
    public static final String EDIT_REPLACE_ACTION_ID = "editReplaceAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action editFindAction;
    private Action editFindAgainAction;
    private Action editReplaceAction;

    public FindReplaceActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (editFindAction != null) {
            editFindAction.setEnabled(activeFile.isPresent());
        }
        if (editFindAgainAction != null) {
            editFindAgainAction.setEnabled(activeFile.isPresent());
        }
        if (editReplaceAction != null) {
            editReplaceAction.setEnabled(activeFile.isPresent());
        }
    }

    @Nonnull
    public Action getEditFindAction() {
        if (editFindAction == null) {
            editFindAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    BinEdComponentPanel activePanel = ((BinEdFileHandler) activeFile.get()).getComponent();
                    BinEdComponentSearch componentExtension = activePanel.getComponentExtension(BinEdComponentSearch.class);
                    componentExtension.showSearchPanel(false);
                }
            };
            ActionUtils.setupAction(editFindAction, resourceBundle, EDIT_FIND_ACTION_ID);
            editFindAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, ActionUtils.getMetaMask()));
            editFindAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return editFindAction;
    }

    @Nonnull
    public Action getEditFindAgainAction() {
        if (editFindAgainAction == null) {
            editFindAgainAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    BinEdComponentPanel activePanel = ((BinEdFileHandler) activeFile.get()).getComponent();
                    throw new UnsupportedOperationException("Not supported yet.");
                    // TODO activePanel.findAgain();
                }
            };
            ActionUtils.setupAction(editFindAgainAction, resourceBundle, EDIT_FIND_AGAIN_ACTION_ID);
            editFindAgainAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        }
        return editFindAgainAction;
    }

    @Nonnull
    public Action getEditReplaceAction() {
        if (editReplaceAction == null) {
            editReplaceAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }

                    BinEdComponentPanel activePanel = ((BinEdFileHandler) activeFile.get()).getComponent();
                    BinEdComponentSearch componentExtension = activePanel.getComponentExtension(BinEdComponentSearch.class);
                    componentExtension.showSearchPanel(true);
                }
            };
            ActionUtils.setupAction(editReplaceAction, resourceBundle, EDIT_REPLACE_ACTION_ID);
            editReplaceAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, ActionUtils.getMetaMask()));
            editReplaceAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }
        return editReplaceAction;
    }
}
