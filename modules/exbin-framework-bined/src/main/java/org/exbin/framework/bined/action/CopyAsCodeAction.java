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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.context.api.ContextChangeRegistration;

/**
 * Copy as code actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CopyAsCodeAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "copyAsCodeAction";

    protected CodeAreaCore codeArea;

    public void setup(ResourceBundle resourceBundle) {
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
        registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
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
                CodeDataClipboardData binaryData = new CodeDataClipboardData(copy, binedDataFlavor, codeType, charactersCase);
                ((CodeAreaOperationCommandHandler) commandHandler).setClipboardContent(binaryData);
            } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                DataFlavor binedDataFlavor = ((DefaultCodeAreaCommandHandler) commandHandler).getBinedDataFlavor();
                CodeDataClipboardData binaryData = new CodeDataClipboardData(copy, binedDataFlavor, codeType, charactersCase);
                ((DefaultCodeAreaCommandHandler) commandHandler).setClipboardContent(binaryData);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class CodeDataClipboardData implements CodeAreaSwingUtils.ClipboardData {

        private final BinaryData data;
        private final DataFlavor binaryDataFlavor;
        private final CodeType codeType;
        private final CodeCharactersCase charactersCase;

        public CodeDataClipboardData(BinaryData data, DataFlavor binaryDataFlavor, CodeType codeType, CodeCharactersCase charactersCase) {
            this.data = data;
            this.binaryDataFlavor = binaryDataFlavor;
            this.codeType = codeType;
            this.charactersCase = charactersCase;
        }

        @Nonnull
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{binaryDataFlavor, DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(binaryDataFlavor) || flavor.equals(DataFlavor.stringFlavor);
        }

        @Nonnull
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(binaryDataFlavor)) {
                return data;
            } else {
                int charsPerByte = codeType.getMaxDigitsForByte() + 1;
                int textLength = (int) (data.getDataSize() * charsPerByte);
                if (textLength > 0) {
                    textLength--;
                }

                char[] targetData = new char[textLength];
                Arrays.fill(targetData, ' ');
                for (int i = 0; i < data.getDataSize(); i++) {
                    CodeAreaUtils.byteToCharsCode(data.getByte(i), codeType, targetData, i * charsPerByte, charactersCase);
                }
                return new String(targetData);
            }
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            // do nothing
        }

        @Override
        public void dispose() {
            data.dispose();
        }
    }
}
