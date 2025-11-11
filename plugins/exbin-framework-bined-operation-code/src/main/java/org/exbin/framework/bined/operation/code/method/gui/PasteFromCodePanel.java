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
package org.exbin.framework.bined.operation.code.method.gui;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.api.ParamChangeListener;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.code.CodeImportFormat;

/**
 * Paste from code method panel.
 */
@ParametersAreNonnullByDefault
public class PasteFromCodePanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(PasteFromCodePanel.class);
    private final List<CodeImportFormat> importFormats = new ArrayList<>();

    private BinaryData parsedData;
    private ParamChangeListener paramChangeListener = null;
    private String errorText = "";

    public PasteFromCodePanel() {
        initComponents();
        init();
    }

    private void init() {
        // Setup code input area
        codeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codeTextArea.setLineWrap(true);
        codeTextArea.setWrapStyleWord(false);

        // DON'T auto-paste from clipboard - user should paste manually
        tryPasteFromClipboard();

        // Add listener to format combo box
        formatComboBox.addActionListener(e -> {
            paramChanged();
        });

        // Add listener to auto-detect checkbox
        autoDetectCheckBox.addActionListener(e -> {
            boolean autoDetect = autoDetectCheckBox.isSelected();
            formatComboBox.setEnabled(!autoDetect);
            paramChanged();
        });

        // Add listener to code text area
        codeTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                paramChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                paramChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                paramChanged();
            }
        });
    }

    private void tryPasteFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                String clipboardText = (String) clipboard.getData(DataFlavor.stringFlavor);
                if (clipboardText != null && !clipboardText.isEmpty()) {
                    codeTextArea.setText(clipboardText);
                }
            }
        } catch (Exception ex) {
            // Ignore clipboard errors
        }
    }

    public void setImportFormats(List<CodeImportFormat> formats) {
        importFormats.clear();
        importFormats.addAll(formats);

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (CodeImportFormat format : formats) {
            model.addElement(format.getFormatName());
        }
        formatComboBox.setModel(model);

        if (!formats.isEmpty()) {
            formatComboBox.setSelectedIndex(0);
        }

        // Force UI refresh and ensure visibility
        formatComboBox.setVisible(true);
        formatComboBox.revalidate();
        formatComboBox.repaint();
        this.revalidate();
        this.repaint();
    }

    @Nullable
    public CodeImportFormat getSelectedFormat() {
        int index = formatComboBox.getSelectedIndex();
        if (index >= 0 && index < importFormats.size()) {
            return importFormats.get(index);
        }
        return null;
    }

    @Nullable
    private CodeImportFormat detectFormat(String code) {
        for (CodeImportFormat format : importFormats) {
            if (format.canParse(code)) {
                return format;
            }
        }
        return null;
    }

    public void setParamChangeListener(ParamChangeListener paramChangeListener) {
        this.paramChangeListener = paramChangeListener;
    }

    private void paramChanged() {
        updateBinaryPreview();
        if (paramChangeListener != null) {
            paramChangeListener.paramChanged();
        }
    }

    @Nonnull
    public BinaryData getResultData() {
        return parsedData;
    }

    @Nonnull
    public String getErrorText() {
        return errorText;
    }

    private void updateBinaryPreview() {
        parsedData = new ByteArrayEditableData();
        errorText = "";
        String code = codeTextArea.getText();
        if (code == null || code.trim().isEmpty()) {
            return;
        }

        CodeImportFormat format;
        if (autoDetectCheckBox.isSelected()) {
            format = detectFormat(code);
            if (format == null) {
                return;
            }
        } else {
            format = getSelectedFormat();
            if (format == null) {
                return;
            }
        }

        try {
            parsedData = format.parseCode(code);
        } catch (CodeImportFormat.CodeParseException ex) {
            errorText = "Error: " + ex.getMessage();
        }
    }

    @Nullable
    public BinaryData getParsedData() {
        return parsedData;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        codeLabel = new javax.swing.JLabel();
        codeScrollPane = new javax.swing.JScrollPane();
        codeTextArea = new javax.swing.JTextArea();
        formatLabel = new javax.swing.JLabel();
        formatComboBox = new javax.swing.JComboBox<>();
        autoDetectCheckBox = new javax.swing.JCheckBox();

        codeLabel.setText(resourceBundle.getString("codeLabel.text"));
        codeTextArea.setRows(8);
        codeScrollPane.setViewportView(codeTextArea);

        formatLabel.setText(resourceBundle.getString("formatLabel.text"));
        autoDetectCheckBox.setText(resourceBundle.getString("autoDetectCheckBox.text"));
        autoDetectCheckBox.setSelected(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(codeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                                        .addComponent(formatComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(codeLabel)
                                                        .addComponent(formatLabel)
                                                        .addComponent(autoDetectCheckBox))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(codeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(codeScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(formatLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(autoDetectCheckBox)
                                .addContainerGap())
        );
    }

    // Variables declaration
    private javax.swing.JLabel codeLabel;
    private javax.swing.JScrollPane codeScrollPane;
    private javax.swing.JTextArea codeTextArea;
    private javax.swing.JLabel formatLabel;
    private javax.swing.JComboBox<String> formatComboBox;
    private javax.swing.JCheckBox autoDetectCheckBox;
    // End of variables declaration
}
