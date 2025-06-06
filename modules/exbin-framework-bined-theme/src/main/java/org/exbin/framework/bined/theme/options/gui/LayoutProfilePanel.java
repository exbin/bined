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
package org.exbin.framework.bined.theme.options.gui;

import java.awt.BorderLayout;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.utils.WindowUtils;

/**
 * Layout profile panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LayoutProfilePanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(LayoutProfilePanel.class);

    private final PreviewPanel previewPanel = new PreviewPanel();

    public LayoutProfilePanel() {
        initComponents();
        init();
    }

    private void init() {
        add(previewPanel, BorderLayout.CENTER);
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Nonnull
    public DefaultSectionCodeAreaLayoutProfile getLayoutProfile() {
        SectCodeArea codeArea = previewPanel.getCodeArea();
        DefaultSectionCodeAreaLayoutProfile layoutProfile = (DefaultSectionCodeAreaLayoutProfile) codeArea.getLayoutProfile();
        return Objects.requireNonNull(layoutProfile).createCopy();
    }

    public void setLayoutProfile(DefaultSectionCodeAreaLayoutProfile layoutProfile) {
        DefaultSectionCodeAreaLayoutProfile newLayoutProfile = layoutProfile.createCopy();
        updateLayoutProfile(newLayoutProfile);
        showHeaderCheckBox.setSelected(newLayoutProfile.isShowHeader());
        headerTopSpaceSpinner.setValue(newLayoutProfile.getTopHeaderSpace());
        headerBottomSpaceSpinner.setValue(newLayoutProfile.getBottomHeaderSpace());
        showRowPositionCheckBox.setSelected(newLayoutProfile.isShowRowPosition());
        rowPositionLeftSpaceSpinner.setValue(newLayoutProfile.getLeftRowPositionSpace());
        rowPositionRightSpaceSpinner.setValue(newLayoutProfile.getRightRowPositionSpace());
        spaceGroupSizeSpinner.setValue(newLayoutProfile.getSpaceGroupSize());
        halfSpaceGroupSizeSpinner.setValue(newLayoutProfile.getHalfSpaceGroupSize());
        doubleSpaceGroupSizeSpinner.setValue(newLayoutProfile.getDoubleSpaceGroupSize());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        preferencesScrollPane = new javax.swing.JScrollPane();
        preferencesPanel = new javax.swing.JPanel();
        halfSpaceGroupSizeLabel = new javax.swing.JLabel();
        rowPositionPanel = new javax.swing.JPanel();
        showRowPositionCheckBox = new javax.swing.JCheckBox();
        rowPositionLeftSpaceLabel = new javax.swing.JLabel();
        rowPositionLeftSpaceSpinner = new javax.swing.JSpinner();
        rowPositionRightSpaceLabel = new javax.swing.JLabel();
        rowPositionRightSpaceSpinner = new javax.swing.JSpinner();
        halfSpaceGroupSizeSpinner = new javax.swing.JSpinner();
        spaceGroupSizeLabel = new javax.swing.JLabel();
        spaceGroupSizeSpinner = new javax.swing.JSpinner();
        doubleSpaceGroupSizeLabel = new javax.swing.JLabel();
        doubleSpaceGroupSizeSpinner = new javax.swing.JSpinner();
        headerPanel = new javax.swing.JPanel();
        showHeaderCheckBox = new javax.swing.JCheckBox();
        headerTopSpaceLabel = new javax.swing.JLabel();
        headerTopSpaceSpinner = new javax.swing.JSpinner();
        headerBottomSpaceLabel = new javax.swing.JLabel();
        headerBottomSpaceSpinner = new javax.swing.JSpinner();

        setLayout(new java.awt.BorderLayout());

        halfSpaceGroupSizeLabel.setText(resourceBundle.getString("halfSpaceGroupSizeLabel.text")); // NOI18N

        rowPositionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("rowPositionPanel.border.title"))); // NOI18N

        showRowPositionCheckBox.setText(resourceBundle.getString("showRowPositionCheckBox.text")); // NOI18N
        showRowPositionCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showRowPositionCheckBoxItemStateChanged(evt);
            }
        });

        rowPositionLeftSpaceLabel.setText(resourceBundle.getString("rowPositionLeftSpaceLabel.text")); // NOI18N

        rowPositionLeftSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        rowPositionLeftSpaceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rowPositionLeftSpaceSpinnerStateChanged(evt);
            }
        });

        rowPositionRightSpaceLabel.setText(resourceBundle.getString("rowPositionRightSpaceLabel.text")); // NOI18N

        rowPositionRightSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        rowPositionRightSpaceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rowPositionRightSpaceSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout rowPositionPanelLayout = new javax.swing.GroupLayout(rowPositionPanel);
        rowPositionPanel.setLayout(rowPositionPanelLayout);
        rowPositionPanelLayout.setHorizontalGroup(
            rowPositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rowPositionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rowPositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showRowPositionCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, rowPositionPanelLayout.createSequentialGroup()
                        .addGroup(rowPositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rowPositionLeftSpaceLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rowPositionRightSpaceLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(rowPositionLeftSpaceSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(rowPositionRightSpaceSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        rowPositionPanelLayout.setVerticalGroup(
            rowPositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rowPositionPanelLayout.createSequentialGroup()
                .addComponent(showRowPositionCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowPositionLeftSpaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowPositionLeftSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowPositionRightSpaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowPositionRightSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        halfSpaceGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        halfSpaceGroupSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                halfSpaceGroupSizeSpinnerStateChanged(evt);
            }
        });

        spaceGroupSizeLabel.setText(resourceBundle.getString("spaceGroupSizeLabel.text")); // NOI18N

        spaceGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spaceGroupSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spaceGroupSizeSpinnerStateChanged(evt);
            }
        });

        doubleSpaceGroupSizeLabel.setText(resourceBundle.getString("doubleSpaceGroupSizeLabel.text")); // NOI18N

        doubleSpaceGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        doubleSpaceGroupSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                doubleSpaceGroupSizeSpinnerStateChanged(evt);
            }
        });

        headerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("headerPanel.border.title"))); // NOI18N

        showHeaderCheckBox.setText(resourceBundle.getString("showHeaderCheckBox.text")); // NOI18N
        showHeaderCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showHeaderCheckBoxItemStateChanged(evt);
            }
        });

        headerTopSpaceLabel.setText(resourceBundle.getString("headerTopSpaceLabel.text")); // NOI18N

        headerTopSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        headerTopSpaceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                headerTopSpaceSpinnerStateChanged(evt);
            }
        });

        headerBottomSpaceLabel.setText(resourceBundle.getString("headerBottomSpaceLabel.text")); // NOI18N

        headerBottomSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        headerBottomSpaceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                headerBottomSpaceSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showHeaderCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerTopSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(headerTopSpaceLabel)
                            .addComponent(headerBottomSpaceLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(headerBottomSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showHeaderCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerTopSpaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerTopSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerBottomSpaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerBottomSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout preferencesPanelLayout = new javax.swing.GroupLayout(preferencesPanel);
        preferencesPanel.setLayout(preferencesPanelLayout);
        preferencesPanelLayout.setHorizontalGroup(
            preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addComponent(halfSpaceGroupSizeLabel)
                        .addGap(51, 51, 51))
                    .addGroup(preferencesPanelLayout.createSequentialGroup()
                        .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(doubleSpaceGroupSizeLabel)
                            .addComponent(spaceGroupSizeLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, preferencesPanelLayout.createSequentialGroup()
                        .addGroup(preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(halfSpaceGroupSizeSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(spaceGroupSizeSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(doubleSpaceGroupSizeSpinner, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(headerPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rowPositionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        preferencesPanelLayout.setVerticalGroup(
            preferencesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rowPositionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(halfSpaceGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(halfSpaceGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spaceGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spaceGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doubleSpaceGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(doubleSpaceGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        preferencesScrollPane.setViewportView(preferencesPanel);

        add(preferencesScrollPane, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    private void showRowPositionCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showRowPositionCheckBoxItemStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setShowRowPosition(showRowPositionCheckBox.isSelected());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_showRowPositionCheckBoxItemStateChanged

    private void rowPositionLeftSpaceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rowPositionLeftSpaceSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setLeftRowPositionSpace((Integer) rowPositionLeftSpaceSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_rowPositionLeftSpaceSpinnerStateChanged

    private void rowPositionRightSpaceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rowPositionRightSpaceSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setRightRowPositionSpace((Integer) rowPositionRightSpaceSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_rowPositionRightSpaceSpinnerStateChanged

    private void halfSpaceGroupSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_halfSpaceGroupSizeSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setHalfSpaceGroupSize((Integer) halfSpaceGroupSizeSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_halfSpaceGroupSizeSpinnerStateChanged

    private void spaceGroupSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spaceGroupSizeSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setSpaceGroupSize((Integer) spaceGroupSizeSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_spaceGroupSizeSpinnerStateChanged

    private void doubleSpaceGroupSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_doubleSpaceGroupSizeSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setDoubleSpaceGroupSize((Integer) doubleSpaceGroupSizeSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_doubleSpaceGroupSizeSpinnerStateChanged

    private void headerBottomSpaceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_headerBottomSpaceSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setBottomHeaderSpace((Integer) headerBottomSpaceSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_headerBottomSpaceSpinnerStateChanged

    private void headerTopSpaceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_headerTopSpaceSpinnerStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setTopHeaderSpace((Integer) headerTopSpaceSpinner.getValue());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_headerTopSpaceSpinnerStateChanged

    private void showHeaderCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showHeaderCheckBoxItemStateChanged
        DefaultSectionCodeAreaLayoutProfile layoutProfile = getLayoutProfile();
        layoutProfile.setShowHeader(showHeaderCheckBox.isSelected());
        updateLayoutProfile(layoutProfile);
    }//GEN-LAST:event_showHeaderCheckBoxItemStateChanged

    private void updateLayoutProfile(DefaultSectionCodeAreaLayoutProfile layoutProfile) {
        SectCodeArea codeArea = previewPanel.getCodeArea();
        codeArea.setLayoutProfile(layoutProfile);
    }

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new LayoutProfilePanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel doubleSpaceGroupSizeLabel;
    private javax.swing.JSpinner doubleSpaceGroupSizeSpinner;
    private javax.swing.JLabel halfSpaceGroupSizeLabel;
    private javax.swing.JSpinner halfSpaceGroupSizeSpinner;
    private javax.swing.JLabel headerBottomSpaceLabel;
    private javax.swing.JSpinner headerBottomSpaceSpinner;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel headerTopSpaceLabel;
    private javax.swing.JSpinner headerTopSpaceSpinner;
    private javax.swing.JPanel preferencesPanel;
    private javax.swing.JScrollPane preferencesScrollPane;
    private javax.swing.JLabel rowPositionLeftSpaceLabel;
    private javax.swing.JSpinner rowPositionLeftSpaceSpinner;
    private javax.swing.JPanel rowPositionPanel;
    private javax.swing.JLabel rowPositionRightSpaceLabel;
    private javax.swing.JSpinner rowPositionRightSpaceSpinner;
    private javax.swing.JCheckBox showHeaderCheckBox;
    private javax.swing.JCheckBox showRowPositionCheckBox;
    private javax.swing.JLabel spaceGroupSizeLabel;
    private javax.swing.JSpinner spaceGroupSizeSpinner;
    // End of variables declaration//GEN-END:variables
}
