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
package org.exbin.framework.bined.viewer.settings.gui;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.App;
import org.exbin.framework.bined.StatusCursorPositionFormat;
import org.exbin.framework.bined.StatusDocumentSizeFormat;
import org.exbin.framework.bined.settings.StatusOptions;
import org.exbin.framework.context.api.ApplicationContextProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Editor status bar options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(StatusSettingsPanel.class);

    public StatusSettingsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setCursorPositionCodeTypes(List<String> cursorPositionCodeTypes) {
        for (String cursorPositionCodeType : cursorPositionCodeTypes) {
            cursorPositionCodeTypeComboBox.addItem(cursorPositionCodeType);
        }
    }

    public void setDocumentSizeCodeTypes(List<String> documentSizeCodeTypes) {
        for (String documentSizeCodeType : documentSizeCodeTypes) {
            documentSizeCodeTypeComboBox.addItem(documentSizeCodeType);
        }
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
        StatusOptions options = settingsOptionsProvider.getSettingsOptions(StatusOptions.class);
        StatusCursorPositionFormat cursorPositionFormat = options.getCursorPositionFormat();
        cursorPositionCodeTypeComboBox.setSelectedIndex(cursorPositionFormat.getCodeType().ordinal());
        cursorPositionShowOffsetCheckBox.setSelected(cursorPositionFormat.isShowOffset());

        StatusDocumentSizeFormat documentSizeFormat = options.getDocumentSizeFormat();
        documentSizeCodeTypeComboBox.setSelectedIndex(documentSizeFormat.getCodeType().ordinal());
        cursorPositionShowOffsetCheckBox.setSelected(documentSizeFormat.isShowRelative());

        octalGroupSizeSpinner.setValue(options.getOctalSpaceGroupSize());
        decimalGroupSizeSpinner.setValue(options.getDecimalSpaceGroupSize());
        hexadecimalGroupSizeSpinner.setValue(options.getHexadecimalSpaceGroupSize());
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ApplicationContextProvider applicationContextProvider) {
        StatusOptions options = settingsOptionsProvider.getSettingsOptions(StatusOptions.class);
        StatusCursorPositionFormat cursorPositionFormat = new StatusCursorPositionFormat();
        cursorPositionFormat.setCodeType(PositionCodeType.values()[cursorPositionCodeTypeComboBox.getSelectedIndex()]);
        cursorPositionFormat.setShowOffset(cursorPositionShowOffsetCheckBox.isSelected());
        options.setCursorPositionFormat(cursorPositionFormat);

        StatusDocumentSizeFormat documentSizeFormat = new StatusDocumentSizeFormat();
        documentSizeFormat.setCodeType(PositionCodeType.values()[documentSizeCodeTypeComboBox.getSelectedIndex()]);
        documentSizeFormat.setShowRelative(cursorPositionShowOffsetCheckBox.isSelected());
        options.setDocumentSizeFormat(documentSizeFormat);

        options.setOctalSpaceGroupSize((int) octalGroupSizeSpinner.getValue());
        options.setDecimalSpaceGroupSize((int) decimalGroupSizeSpinner.getValue());
        options.setHexadecimalSpaceGroupSize((int) hexadecimalGroupSizeSpinner.getValue());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cursorPositionCodeTypeLabel = new javax.swing.JLabel();
        cursorPositionCodeTypeComboBox = new javax.swing.JComboBox<>();
        cursorPositionShowOffsetCheckBox = new javax.swing.JCheckBox();
        documentSizeCodeTypeLabel = new javax.swing.JLabel();
        documentSizeCodeTypeComboBox = new javax.swing.JComboBox<>();
        documentSizeShowRelativeCheckBox = new javax.swing.JCheckBox();
        octalGroupSizeLabel = new javax.swing.JLabel();
        octalGroupSizeSpinner = new javax.swing.JSpinner();
        decimalGroupSizeLabel = new javax.swing.JLabel();
        decimalGroupSizeSpinner = new javax.swing.JSpinner();
        hexadecimalGroupSizeLabel = new javax.swing.JLabel();
        hexadecimalGroupSizeSpinner = new javax.swing.JSpinner();

        cursorPositionCodeTypeLabel.setText(resourceBundle.getString("cursorPositionCodeTypeLabel.text")); // NOI18N

        cursorPositionShowOffsetCheckBox.setSelected(true);
        cursorPositionShowOffsetCheckBox.setText(resourceBundle.getString("cursorPositionShowOffsetCheckBox.text")); // NOI18N

        documentSizeCodeTypeLabel.setText(resourceBundle.getString("documentSizeCodeTypeLabel.text")); // NOI18N

        documentSizeShowRelativeCheckBox.setSelected(true);
        documentSizeShowRelativeCheckBox.setText(resourceBundle.getString("documentSizeShowRelativeCheckBox.text")); // NOI18N

        octalGroupSizeLabel.setText(resourceBundle.getString("octalGroupSizeLabel.text")); // NOI18N

        octalGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 0, null, 1));

        decimalGroupSizeLabel.setText(resourceBundle.getString("decimalGroupSizeLabel.text")); // NOI18N

        decimalGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 0, null, 1));

        hexadecimalGroupSizeLabel.setText(resourceBundle.getString("hexadecimalGroupSizeLabel.text")); // NOI18N

        hexadecimalGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 0, null, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cursorPositionCodeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(documentSizeCodeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cursorPositionCodeTypeLabel)
                    .addComponent(cursorPositionShowOffsetCheckBox)
                    .addComponent(documentSizeCodeTypeLabel)
                    .addComponent(documentSizeShowRelativeCheckBox)
                    .addComponent(decimalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(octalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(hexadecimalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(decimalGroupSizeLabel)
                    .addComponent(octalGroupSizeLabel)
                    .addComponent(hexadecimalGroupSizeLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cursorPositionCodeTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorPositionCodeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorPositionShowOffsetCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(documentSizeCodeTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentSizeCodeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentSizeShowRelativeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(octalGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(octalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decimalGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decimalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hexadecimalGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hexadecimalGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new StatusSettingsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cursorPositionCodeTypeComboBox;
    private javax.swing.JLabel cursorPositionCodeTypeLabel;
    private javax.swing.JCheckBox cursorPositionShowOffsetCheckBox;
    private javax.swing.JLabel decimalGroupSizeLabel;
    private javax.swing.JSpinner decimalGroupSizeSpinner;
    private javax.swing.JComboBox<String> documentSizeCodeTypeComboBox;
    private javax.swing.JLabel documentSizeCodeTypeLabel;
    private javax.swing.JCheckBox documentSizeShowRelativeCheckBox;
    private javax.swing.JLabel hexadecimalGroupSizeLabel;
    private javax.swing.JSpinner hexadecimalGroupSizeSpinner;
    private javax.swing.JLabel octalGroupSizeLabel;
    private javax.swing.JSpinner octalGroupSizeSpinner;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
    }
}
