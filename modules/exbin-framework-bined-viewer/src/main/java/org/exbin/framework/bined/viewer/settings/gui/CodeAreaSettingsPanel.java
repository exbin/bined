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
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.RowWrappingMode;
import org.exbin.framework.App;
import org.exbin.framework.bined.viewer.settings.CodeAreaOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Code area preference parameters panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CodeAreaSettingsPanel.class);

    public CodeAreaSettingsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setViewModes(List<String> viewModes) {
        for (String viewMode : viewModes) {
            viewModeComboBox.addItem(viewMode);
        }
    }

    public void setCodeTypes(List<String> codeTypes) {
        for (String codeType : codeTypes) {
            codeTypeComboBox.addItem(codeType);
        }
    }

    public void setPositionCodeTypes(List<String> positionCodeTypes) {
        for (String positionCodeType : positionCodeTypes) {
            positionCodeTypeComboBox.addItem(positionCodeType);
        }
    }

    public void setCharactersCases(List<String> charactersCases) {
        for (String charactersCase : charactersCases) {
            codeCharactersModeComboBox.addItem(charactersCase);
        }
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider) {
        CodeAreaOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaOptions.class);
        options.setCodeType(CodeType.values()[codeTypeComboBox.getSelectedIndex()]);
        options.setShowNonprintables(showNonprintableCharactersCheckBox.isSelected());
        options.setCodeCharactersCase(CodeCharactersCase.values()[codeCharactersModeComboBox.getSelectedIndex()]);
        options.setPositionCodeType(PositionCodeType.values()[positionCodeTypeComboBox.getSelectedIndex()]);
        options.setViewMode(CodeAreaViewMode.values()[viewModeComboBox.getSelectedIndex()]);
        options.setCodeColorization(codeColorizationCheckBox.isSelected());
        options.setRowWrappingMode(rowWrappingModeCheckBox.isSelected() ? RowWrappingMode.WRAPPING : RowWrappingMode.NO_WRAPPING);
        options.setMaxBytesPerRow((Integer) maxBytesPerRowSpinner.getValue());
        options.setMinRowPositionLength((Integer) minRowPositionLengthSpinner.getValue());
        options.setMaxRowPositionLength((Integer) maxRowPositionLengthSpinner.getValue());
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider) {
        CodeAreaOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaOptions.class);
        codeTypeComboBox.setSelectedIndex(options.getCodeType().ordinal());
        showNonprintableCharactersCheckBox.setSelected(options.isShowNonprintables());
        codeCharactersModeComboBox.setSelectedIndex(options.getCodeCharactersCase().ordinal());
        positionCodeTypeComboBox.setSelectedIndex(options.getPositionCodeType().ordinal());
        viewModeComboBox.setSelectedIndex(options.getViewMode().ordinal());
        codeColorizationCheckBox.setSelected(options.isCodeColorization());
        rowWrappingModeCheckBox.setSelected(options.getRowWrappingMode() == RowWrappingMode.WRAPPING);
        maxBytesPerRowSpinner.setValue(options.getMaxBytesPerRow());
        minRowPositionLengthSpinner.setValue(options.getMinRowPositionLength());
        maxRowPositionLengthSpinner.setValue(options.getMaxRowPositionLength());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewModeModeLabel = new javax.swing.JLabel();
        viewModeComboBox = new javax.swing.JComboBox<>();
        codeTypeModeLabel = new javax.swing.JLabel();
        codeTypeComboBox = new javax.swing.JComboBox<>();
        positionCodeTypeLabel = new javax.swing.JLabel();
        positionCodeTypeComboBox = new javax.swing.JComboBox<>();
        hexCharactersModeLabel = new javax.swing.JLabel();
        codeCharactersModeComboBox = new javax.swing.JComboBox<>();
        showNonprintableCharactersCheckBox = new javax.swing.JCheckBox();
        codeColorizationCheckBox = new javax.swing.JCheckBox();
        rowWrappingModeCheckBox = new javax.swing.JCheckBox();
        maxBytesPerRowLabel = new javax.swing.JLabel();
        maxBytesPerRowSpinner = new javax.swing.JSpinner();
        minRowPositionLengthLabel = new javax.swing.JLabel();
        minRowPositionLengthSpinner = new javax.swing.JSpinner();
        maxRowPositionLengthLabel = new javax.swing.JLabel();
        maxRowPositionLengthSpinner = new javax.swing.JSpinner();

        viewModeModeLabel.setText(resourceBundle.getString("viewModeModeLabel.text")); // NOI18N

        codeTypeModeLabel.setText(resourceBundle.getString("codeTypeModeLabel.text")); // NOI18N

        positionCodeTypeLabel.setText(resourceBundle.getString("positionCodeTypeLabel.text")); // NOI18N

        hexCharactersModeLabel.setText(resourceBundle.getString("hexCharactersModeLabel.text")); // NOI18N

        showNonprintableCharactersCheckBox.setText(resourceBundle.getString("showNonprintableCharactersCheckBox.text")); // NOI18N

        codeColorizationCheckBox.setText(resourceBundle.getString("codeColorizationCheckBox.text")); // NOI18N

        rowWrappingModeCheckBox.setText(resourceBundle.getString("wrapLineModeCheckBox.text")); // NOI18N

        maxBytesPerRowLabel.setText(resourceBundle.getString("maxBytesPerRowLabel.text")); // NOI18N

        maxBytesPerRowSpinner.setModel(new javax.swing.SpinnerNumberModel(16, 0, null, 1));

        minRowPositionLengthLabel.setText(resourceBundle.getString("minRowPositionLengthLabel.text")); // NOI18N

        minRowPositionLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        minRowPositionLengthSpinner.setValue(8);

        maxRowPositionLengthLabel.setText(resourceBundle.getString("maxRowPositionLengthLabel.text")); // NOI18N

        maxRowPositionLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        maxRowPositionLengthSpinner.setValue(8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(codeCharactersModeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(positionCodeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewModeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(codeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(codeColorizationCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showNonprintableCharactersCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(maxBytesPerRowSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(minRowPositionLengthSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(maxRowPositionLengthSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hexCharactersModeLabel)
                            .addComponent(positionCodeTypeLabel)
                            .addComponent(viewModeModeLabel)
                            .addComponent(codeTypeModeLabel)
                            .addComponent(rowWrappingModeCheckBox)
                            .addComponent(maxBytesPerRowLabel)
                            .addComponent(minRowPositionLengthLabel)
                            .addComponent(maxRowPositionLengthLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewModeModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeTypeModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionCodeTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionCodeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hexCharactersModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeCharactersModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showNonprintableCharactersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeColorizationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowWrappingModeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxBytesPerRowLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxBytesPerRowSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minRowPositionLengthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minRowPositionLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxRowPositionLengthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxRowPositionLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            WindowUtils.invokeWindow(new CodeAreaSettingsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> codeCharactersModeComboBox;
    private javax.swing.JCheckBox codeColorizationCheckBox;
    private javax.swing.JComboBox<String> codeTypeComboBox;
    private javax.swing.JLabel codeTypeModeLabel;
    private javax.swing.JLabel hexCharactersModeLabel;
    private javax.swing.JLabel maxBytesPerRowLabel;
    private javax.swing.JSpinner maxBytesPerRowSpinner;
    private javax.swing.JLabel maxRowPositionLengthLabel;
    private javax.swing.JSpinner maxRowPositionLengthSpinner;
    private javax.swing.JLabel minRowPositionLengthLabel;
    private javax.swing.JSpinner minRowPositionLengthSpinner;
    private javax.swing.JComboBox<String> positionCodeTypeComboBox;
    private javax.swing.JLabel positionCodeTypeLabel;
    private javax.swing.JCheckBox rowWrappingModeCheckBox;
    private javax.swing.JCheckBox showNonprintableCharactersCheckBox;
    private javax.swing.JComboBox<String> viewModeComboBox;
    private javax.swing.JLabel viewModeModeLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
    }
}
