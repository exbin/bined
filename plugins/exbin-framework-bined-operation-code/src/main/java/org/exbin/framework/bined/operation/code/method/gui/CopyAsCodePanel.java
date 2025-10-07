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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.code.CodeExportFormat;
import org.exbin.framework.bined.operation.code.CodeExportOptions;

/**
 * Copy as code method panel.
 */
@ParametersAreNonnullByDefault
public class CopyAsCodePanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CopyAsCodePanel.class);
    private final List<CodeExportFormat> exportFormats = new ArrayList<>();
    private final CodeExportOptions currentOptions = new CodeExportOptions();

    private JTextArea codePreviewArea;
    private BinaryData sourceData;

    public CopyAsCodePanel() {
        initComponents();
        init();
    }

    private void init() {
        // Initialize code preview area
        codePreviewArea = new JTextArea();
        codePreviewArea.setEditable(false);
        codePreviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codePreviewArea.setLineWrap(false);
        JScrollPane previewScrollPane = new JScrollPane(codePreviewArea);
        previewPanel.setLayout(new BorderLayout());
        previewPanel.add(previewScrollPane, BorderLayout.CENTER);

        // Add listener to format combo box
        formatComboBox.addActionListener(e -> {
            updateOptionsFromFormat();
            updatePreview();
        });

        // Add listeners to option controls
        uppercaseCheckBox.addActionListener(e -> {
            currentOptions.setUppercaseHex(uppercaseCheckBox.isSelected());
            updatePreview();
        });

        lineBreaksCheckBox.addActionListener(e -> {
            currentOptions.setIncludeLineBreaks(lineBreaksCheckBox.isSelected());
            updatePreview();
        });

        variableDeclCheckBox.addActionListener(e -> {
            currentOptions.setIncludeVariableDeclaration(variableDeclCheckBox.isSelected());
            updatePreview();
        });

        bytesPerLineSpinner.addChangeListener(e -> {
            currentOptions.setBytesPerLine((Integer) bytesPerLineSpinner.getValue());
            updatePreview();
        });
    }

    public void setExportFormats(List<CodeExportFormat> formats) {
        exportFormats.clear();
        exportFormats.addAll(formats);

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (CodeExportFormat format : formats) {
            model.addElement(format.getFormatName());
        }
        formatComboBox.setModel(model);

        if (!formats.isEmpty()) {
            formatComboBox.setSelectedIndex(0);
            updateOptionsFromFormat();
        }

        // Force UI refresh and ensure visibility
        formatComboBox.setVisible(true);
        formatComboBox.revalidate();
        formatComboBox.repaint();
        this.revalidate();
        this.repaint();
    }

    public void setSourceData(BinaryData data) {
        this.sourceData = data;
        // Update preview immediately when data is set
        updatePreview();
    }

    @Nullable
    public CodeExportFormat getSelectedFormat() {
        int index = formatComboBox.getSelectedIndex();
        if (index >= 0 && index < exportFormats.size()) {
            return exportFormats.get(index);
        }
        return null;
    }

    @Nonnull
    public CodeExportOptions getCurrentOptions() {
        return currentOptions;
    }

    @Nonnull
    public String getGeneratedCode() {
        return codePreviewArea.getText();
    }

    private void updateOptionsFromFormat() {
        CodeExportFormat format = getSelectedFormat();
        if (format != null) {
            CodeExportOptions defaults = format.getDefaultOptions();
            uppercaseCheckBox.setSelected(defaults.isUppercaseHex());
            lineBreaksCheckBox.setSelected(defaults.isIncludeLineBreaks());
            variableDeclCheckBox.setSelected(defaults.isIncludeVariableDeclaration());
            bytesPerLineSpinner.setValue(defaults.getBytesPerLine());

            currentOptions.setUppercaseHex(defaults.isUppercaseHex());
            currentOptions.setIncludeLineBreaks(defaults.isIncludeLineBreaks());
            currentOptions.setIncludeVariableDeclaration(defaults.isIncludeVariableDeclaration());
            currentOptions.setBytesPerLine(defaults.getBytesPerLine());
            currentOptions.setIndentation(defaults.getIndentation());
            currentOptions.setVariableName(defaults.getVariableName());
        }
    }

    private void updatePreview() {
        if (sourceData == null) {
            codePreviewArea.setText("");
            return;
        }

        CodeExportFormat format = getSelectedFormat();
        if (format == null) {
            codePreviewArea.setText("");
            return;
        }

        try {
            String code = format.generateCode(sourceData, currentOptions);
            codePreviewArea.setText(code);
            codePreviewArea.setCaretPosition(0);
        } catch (Exception ex) {
            codePreviewArea.setText("Error generating code: " + ex.getMessage());
        }
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
        formatLabel = new javax.swing.JLabel();
        formatComboBox = new javax.swing.JComboBox<>();
        optionsLabel = new javax.swing.JLabel();
        uppercaseCheckBox = new javax.swing.JCheckBox();
        lineBreaksCheckBox = new javax.swing.JCheckBox();
        variableDeclCheckBox = new javax.swing.JCheckBox();
        bytesPerLineLabel = new javax.swing.JLabel();
        bytesPerLineSpinner = new javax.swing.JSpinner();
        previewLabel = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel();

        formatLabel.setText(resourceBundle.getString("formatLabel.text"));
        optionsLabel.setText(resourceBundle.getString("optionsLabel.text"));
        uppercaseCheckBox.setText(resourceBundle.getString("uppercaseCheckBox.text"));
        uppercaseCheckBox.setSelected(true);
        lineBreaksCheckBox.setText(resourceBundle.getString("lineBreaksCheckBox.text"));
        lineBreaksCheckBox.setSelected(true);
        variableDeclCheckBox.setText(resourceBundle.getString("variableDeclCheckBox.text"));
        variableDeclCheckBox.setSelected(true);
        bytesPerLineLabel.setText(resourceBundle.getString("bytesPerLineLabel.text"));
        bytesPerLineSpinner.setModel(new javax.swing.SpinnerNumberModel(16, 1, 64, 1));
        previewLabel.setText(resourceBundle.getString("previewLabel.text"));

        javax.swing.GroupLayout previewPanelLayout = new javax.swing.GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout.setHorizontalGroup(
                previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        previewPanelLayout.setVerticalGroup(
                previewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(formatComboBox, 0, 656, Short.MAX_VALUE)
                                        .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(formatLabel)
                                                        .addComponent(optionsLabel)
                                                        .addComponent(uppercaseCheckBox)
                                                        .addComponent(lineBreaksCheckBox)
                                                        .addComponent(variableDeclCheckBox)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(bytesPerLineLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(bytesPerLineSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(previewLabel))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(formatLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(formatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(optionsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uppercaseCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lineBreaksCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variableDeclCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(bytesPerLineLabel)
                                        .addComponent(bytesPerLineSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(previewLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }

    // Variables declaration
    private javax.swing.JLabel formatLabel;
    private javax.swing.JComboBox<String> formatComboBox;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JCheckBox uppercaseCheckBox;
    private javax.swing.JCheckBox lineBreaksCheckBox;
    private javax.swing.JCheckBox variableDeclCheckBox;
    private javax.swing.JLabel bytesPerLineLabel;
    private javax.swing.JSpinner bytesPerLineSpinner;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration
}
