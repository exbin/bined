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
package org.exbin.framework.bined.action;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Show unprintables actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShowUnprintablesActions implements FileDependentAction {

    public static final String VIEW_UNPRINTABLES_ACTION_ID = "viewUnprintablesAction";
    public static final String VIEW_UNPRINTABLES_TOOLBAR_ACTION_ID = "viewUnprintablesToolbarAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action viewUnprintablesAction;
    private Action viewUnprintablesToolbarAction;

    public ShowUnprintablesActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        Boolean showUnprintables = activeFile.isPresent() ? ((BinEdFileHandler) activeFile.get()).getCodeArea().isShowUnprintables() : null;

        if (viewUnprintablesAction != null) {
            viewUnprintablesAction.setEnabled(activeFile.isPresent());
            if (showUnprintables != null) {
                viewUnprintablesAction.putValue(Action.SELECTED_KEY, showUnprintables);
            }
        }
        if (viewUnprintablesToolbarAction != null) {
            viewUnprintablesToolbarAction.setEnabled(activeFile.isPresent());
            if (showUnprintables != null) {
                viewUnprintablesToolbarAction.putValue(Action.SELECTED_KEY, showUnprintables);
            }
        }
    }

    public void setShowUnprintables(boolean showUnprintables) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ((BinEdFileHandler) activeFile.get()).getCodeArea().setShowUnprintables(showUnprintables);
        viewUnprintablesAction.putValue(Action.SELECTED_KEY, showUnprintables);
        viewUnprintablesToolbarAction.putValue(Action.SELECTED_KEY, showUnprintables);
    }

    @Nonnull
    public Action getViewUnprintablesAction() {
        if (viewUnprintablesAction == null) {
            viewUnprintablesAction = createViewUnprintablesAction();
            ActionUtils.setupAction(viewUnprintablesAction, resourceBundle, VIEW_UNPRINTABLES_ACTION_ID);
            viewUnprintablesAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
            viewUnprintablesAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, ActionUtils.getMetaMask()));

        }
        return viewUnprintablesAction;
    }

    @Nonnull
    public Action getViewUnprintablesToolbarAction() {
        if (viewUnprintablesToolbarAction == null) {
            viewUnprintablesToolbarAction = createViewUnprintablesAction();
            ActionUtils.setupAction(viewUnprintablesToolbarAction, resourceBundle, VIEW_UNPRINTABLES_TOOLBAR_ACTION_ID);
            viewUnprintablesToolbarAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        }
        return viewUnprintablesToolbarAction;
    }

    @Nonnull
    private Action createViewUnprintablesAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    throw new IllegalStateException();
                }

                boolean showUnprintables = ((BinEdFileHandler) activeFile.get()).getCodeArea().isShowUnprintables();
                setShowUnprintables(!showUnprintables);
            }
        };
    }
}
