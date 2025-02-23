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
package org.exbin.framework.bined.inspector.options.gui;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.options.DataInspectorOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.text.font.options.TextFontOptions;
import org.exbin.framework.text.font.options.gui.TextFontOptionsPanel;
import org.exbin.framework.text.font.service.TextFontService;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Data inspector options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorOptionsPanel extends javax.swing.JPanel implements OptionsComponent<DataInspectorOptions> {

    private OptionsModifiedListener optionsModifiedListener;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DataInspectorOptionsPanel.class);
    private TextFontOptionsPanel textFontOptionsPanel;
    private Font defaultFont;
    private Font currentFont;

    public DataInspectorOptionsPanel() {
        initComponents();
        init();
    }

    private void init() {
        textFontOptionsPanel = new TextFontOptionsPanel();
        textFontOptionsPanel.setTextFontService(new TextFontService() {
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
        fontChangePanel.add(textFontOptionsPanel);
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setTextFontService(TextFontService textFontService) {
        textFontOptionsPanel.setTextFontService(textFontService);
    }

    public void setFontChangeAction(TextFontOptionsPanel.FontChangeAction fontChangeAction) {
        textFontOptionsPanel.setFontChangeAction(fontChangeAction);
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    public void setCurrentFont(Font currentFont) {
        this.currentFont = currentFont;
    }

    @Override
    public void loadFromOptions(DataInspectorOptions options) {
        showParsingPanelCheckBox.setSelected(options.isShowParsingPanel());
        TextFontOptions textFontOptions = new TextFontOptions(new DefaultOptionsStorage());
        textFontOptions.setUseDefaultFont(options.isUseDefaultFont());
        Map<TextAttribute, ?> fontAttributes = options.getFontAttributes();
        textFontOptions.setFontAttributes((fontAttributes == null) ? defaultFont.getAttributes() : fontAttributes);
        textFontOptionsPanel.loadFromOptions(textFontOptions);
    }

    @Override
    public void saveToOptions(DataInspectorOptions options) {
        options.setShowParsingPanel(showParsingPanelCheckBox.isSelected());
        TextFontOptions textFontOptions = new TextFontOptions(new DefaultOptionsStorage());
        textFontOptionsPanel.saveToOptions(textFontOptions);
        options.setUseDefaultFont(textFontOptions.isUseDefaultFont());
        options.setFontAttributes(textFontOptions.getFontAttributes());
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

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new DataInspectorOptionsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fieldsFontLabel;
    private javax.swing.JPanel fontChangePanel;
    private javax.swing.JCheckBox showParsingPanelCheckBox;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }
}
