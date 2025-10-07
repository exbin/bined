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

import java.awt.BorderLayout;
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
import org.exbin.bined.EditMode;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.code.CodeImportFormat;

/**
 * Paste from code method panel.
 */
@ParametersAreNonnullByDefault
public class PasteFromCodePanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(PasteFromCodePanel.class);
    private final List<CodeImportFormat> importFormats = new ArrayList<>();

    private SectCodeArea binaryPreviewArea;
    private BinaryData parsedData;

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
        // tryPasteFromClipboard();
        // Initialize binary preview area
        binaryPreviewArea = new SectCodeArea();
        binaryPreviewArea.setContentData(new ByteArrayEditableData());
        binaryPreviewArea.setEditMode(EditMode.READ_ONLY);

        // Ensure preview area is visible
        binaryPreviewArea.setVisible(true);
        binaryPreviewArea.setPreferredSize(new java.awt.Dimension(400, 150));

        binaryPreviewPanel.setLayout(new BorderLayout());
        binaryPreviewPanel.add(binaryPreviewArea, BorderLayout.CENTER);
        binaryPreviewPanel.revalidate();
        binaryPreviewPanel.repaint();

        System.out.println("PasteFromCodePanel: Binary preview area initialized"); // Debug

        // Add listener to format combo box
        formatComboBox.addActionListener(e -> {
            updateBinaryPreview();
        });

        // Add listener to auto-detect checkbox
        autoDetectCheckBox.addActionListener(e -> {
            boolean autoDetect = autoDetectCheckBox.isSelected();
            formatComboBox.setEnabled(!autoDetect);
            updateBinaryPreview();
        });

        // Add listener to code text area
        codeTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBinaryPreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBinaryPreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateBinaryPreview();
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

    private void updateBinaryPreview() {
        String code = codeTextArea.getText();
        System.out.println("PasteFromCodePanel: Updating preview, code length: " + (code != null ? code.length() : 0)); // Debug

        if (code == null || code.trim().isEmpty()) {
            binaryPreviewArea.setContentData(new ByteArrayEditableData());
            parsedData = null;
            binaryPreviewArea.repaint();
            System.out.println("PasteFromCodePanel: Code is empty, cleared preview"); // Debug
            return;
        }

        CodeImportFormat format;
        if (autoDetectCheckBox.isSelected()) {
            format = detectFormat(code);
            System.out.println("PasteFromCodePanel: Auto-detect format: " + (format != null ? format.getFormatName() : "null")); // Debug
            if (format == null) {
                binaryPreviewArea.setContentData(new ByteArrayEditableData());
                parsedData = null;
                binaryPreviewArea.repaint();
                System.out.println("PasteFromCodePanel: Format not detected"); // Debug
                return;
            }
        } else {
            format = getSelectedFormat();
            System.out.println("PasteFromCodePanel: Manual format: " + (format != null ? format.getFormatName() : "null")); // Debug
            if (format == null) {
                return;
            }
        }

        try {
            BinaryData data = format.parseCode(code);
            System.out.println("PasteFromCodePanel: Parsed " + data.getDataSize() + " bytes"); // Debug
            binaryPreviewArea.setContentData(data);
            parsedData = data;
            binaryPreviewArea.repaint();
            binaryPreviewArea.revalidate();
            System.out.println("PasteFromCodePanel: Preview updated successfully"); // Debug
        } catch (CodeImportFormat.CodeParseException ex) {
            // Show error in preview (empty data)
            System.out.println("PasteFromCodePanel: Parse error: " + ex.getMessage()); // Debug
            binaryPreviewArea.setContentData(new ByteArrayEditableData());
            parsedData = null;
            binaryPreviewArea.repaint();
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
        binaryPreviewLabel = new javax.swing.JLabel();
        binaryPreviewPanel = new javax.swing.JPanel();

        codeLabel.setText(resourceBundle.getString("codeLabel.text"));
        codeTextArea.setRows(8);
        codeScrollPane.setViewportView(codeTextArea);

        formatLabel.setText(resourceBundle.getString("formatLabel.text"));
        autoDetectCheckBox.setText(resourceBundle.getString("autoDetectCheckBox.text"));
        autoDetectCheckBox.setSelected(true);
        binaryPreviewLabel.setText(resourceBundle.getString("binaryPreviewLabel.text"));

        javax.swing.GroupLayout binaryPreviewPanelLayout = new javax.swing.GroupLayout(binaryPreviewPanel);
        binaryPreviewPanel.setLayout(binaryPreviewPanelLayout);
        binaryPreviewPanelLayout.setHorizontalGroup(
                binaryPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        binaryPreviewPanelLayout.setVerticalGroup(
                binaryPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 150, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(codeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                                        .addComponent(formatComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(binaryPreviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(codeLabel)
                                                        .addComponent(formatLabel)
                                                        .addComponent(autoDetectCheckBox)
                                                        .addComponent(binaryPreviewLabel))
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
                                .addGap(18, 18, 18)
                                .addComponent(binaryPreviewLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(binaryPreviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JLabel binaryPreviewLabel;
    private javax.swing.JPanel binaryPreviewPanel;
    // End of variables declaration
}
