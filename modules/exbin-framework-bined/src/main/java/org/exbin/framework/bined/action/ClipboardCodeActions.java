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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Clipboard code actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardCodeActions {

    protected ResourceBundle resourceBundle;
    protected ActionMethod copyAsCodeMethod = null;
    protected ActionMethod pasteFromCodeMethod = null;

    public ClipboardCodeActions() {
    }

    public void setup(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Nonnull
    public CopyAsCodeAction createCopyAsCodeAction() {
        CopyAsCodeAction copyAsCodeAction = new CopyAsCodeAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copyAsCodeMethod != null) {
                    copyAsCodeMethod.performAction(codeArea);
                    return;
                }

                super.actionPerformed(e);
            }
        };
        copyAsCodeAction.setup(resourceBundle);
        return copyAsCodeAction;
    }

    @Nonnull
    public PasteFromCodeAction createPasteFromCodeAction() {
        PasteFromCodeAction pasteFromCodeAction = new PasteFromCodeAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pasteFromCodeMethod != null) {
                    pasteFromCodeMethod.performAction(codeArea);
                    return;
                }

                super.actionPerformed(e);
            }
        };
        pasteFromCodeAction.setup(resourceBundle);
        return pasteFromCodeAction;
    }

    public void setCopyAsCodeMethod(ActionMethod copyAsCodeMethod) {
        this.copyAsCodeMethod = copyAsCodeMethod;
    }

    public void setPasteFromCodeMethod(ActionMethod pasteFromCodeMethod) {
        this.pasteFromCodeMethod = pasteFromCodeMethod;
    }

    @ParametersAreNonnullByDefault
    public interface ActionMethod {

        void performAction(CodeAreaCore codeArea);
    }
}
