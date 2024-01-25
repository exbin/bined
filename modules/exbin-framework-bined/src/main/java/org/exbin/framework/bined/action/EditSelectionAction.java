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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.framework.bined.gui.EditSelectionPanel;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Edit selection action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditSelectionAction extends AbstractAction implements CodeAreaAction {

    public static final String ACTION_ID = "editSelectionAction";

    private ResourceBundle resourceBundle;
    private CodeAreaCore codeArea;

    public EditSelectionAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveCodeArea(@Nullable CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        setEnabled(codeArea != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final EditSelectionPanel editSelectionPanel = new EditSelectionPanel();
        editSelectionPanel.setCursorPosition(((CaretCapable) codeArea).getDataPosition());
        editSelectionPanel.setMaxPosition(codeArea.getDataSize());
        editSelectionPanel.setSelectionRange(((SelectionCapable) codeArea).getSelection());
        DefaultControlPanel controlPanel = new DefaultControlPanel(editSelectionPanel.getResourceBundle());
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final DialogWrapper dialog = windowModule.createDialog(codeArea, Dialog.ModalityType.APPLICATION_MODAL, editSelectionPanel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), editSelectionPanel.getClass(), editSelectionPanel.getResourceBundle());
        windowModule.setDialogTitle(dialog, editSelectionPanel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                editSelectionPanel.acceptInput();
                Optional<SelectionRange> selectionRange = editSelectionPanel.getSelectionRange();
                if (selectionRange.isPresent()) {
                    ((SelectionCapable) codeArea).setSelection(selectionRange.get());
                } else {
                    codeArea.clearSelection();
                }
                ((ScrollingCapable) codeArea).revealCursor();
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(editSelectionPanel::initFocus);
        dialog.showCentered(codeArea);
    }
}
