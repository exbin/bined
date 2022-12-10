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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Clipboard code actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardCodeActions implements FileDependentAction {

    public static final String COPY_AS_CODE_ACTION_ID = "copyAsCodeAction";
    public static final String PASTE_FROM_CODE_ACTION_ID = "pasteFromCodeAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action copyAsCodeAction;
    private Action pasteFromCodeAction;

    public ClipboardCodeActions() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        boolean hasSelection = false;
        boolean canPaste = false;
        if (activeFile.isPresent()) {
            ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            hasSelection = codeArea.hasSelection();
            canPaste = codeArea.canPaste();
        }
        if (copyAsCodeAction != null) {
            copyAsCodeAction.setEnabled(hasSelection);
        }
        if (pasteFromCodeAction != null) {
            pasteFromCodeAction.setEnabled(canPaste);
        }
    }

    @Nonnull
    public Action getCopyAsCodeAction() {
        if (copyAsCodeAction == null) {
            copyAsCodeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }
                    // TODO move out of code area
                    ((BinEdFileHandler) activeFile.get()).getCodeArea().copyAsCode();
                }
            };
            ActionUtils.setupAction(copyAsCodeAction, resourceBundle, COPY_AS_CODE_ACTION_ID);
        }
        return copyAsCodeAction;
    }

    @Nonnull
    public Action getPasteFromCodeAction() {
        if (pasteFromCodeAction == null) {
            pasteFromCodeAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        throw new IllegalStateException();
                    }
                    // TODO move out of code area
                    try {
                        ((BinEdFileHandler) activeFile.get()).getCodeArea().pasteFromCode();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog((Component) e.getSource(), ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            ActionUtils.setupAction(pasteFromCodeAction, resourceBundle, PASTE_FROM_CODE_ACTION_ID);
        }
        return pasteFromCodeAction;
    }
}
