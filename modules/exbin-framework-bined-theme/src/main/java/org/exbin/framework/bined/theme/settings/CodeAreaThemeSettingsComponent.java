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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.section.theme.SectionBackgroundPaintMode;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.settings.gui.NamedProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.ThemeProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.ThemeProfilesSettingsPanel;
import org.exbin.framework.bined.theme.settings.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.theme.settings.gui.ThemeTemplatePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Theme profiles options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaThemeSettingsComponent implements SettingsComponentProvider<CodeAreaThemeOptions> {

    public static final String COMPONENT_ID = "themeProfiles";

    private ResourceBundle resourceBundle;
    private EditorProvider editorProvider;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public SettingsComponent<CodeAreaThemeOptions> createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ThemeProfilesSettingsPanel panel = new ThemeProfilesSettingsPanel();
        panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
            ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
            themeProfilePanel.setThemeProfile(new SectionCodeAreaThemeProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            namedProfilePanel.setProfileName(profileName);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = themeProfilePanel.getResourceBundle();
            ThemeProfileResult result = new ThemeProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setEditProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord) -> {
            ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = themeProfilePanel.getResourceBundle();
            ThemeProfileResult result = new ThemeProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("editProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName());
            themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setCopyProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord) -> {
            ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
            themeProfilePanel.setThemeProfile(new SectionCodeAreaThemeProfile());
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = themeProfilePanel.getResourceBundle();
            ThemeProfileResult result = new ThemeProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("copyProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), panelResourceBundle);
            namedProfilePanel.setProfileName(profileRecord.getProfileName() + panelResourceBundle.getString("copyProfile.profilePostfix"));
            themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                    );
                }

                dialog.close();
                dialog.dispose();
            });
            dialog.showCentered(parentComponent);

            return result.profile;
        });
        panel.setTemplateProfileOperation((JComponent parentComponent) -> {
            ThemeTemplatePanel themeTemplatePanel = new ThemeTemplatePanel();
            NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeTemplatePanel);
            namedProfilePanel.setProfileName("");
            themeTemplatePanel.addListSelectionListener((e) -> {
                ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
            });
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

            ResourceBundle panelResourceBundle = themeTemplatePanel.getResourceBundle();
            ThemeProfileResult result = new ThemeProfileResult();
            final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addTemplate.title"), Dialog.ModalityType.APPLICATION_MODAL);
            windowModule.addHeaderPanel(dialog.getWindow(), themeTemplatePanel.getClass(), panelResourceBundle);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                    if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                    if (selectedTemplate == null) {
                        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.noTemplate.message"), panelResourceBundle.getString("error.noTemplate.title"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    result.profile = new ThemeProfilesPanel.ThemeProfile(
                            namedProfilePanel.getProfileName(), selectedTemplate.getThemeProfile()
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

    class ThemeProfileResult {

        ThemeProfilesPanel.ThemeProfile profile;
    }

    @Nonnull
    private ThemeProfilePanel createThemeProfilePanel() {
        ThemeProfilePanel themeProfilePanel = new ThemeProfilePanel();
        List<String> backgroundModes = new ArrayList<>();
        for (SectionBackgroundPaintMode mode : SectionBackgroundPaintMode.values()) {
            backgroundModes.add(resourceBundle.getString("backgroundPaintMode." + mode.name().toLowerCase()));
        }
        themeProfilePanel.setBackgroundModes(backgroundModes);
        return themeProfilePanel;
    }
}
