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
package org.exbin.framework.bined.operation.code.method;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.swing.command.InsertDataCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PasteFromDataMethod;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.code.CodeImportFormat;
import org.exbin.framework.bined.operation.code.format.CHexArrayParser;
import org.exbin.framework.bined.operation.code.format.JavaByteArrayParser;
import org.exbin.framework.bined.operation.code.format.PythonBytesParser;
import org.exbin.framework.bined.operation.code.method.gui.PasteFromCodePanel;
import org.exbin.framework.bined.operation.gui.BinaryPreviewPanel;

/**
 * Paste programming language code as binary data method.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PasteFromCodeDataMethod implements PasteFromDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(PasteFromCodePanel.class);

    private long previewLengthLimit = 0;
    private final List<CodeImportFormat> exportFormats;
    private PreviewDataHandler previewDataHandler;
    private BinaryPreviewPanel previewPanel;

    public PasteFromCodeDataMethod() {
        // Initialize available export formats
        exportFormats = Arrays.asList(
                new JavaByteArrayParser(),
                new PythonBytesParser(),
                new CHexArrayParser()
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("method.name");
    }

    @Nonnull
    @Override
    public Component createComponent() {
        final PasteFromCodePanel pasteFromCodePanel = new PasteFromCodePanel();
        pasteFromCodePanel.setImportFormats(exportFormats);
        return pasteFromCodePanel;
    }

    @Override
    public void initFocus(Component component) {
        // ((PasteFromCodePanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createPasteCommand(Component component, CodeAreaCore codeArea, long position, EditOperation editOperation) {
        BinaryData parsedData = ((PasteFromCodePanel) component).getParsedData();
        if (parsedData != null && parsedData.getDataSize() > 0) {
            return new InsertDataCommand(codeArea, position, 0, parsedData);
        }

        return null;
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        PasteFromCodePanel panel = (PasteFromCodePanel) component;
        panel.setResultChangeListener(() -> {
            fillPreviewData(panel);
        });
        fillPreviewData(panel);
    }

    private void fillPreviewData(PasteFromCodePanel panel) {
        previewPanel = new BinaryPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);
        SwingUtilities.invokeLater(() -> {
            String errorText = panel.getErrorText();
            if (errorText.isEmpty()) {
                previewPanel.setPreviewData(panel.getResultData());
            } else {
                previewPanel.setErrorMessage(errorText);
            }
        });
    }
}
