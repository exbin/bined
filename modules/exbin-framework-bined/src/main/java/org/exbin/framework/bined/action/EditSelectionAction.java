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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.EditSelectionPanel;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * Edit selection action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditSelectionAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "editSelectionAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public EditSelectionAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        setEnabled(activeFile.isPresent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
        final EditSelectionPanel editSelectionPanel = new EditSelectionPanel();
        editSelectionPanel.setCursorPosition(codeArea.getDataPosition());
        editSelectionPanel.setMaxPosition(codeArea.getDataSize());
        editSelectionPanel.setSelectionRange(codeArea.getSelection());
        DefaultControlPanel controlPanel = new DefaultControlPanel(editSelectionPanel.getResourceBundle());
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        final DialogWrapper dialog = frameModule.createDialog(editorProvider.getEditorComponent(), Dialog.ModalityType.APPLICATION_MODAL, editSelectionPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), editSelectionPanel.getClass(), editSelectionPanel.getResourceBundle());
        frameModule.setDialogTitle(dialog, editSelectionPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                editSelectionPanel.acceptInput();
                Optional<SelectionRange> selectionRange = editSelectionPanel.getSelectionRange();
                if (selectionRange.isPresent()) {
                    codeArea.setSelection(selectionRange.get());
                } else {
                    codeArea.clearSelection();
                }
                codeArea.revealCursor();
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(editSelectionPanel::initFocus);
        dialog.showCentered(editorProvider.getEditorComponent());
    }
}
