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
package org.exbin.bined.jaguif.component.action;

import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.CodeClipboardData;
import org.exbin.jaguif.context.api.ContextChangeRegistration;

/**
 * Copy as code action.
 */
@ParametersAreNonnullByDefault
public class CopyAsCodeAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "copyAsCode";

    protected CodeAreaCore codeArea;

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CopyAsCodeAction.copyAsCode(codeArea);
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerChangeListener(ContextComponent.class, (instance) -> {
            codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
            boolean hasInstance = codeArea != null;
            boolean hasSelection = hasInstance;
            if (hasInstance) {
                hasSelection = codeArea.hasSelection();
            }
            setEnabled(hasSelection);
        });
    }

    public static void copyAsCode(CodeAreaCore codeArea) {
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (!selection.isEmpty()) {
            long first = selection.getFirst();
            long last = selection.getLast();

            BinaryData copy = codeArea.getContentData().copy(first, last - first + 1);

            CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();
            CodeCharactersCase charactersCase = ((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase();

            CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
            if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                DataFlavor binedDataFlavor = ((CodeAreaOperationCommandHandler) commandHandler).getBinedDataFlavor();
                CodeClipboardData binaryData = new CodeClipboardData(copy, binedDataFlavor, codeType, charactersCase);
                ((CodeAreaOperationCommandHandler) commandHandler).setClipboardContent(binaryData);
            } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                DataFlavor binedDataFlavor = ((DefaultCodeAreaCommandHandler) commandHandler).getBinedDataFlavor();
                CodeClipboardData binaryData = new CodeClipboardData(copy, binedDataFlavor, codeType, charactersCase);
                ((DefaultCodeAreaCommandHandler) commandHandler).setClipboardContent(binaryData);
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
