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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.bined.CharsetStreamTranslator;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.context.api.ContextChangeRegistration;

/**
 * Clipboard code actions.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PasteFromCodeAction extends AbstractAction implements ActionContextChange {

    public static final String ACTION_ID = "pasteFromCodeAction";

    protected CodeAreaCore codeArea;

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PasteFromCodeAction.pasteFromCode(codeArea);
    }

    @Override
    public void register(ContextChangeRegistration registrar) {
        registrar.registerUpdateListener(ContextComponent.class, (instance) -> {
            codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
            boolean hasInstance = codeArea != null;
            boolean hasSelection = hasInstance;
            if (hasInstance) {
                hasSelection = codeArea.canPaste() && codeArea.isEditable();
            }
            setEnabled(hasSelection);
        });
    }

    public static void pasteFromCode(CodeAreaCore codeArea) {
        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
        if (!commandHandler.checkEditAllowed()) {
            return;
        }

        Clipboard clipboard;
        DataFlavor binedDataFlavor;
        if (commandHandler instanceof CodeAreaOperationCommandHandler) {
            clipboard = ((CodeAreaOperationCommandHandler) commandHandler).getClipboard();
            binedDataFlavor = ((CodeAreaOperationCommandHandler) commandHandler).getBinedDataFlavor();
        } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
            clipboard = ((DefaultCodeAreaCommandHandler) commandHandler).getClipboard();
            binedDataFlavor = ((DefaultCodeAreaCommandHandler) commandHandler).getBinedDataFlavor();
        } else {
            throw new IllegalStateException();
        }

        try {
            if (clipboard.isDataFlavorAvailable(binedDataFlavor)) {
                commandHandler.paste();
            } else if (clipboard.isDataFlavorAvailable(DataFlavor.getTextPlainUnicodeFlavor())) {
                InputStream insertedData;
                try {
                    insertedData = (InputStream) clipboard.getData(DataFlavor.getTextPlainUnicodeFlavor());
                    CodeType codeType = ((CodeTypeCapable) codeArea).getCodeType();

                    DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();
                    String charsetName = textPlainUnicodeFlavor.getParameter(CodeAreaOperationCommandHandler.MIME_CHARSET);
                    CharsetStreamTranslator translator = new CharsetStreamTranslator(Charset.forName(charsetName), ((CharsetCapable) codeArea).getCharset(), insertedData);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] dataBuffer = new byte[1024];
                    int length;
                    while ((length = translator.read(dataBuffer)) != -1) {
                        outputStream.write(dataBuffer, 0, length);
                    }
                    String insertedString = outputStream.toString(((CharsetCapable) codeArea).getCharset().name());
                    ByteArrayEditableData clipData = new ByteArrayEditableData();
                    CodeAreaUtils.insertHexStringIntoData(insertedString, clipData, codeType);

                    if (commandHandler instanceof CodeAreaOperationCommandHandler) {
                        ((CodeAreaOperationCommandHandler) commandHandler).pasteBinaryData(clipData);
                    } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
                        ((DefaultCodeAreaCommandHandler) commandHandler).pasteBinaryData(clipData);
                    }
                } catch (UnsupportedFlavorException | IllegalStateException | IOException ex) {
                    Logger.getLogger(CodeAreaOperationCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IllegalStateException ex) {
            // Clipboard not available - ignore
        }
    }
}
