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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.api.CopyAsDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.code.CodeExportFormat;
import org.exbin.framework.bined.operation.code.format.CHexArrayFormat;
import org.exbin.framework.bined.operation.code.format.JavaByteArrayFormat;
import org.exbin.framework.bined.operation.code.format.PythonBytesFormat;
import org.exbin.framework.bined.operation.code.method.gui.CopyAsCodePanel;
import org.exbin.framework.bined.operation.gui.TextPreviewPanel;

/**
 * Copy binary data as programming language code method.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CopyAsCodeDataMethod implements CopyAsDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CopyAsCodePanel.class);

    private final List<CodeExportFormat> exportFormats;
    private long previewLengthLimit = 0;
    private PreviewDataHandler previewDataHandler;
    private TextPreviewPanel previewPanel;

    public CopyAsCodeDataMethod() {
        // Initialize available export formats
        exportFormats = Arrays.asList(
            new JavaByteArrayFormat(),
            new PythonBytesFormat(),
            new CHexArrayFormat()
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
        final CopyAsCodePanel copyAsCodePanel = new CopyAsCodePanel();
        copyAsCodePanel.setExportFormats(exportFormats);
//        if (sourceData != null && !sourceData.isEmpty()) {
//            copyAsCodePanel.setSourceData(sourceData);
//        }
        return copyAsCodePanel;
    }

    @Override
    public void initFocus(Component component) {
        // ((CopyAsCodePanel) component).initFocus();
    }

    @Override
    public void performCopy(Component component, CodeAreaCore codeArea) {
        String code = ((CopyAsCodePanel) component).getResultText();
        StringSelection stringSelection = new StringSelection(code);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        CopyAsCodePanel panel = (CopyAsCodePanel) component;
        panel.setResultChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(CopyAsCodePanel panel, CodeAreaCore codeArea) {
        previewPanel = new TextPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);
        SwingUtilities.invokeLater(() -> {
            EditableBinaryData previewBinaryData = new ByteArrayEditableData();
            previewBinaryData.clear();
            long position;
            long length;
            SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
            if (selection.isEmpty()) {
                position = 0;
                length = codeArea.getDataSize();
            } else {
                position = selection.getFirst();
                length = selection.getLength();
            }
            previewBinaryData.insert(0, codeArea.getContentData().copy(position, length));
            panel.setSourceData(previewBinaryData);
            String errorText = panel.getErrorText();
            if (errorText.isEmpty()) {
                previewPanel.setPreviewText(panel.getResultText());
            } else {
                previewPanel.setErrorMessage(errorText);
            }
        });
    }
}
