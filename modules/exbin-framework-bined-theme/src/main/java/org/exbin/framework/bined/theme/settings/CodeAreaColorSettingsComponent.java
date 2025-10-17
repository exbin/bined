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
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.settings.gui.ColorProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.ColorProfilesSettingsPanel;
import org.exbin.framework.bined.theme.settings.gui.ColorProfilesPanel;
import org.exbin.framework.bined.theme.settings.gui.ColorTemplatePanel;
import org.exbin.framework.bined.theme.settings.gui.NamedProfilePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Color profiles settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaColorSettingsComponent implements SettingsComponentProvider<CodeAreaColorOptions> {

    public static final String PAGE_ID = "colorProfiles";

    private EditorProvider editorProvider;

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public SettingsComponent<CodeAreaColorOptions> createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ColorProfilesSettingsPanel panel = new ColorProfilesSettingsPanel();
        panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            colorProfilePanel.setColorProfile(new SectionCodeAreaColorProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
            ColorProfileResult result = new ColorProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);
            return result.profile;
        });
        panel.setEditProfileOperation((JComponent parentComponent, ColorProfilesPanel.ColorProfile profileRecord) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
            ColorProfileResult result = new ColorProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("editProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setCopyProfileOperation((JComponent parentComponent, ColorProfilesPanel.ColorProfile profileRecord) -> {
            ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
            colorProfilePanel.setColorProfile(new SectionCodeAreaColorProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
            ColorProfileResult result = new ColorProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("copyProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName() + panelResourceBundle.getString("copyProfile.profilePostfix"));
            colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setTemplateProfileOperation((JComponent parentComponent) -> {
            ColorTemplatePanel colorTemplatePanel = new ColorTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorTemplatePanel);
            namedProfilePanel.setProfileName("");
            colorTemplatePanel.addListSelectionListener((e) -> {
                ColorTemplatePanel.ColorProfile selectedTemplate = colorTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = colorTemplatePanel.getResourceBundle();
            ColorProfileResult result = new ColorProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addTemplate.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), colorTemplatePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ColorTemplatePanel.ColorProfile selectedTemplate = colorTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.noTemplate.message"), panelResourceBundle.getString("error.noTemplate.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ColorProfilesPanel.ColorProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getColorProfile()
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

    class ColorProfileResult {

        ColorProfilesPanel.ColorProfile profile;
    }
}
