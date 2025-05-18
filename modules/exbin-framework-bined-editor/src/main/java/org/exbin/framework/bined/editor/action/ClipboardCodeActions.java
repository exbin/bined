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
package org.exbin.framework.bined.editor.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;

/**
 * Clipboard code actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardCodeActions {

    private ResourceBundle resourceBundle;

    public ClipboardCodeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public CopyAsCodeAction createCopyAsCodeAction() {
        CopyAsCodeAction copyAsCodeAction = new CopyAsCodeAction();
        copyAsCodeAction.setup(resourceBundle);
        return copyAsCodeAction;
    }

    @Nonnull
    public PasteFromCodeAction createPasteFromCodeAction() {
        PasteFromCodeAction pasteFromCodeAction = new PasteFromCodeAction();
        pasteFromCodeAction.setup(resourceBundle);
        return pasteFromCodeAction;
    }

    @ParametersAreNonnullByDefault
    public static class CopyAsCodeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "copyAsCodeAction";

        private CodeAreaCore codeArea;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO move out of code area
            ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).copyAsCode();
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = codeArea != null;
                boolean hasSelection = hasInstance;
                if (hasInstance) {
                    hasSelection = codeArea.hasSelection();
                }
                setEnabled(hasSelection);
            });
        }
    }

    @ParametersAreNonnullByDefault
    public static class PasteFromCodeAction extends AbstractAction implements ActionContextChange {

        public static final String ACTION_ID = "pasteFromCodeAction";

        private CodeAreaCore codeArea;

        public void setup(ResourceBundle resourceBundle) {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.initAction(this, resourceBundle, ACTION_ID);
            putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO move out of code area
            try {
                ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).pasteFromCode();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog((Component) e.getSource(), ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void register(ActionContextChangeManager manager) {
            manager.registerUpdateListener(CodeAreaCore.class, (instance) -> {
                codeArea = instance;
                boolean hasInstance = codeArea != null;
                boolean hasSelection = hasInstance;
                if (hasInstance) {
                    hasSelection = codeArea.canPaste() && codeArea.isEditable();
                }
                setEnabled(hasSelection);
            });
        }
    }
}
