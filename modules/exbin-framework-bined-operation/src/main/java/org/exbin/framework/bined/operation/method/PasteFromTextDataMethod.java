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
package org.exbin.framework.bined.operation.method;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.CodeAreaCompoundCommand;
import org.exbin.bined.operation.swing.command.DeleteSelectionCommand;
import org.exbin.bined.operation.swing.command.PasteDataCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.api.PasteFromDataMethod;
import org.exbin.framework.bined.operation.gui.BinaryPreviewPanel;
import org.exbin.framework.bined.operation.method.gui.PasteFromTextDataPanel;

/**
 * Paste from text data method.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PasteFromTextDataMethod implements PasteFromDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(PasteFromTextDataPanel.class);

    public static final String MIME_CHARSET = "charset";
    private PreviewDataHandler previewDataHandler;
    private long previewLengthLimit = 0;
    private BinaryPreviewPanel previewPanel;

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("method.name");
    }

    @Nonnull
    @Override
    public Component createComponent() {
        PasteFromTextDataPanel component = new PasteFromTextDataPanel();

        List<String> codeTypes = new ArrayList<>();
        codeTypes.add(resourceBundle.getString("codeType.binary"));
        codeTypes.add(resourceBundle.getString("codeType.octal"));
        codeTypes.add(resourceBundle.getString("codeType.decimal"));
        codeTypes.add(resourceBundle.getString("codeType.hexadecimal"));
        component.setCodeTypes(codeTypes);
        component.setCodeType(CodeType.HEXADECIMAL);

        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((PasteFromTextDataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createPasteCommand(Component component, CodeAreaCore codeArea, long position, EditOperation editOperation) {
        PasteFromTextDataPanel panel = (PasteFromTextDataPanel) component;
        CodeType codeType = panel.getCodeType();
        ResultData resultData = generateData(codeType);
        if (resultData.errorText == null) {
            return pasteBinaryData(resultData.binaryData, codeArea);
        }

        // TODO Paste error?
        return pasteBinaryData(resultData.binaryData, codeArea);
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        PasteFromTextDataPanel panel = (PasteFromTextDataPanel) component;
        panel.setParamChangeListener(() -> {
            fillPreviewData(panel);
        });
        fillPreviewData(panel);

    }

    private void fillPreviewData(PasteFromTextDataPanel panel) {
        previewPanel = new BinaryPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);

        SwingUtilities.invokeLater(() -> {
            CodeType codeType = panel.getCodeType();
            ResultData resultData = generateData(codeType);
            if (resultData.errorText != null) {
                previewPanel.setErrorMessage(resultData.errorText);
            } else {
                previewPanel.setPreviewData(resultData.binaryData);
            }
        });
    }

    @Nonnull
    private ResultData generateData(CodeType codeType) {
        ResultData resultData = new ResultData();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.getTextPlainUnicodeFlavor())) {
            InputStream insertedData;
            try {
                insertedData = (InputStream) clipboard.getData(DataFlavor.getTextPlainUnicodeFlavor());

                DataFlavor textPlainUnicodeFlavor = DataFlavor.getTextPlainUnicodeFlavor();
                String charsetName = textPlainUnicodeFlavor.getParameter(MIME_CHARSET);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] dataBuffer = new byte[1024];
                int length;
                while ((length = insertedData.read(dataBuffer)) != -1) {
                    outputStream.write(dataBuffer, 0, length);
                }
                String insertedString = outputStream.toString(charsetName);
                ByteArrayEditableData clipData = new ByteArrayEditableData();
                CodeAreaUtils.insertHexStringIntoData(insertedString, clipData, codeType);
                resultData.binaryData = clipData;
                return resultData;
            } catch (UnsupportedFlavorException | IllegalStateException | IOException | IllegalArgumentException ex) {
                resultData.errorText = "Error: " + ex.getMessage();
            }
        }

        return resultData;
    }

    @Nonnull
    private CodeAreaCommand pasteBinaryData(BinaryData pastedData, CodeAreaCore codeArea) {
        DeleteSelectionCommand deleteSelectionCommand = null;
        if (codeArea.hasSelection()) {
            deleteSelectionCommand = new DeleteSelectionCommand(codeArea);
        }

        PasteDataCommand pasteDataCommand = new PasteDataCommand(codeArea, pastedData);
        return CodeAreaCompoundCommand.buildCompoundCommand(codeArea, deleteSelectionCommand, pasteDataCommand);
    }

    private class ResultData {

        BinaryData binaryData;
        String errorText;
    }
}
