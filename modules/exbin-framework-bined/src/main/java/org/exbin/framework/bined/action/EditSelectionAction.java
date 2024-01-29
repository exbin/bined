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
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionActiveComponent;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.bined.gui.EditSelectionPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

/**
 * Edit selection action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditSelectionAction extends AbstractAction {

    public static final String ACTION_ID = "editSelectionAction";

    private ResourceBundle resourceBundle;
    private CodeAreaCore codeArea;

    public EditSelectionAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_ACTIVE_COMPONENT, new ActionActiveComponent() {
            @Nonnull
            @Override
            public Set<Class<?>> forClasses() {
                return Collections.singleton(CodeAreaCore.class);
            }

            @Override
            public void componentActive(Set<Object> affectedClasses) {
                boolean hasInstance = !affectedClasses.isEmpty();
                codeArea = hasInstance ? (CodeAreaCore) affectedClasses.iterator().next() : null;
                setEnabled(hasInstance);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final EditSelectionPanel editSelectionPanel = new EditSelectionPanel();
        editSelectionPanel.setCursorPosition(((CaretCapable) codeArea).getDataPosition());
        editSelectionPanel.setMaxPosition(codeArea.getDataSize());
        editSelectionPanel.setSelectionRange(((SelectionCapable) codeArea).getSelection());
        DefaultControlPanel controlPanel = new DefaultControlPanel(editSelectionPanel.getResourceBundle());
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final WindowHandler dialog = windowModule.createDialog(codeArea, Dialog.ModalityType.APPLICATION_MODAL, editSelectionPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), editSelectionPanel.getClass(), editSelectionPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, editSelectionPanel.getResourceBundle());
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
