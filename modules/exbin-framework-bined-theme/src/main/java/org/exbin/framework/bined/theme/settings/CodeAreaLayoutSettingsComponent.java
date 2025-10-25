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
package org.exbin.framework.bined.theme.settings;

import java.awt.Dialog;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.swing.section.layout.DefaultSectionCodeAreaLayoutProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.settings.gui.LayoutProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.LayoutProfilesSettingsPanel;
import org.exbin.framework.bined.theme.settings.gui.LayoutProfilesPanel;
import org.exbin.framework.bined.theme.settings.gui.LayoutTemplatePanel;
import org.exbin.framework.bined.theme.settings.gui.NamedProfilePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Layout profiles options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaLayoutSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "layoutProfiles";

    private EditorProvider editorProvider;

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        LayoutProfilesSettingsPanel panel = new LayoutProfilesSettingsPanel();
        panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            layoutProfilePanel.setLayoutProfile(new DefaultSectionCodeAreaLayoutProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = layoutProfilePanel.getResourceBundle();
            LayoutProfileResult result = new LayoutProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setEditProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = layoutProfilePanel.getResourceBundle();
            LayoutProfileResult result = new LayoutProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("editProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setCopyProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord) -> {
            LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
            layoutProfilePanel.setLayoutProfile(new DefaultSectionCodeAreaLayoutProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = layoutProfilePanel.getResourceBundle();
            LayoutProfileResult result = new LayoutProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("copyProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName() + panelResourceBundle.getString("copyProfile.profilePostfix"));
            layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setTemplateProfileOperation((JComponent parentComponent) -> {
            LayoutTemplatePanel layoutTemplatePanel = new LayoutTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutTemplatePanel);
            namedProfilePanel.setProfileName("");
            layoutTemplatePanel.addListSelectionListener((e) -> {
                LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = layoutTemplatePanel.getResourceBundle();
            LayoutProfileResult result = new LayoutProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addTemplate.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), layoutTemplatePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.noTemplate.message"), panelResourceBundle.getString("error.noTemplate.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new LayoutProfilesPanel.LayoutProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getLayoutProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
            return result.profile;
        });
        return panel;
    }

    private boolean isValidProfileName(@Nullable String profileName) {
        return profileName != null && !"".equals(profileName.trim());
    }

    class LayoutProfileResult {

        LayoutProfilesPanel.LayoutProfile profile;
    }
}
