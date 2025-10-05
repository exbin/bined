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
package org.exbin.framework.bined.operation.bouncycastle.component.gui;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.bouncycastle.component.SymmetricEncryptionMethod;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Encryption and decryption conversions component panel.
 */
@ParametersAreNonnullByDefault
public class EncryptionPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(EncryptionPanel.class);

    private ConfigChangeListener configChangeListener = null;

    public EncryptionPanel() {
        initComponents();
        init();
    }

    private void init() {
        encryptRadioButton.setSelected(true);
        algorithmComboBox.setSelectedIndex(1); // AES-256
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        operationButtonGroup = new javax.swing.ButtonGroup();
        operationPanel = new javax.swing.JPanel();
        operationLabel = new javax.swing.JLabel();
        encryptRadioButton = new javax.swing.JRadioButton();
        decryptRadioButton = new javax.swing.JRadioButton();
        algorithmLabel = new javax.swing.JLabel();
        algorithmComboBox = new javax.swing.JComboBox<>();
        passwordPanel = new javax.swing.JPanel();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();

        setLayout(new java.awt.BorderLayout());

        operationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("operationPanel.title"))); // NOI18N

        operationLabel.setText(resourceBundle.getString("operationLabel.text")); // NOI18N

        operationButtonGroup.add(encryptRadioButton);
        encryptRadioButton.setSelected(true);
        encryptRadioButton.setText(resourceBundle.getString("encryptRadioButton.text")); // NOI18N
        encryptRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                encryptRadioButtonItemStateChanged(evt);
            }
        });

        operationButtonGroup.add(decryptRadioButton);
        decryptRadioButton.setText(resourceBundle.getString("decryptRadioButton.text")); // NOI18N
        decryptRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                decryptRadioButtonItemStateChanged(evt);
            }
        });

        algorithmLabel.setText(resourceBundle.getString("algorithmLabel.text")); // NOI18N

        algorithmComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            resourceBundle.getString("algorithm.aes128"),
            resourceBundle.getString("algorithm.aes256")
        }));
        algorithmComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                algorithmComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout operationPanelLayout = new javax.swing.GroupLayout(operationPanel);
        operationPanel.setLayout(operationPanelLayout);
        operationPanelLayout.setHorizontalGroup(
                operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(operationPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(operationLabel)
                                        .addComponent(algorithmLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(algorithmComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(operationPanelLayout.createSequentialGroup()
                                                .addComponent(encryptRadioButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(decryptRadioButton)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        operationPanelLayout.setVerticalGroup(
                operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(operationPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(operationLabel)
                                        .addComponent(encryptRadioButton)
                                        .addComponent(decryptRadioButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(operationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(algorithmLabel)
                                        .addComponent(algorithmComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(operationPanel, java.awt.BorderLayout.CENTER);

        passwordPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("passwordPanel.title"))); // NOI18N

        passwordLabel.setText(resourceBundle.getString("passwordLabel.text")); // NOI18N

        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout passwordPanelLayout = new javax.swing.GroupLayout(passwordPanel);
        passwordPanel.setLayout(passwordPanelLayout);
        passwordPanelLayout.setHorizontalGroup(
                passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(passwordPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(passwordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addContainerGap())
        );
        passwordPanelLayout.setVerticalGroup(
                passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(passwordPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(passwordLabel)
                                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(passwordPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void encryptRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_encryptRadioButtonItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fireConfigChanged();
        }
    }//GEN-LAST:event_encryptRadioButtonItemStateChanged

    private void decryptRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_decryptRadioButtonItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fireConfigChanged();
        }
    }//GEN-LAST:event_decryptRadioButtonItemStateChanged

    private void algorithmComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_algorithmComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            fireConfigChanged();
        }
    }//GEN-LAST:event_algorithmComboBoxItemStateChanged

    private void passwordFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyReleased
        fireConfigChanged();
    }//GEN-LAST:event_passwordFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> algorithmComboBox;
    private javax.swing.JLabel algorithmLabel;
    private javax.swing.JRadioButton decryptRadioButton;
    private javax.swing.JRadioButton encryptRadioButton;
    private javax.swing.ButtonGroup operationButtonGroup;
    private javax.swing.JLabel operationLabel;
    private javax.swing.JPanel operationPanel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPanel passwordPanel;
    // End of variables declaration//GEN-END:variables

    public void initFocus() {
        passwordField.requestFocus();
    }

    @Nonnull
    public SymmetricEncryptionMethod.OperationType getOperationType() {
        return encryptRadioButton.isSelected()
                ? SymmetricEncryptionMethod.OperationType.ENCRYPT
                : SymmetricEncryptionMethod.OperationType.DECRYPT;
    }

    @Nonnull
    public SymmetricEncryptionMethod.Algorithm getAlgorithm() {
        return algorithmComboBox.getSelectedIndex() == 0
                ? SymmetricEncryptionMethod.Algorithm.AES_128
                : SymmetricEncryptionMethod.Algorithm.AES_256;
    }

    @Nullable
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void setConfigChangeListener(ConfigChangeListener listener) {
        this.configChangeListener = listener;
    }

    private void fireConfigChanged() {
        if (configChangeListener != null) {
            configChangeListener.configChanged();
        }
    }

    public interface ConfigChangeListener {

        void configChanged();
    }
}
