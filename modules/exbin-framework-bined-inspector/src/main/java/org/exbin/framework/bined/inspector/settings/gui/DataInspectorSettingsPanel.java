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
package org.exbin.framework.bined.inspector.settings.gui;

import java.awt.Font;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinaryFileDocument;
import org.exbin.framework.bined.inspector.BasicValuesInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorComponentExtension;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.bined.inspector.settings.DataInspectorOptions;
import org.exbin.framework.bined.inspector.settings.DataInspectorFontOptions;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.document.api.ContextDocument;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsOverrides;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.text.font.settings.TextFontOptions;
import org.exbin.framework.text.font.settings.gui.TextFontSettingsPanel;
import org.exbin.framework.text.font.TextFontState;

/**
 * Data inspector settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private SettingsModifiedListener settingsModifiedListener;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DataInspectorSettingsPanel.class);
    private TextFontSettingsPanel textFontSettingsPanel;
    private Font defaultFont;
    private Font currentFont;

    public DataInspectorSettingsPanel() {
        initComponents();
        init();
    }

    private void init() {
        textFontSettingsPanel = new TextFontSettingsPanel();
        textFontSettingsPanel.setTextFontState(new TextFontState() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                return currentFont;
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
                return defaultFont;
            }

            @Override
            public void setCurrentFont(Font font) {
                currentFont = font;
            }
        });
        fontChangePanel.add(textFontSettingsPanel);
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setTextFontService(TextFontState textFontService) {
        textFontSettingsPanel.setTextFontState(textFontService);
    }

    public void setFontSettingsController(TextFontSettingsPanel.Controller controller) {
        textFontSettingsPanel.setController(controller);
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    public void setCurrentFont(Font currentFont) {
        this.currentFont = currentFont;
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        DataInspectorOptions options = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);
        showParsingPanelCheckBox.setSelected(options.isShowParsingPanel());

        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsOverrides settingsOptionsOverrides = optionsSettingsModule.createSettingsOptionsOverrides(settingsOptionsProvider);
        settingsOptionsOverrides.overrideSettingsOptions(TextFontOptions.class, DataInspectorFontOptions.class);
        textFontSettingsPanel.loadFromOptions(settingsOptionsOverrides, contextProvider);
        
        if (contextProvider != null) {
            ContextDocument contextDocument = contextProvider.getActiveState(ContextDocument.class);
            if (contextDocument instanceof BinaryFileDocument) {
                BasicValuesInspector basicValuesInspector = DataInspectorSettingsPanel.getBinEdInspector((BinaryFileDocument) contextDocument);
                if (basicValuesInspector != null) {
                    currentFont = ((BasicValuesPanel) basicValuesInspector.getComponent()).getInputFieldsFont();
                }
            }
        }
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        DataInspectorOptions options = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);
        options.setShowParsingPanel(showParsingPanelCheckBox.isSelected());

        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsOverrides settingsOptionsOverrides = optionsSettingsModule.createSettingsOptionsOverrides(settingsOptionsProvider);
        settingsOptionsOverrides.overrideSettingsOptions(TextFontOptions.class, DataInspectorFontOptions.class);
        textFontSettingsPanel.saveToOptions(settingsOptionsOverrides, contextProvider);
    }

    @Nullable
    private static BasicValuesInspector getBinEdInspector(BinaryFileDocument binaryDocument) {
        BinEdInspectorComponentExtension extension = binaryDocument.getComponentExtension(BinEdInspectorComponentExtension.class);
        return extension.getInspector(BasicValuesInspector.class);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        showParsingPanelCheckBox = new javax.swing.JCheckBox();
        fieldsFontLabel = new javax.swing.JLabel();
        fontChangePanel = new javax.swing.JPanel();

        setName("Form"); // NOI18N

        showParsingPanelCheckBox.setSelected(true);
        showParsingPanelCheckBox.setText(resourceBundle.getString("showParsingPanelCheckBox.text")); // NOI18N
        showParsingPanelCheckBox.setName("showParsingPanelCheckBox"); // NOI18N

        fieldsFontLabel.setText(resourceBundle.getString("fieldsFontLabel.text")); // NOI18N
        fieldsFontLabel.setName("fieldsFontLabel"); // NOI18N

        fontChangePanel.setName("fontChangePanel"); // NOI18N
        fontChangePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fontChangePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showParsingPanelCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fieldsFontLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showParsingPanelCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldsFontLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontChangePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fieldsFontLabel;
    private javax.swing.JPanel fontChangePanel;
    private javax.swing.JCheckBox showParsingPanelCheckBox;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.notifyModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }
}
