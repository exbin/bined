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
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.options.CodeAreaLayoutProfileOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Layout profiles options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LayoutProfilesOptionsPanel extends javax.swing.JPanel implements OptionsComponent<CodeAreaLayoutOptions> {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(LayoutProfilesOptionsPanel.class);

    private final ProfileSelectionPanel selectionPanel;
    private final LayoutProfilesPanel profilesPanel;

    public LayoutProfilesOptionsPanel() {
        this.profilesPanel = new LayoutProfilesPanel();
        selectionPanel = new ProfileSelectionPanel(profilesPanel);
        initComponents();
        init();
    }

    private void init() {
        add(selectionPanel, BorderLayout.NORTH);
        add(profilesPanel, BorderLayout.CENTER);
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setAddProfileOperation(LayoutProfilesPanel.AddProfileOperation addProfileOperation) {
        profilesPanel.setAddProfileOperation(addProfileOperation);
    }

    public void setEditProfileOperation(LayoutProfilesPanel.EditProfileOperation editProfileOperation) {
        profilesPanel.setEditProfileOperation(editProfileOperation);
    }

    public void setCopyProfileOperation(LayoutProfilesPanel.CopyProfileOperation copyProfileOperation) {
        profilesPanel.setCopyProfileOperation(copyProfileOperation);
    }

    public void setTemplateProfileOperation(LayoutProfilesPanel.TemplateProfileOperation templateProfileOperation) {
        profilesPanel.setTemplateProfileOperation(templateProfileOperation);
    }

    @Override
    public void loadFromOptions(CodeAreaLayoutOptions options) {
        profilesPanel.loadFromOptions(new CodeAreaLayoutProfileOptions(options));
        selectionPanel.setDefaultProfile(options.getSelectedProfile());
    }

    @Override
    public void saveToOptions(CodeAreaLayoutOptions options) {
        profilesPanel.saveToOptions(new CodeAreaLayoutProfileOptions(options));
        options.setSelectedProfile(selectionPanel.getDefaultProfile());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
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
            WindowUtils.invokeWindow(new LayoutProfilesOptionsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {

    }
}
