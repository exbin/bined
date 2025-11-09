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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.model.ColorThemeProfile;
import org.exbin.framework.bined.theme.model.ThemeProfile;
import org.exbin.framework.bined.theme.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.theme.gui.ThemeTemplatesPanel;
import org.exbin.framework.bined.theme.model.ThemeProfilesListModel;
import org.exbin.framework.bined.theme.settings.gui.ColorProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.ColorProfilesSettingsPanel;
import org.exbin.framework.bined.theme.settings.gui.NamedProfilePanel;
import org.exbin.framework.bined.theme.settings.gui.PreviewPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
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
public class CodeAreaColorSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "colorProfiles";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ColorProfilesSettingsPanel panel = new ColorProfilesSettingsPanel();
        panel.setThemeProfileController(new ThemeProfilesPanel.Controller() {
            @Nonnull
            @Override
            public Optional<ThemeProfile> addProfile() {
                ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
                ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
                colorProfilePanel.setColorProfile(new SectionCodeAreaColorProfile());
                NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
                namedProfilePanel.setProfileName(CodeAreaColorSettingsComponent.getNewProfileName(panel, panelResourceBundle));
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);
                JComponent parentComponent = panel;

                ColorThemeProfile[] result = new ColorThemeProfile[1];
                final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("addProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
                windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
                controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                    if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                        if (!CodeAreaColorSettingsComponent.isValidProfileName(namedProfilePanel.getProfileName())) {
                            CodeAreaColorSettingsComponent.showInvalidNameErrorMessage(parentComponent, panelResourceBundle);
                            return;
                        }

                        result[0] = new ColorThemeProfile(
                                namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                        );
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(parentComponent);

                return Optional.ofNullable(result[0]);
            }

            @Nonnull
            @Override
            public Optional<ThemeProfile> editProfile(ThemeProfile profile) {
                ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
                ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
                NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);
                JComponent parentComponent = panel;

                ColorThemeProfile[] result = new ColorThemeProfile[1];
                final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("editProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
                windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
                namedProfilePanel.setProfileName(profile.getProfileName());
                colorProfilePanel.setColorProfile(((ColorThemeProfile) profile).getColorProfile());
                controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                    if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                        if (!CodeAreaColorSettingsComponent.isValidProfileName(namedProfilePanel.getProfileName())) {
                            CodeAreaColorSettingsComponent.showInvalidNameErrorMessage(parentComponent, panelResourceBundle);
                            return;
                        }

                        result[0] = new ColorThemeProfile(
                                namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                        );
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(parentComponent);

                return Optional.ofNullable(result[0]);
            }

            @Nonnull
            @Override
            public ThemeProfile copyProfile(ThemeProfile profile) {
                ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
                ResourceBundle panelResourceBundle = colorProfilePanel.getResourceBundle();
                colorProfilePanel.setColorProfile(new SectionCodeAreaColorProfile());
                NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);
                JComponent parentComponent = panel;

                ColorThemeProfile[] result = new ColorThemeProfile[1];
                final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, panelResourceBundle.getString("copyProfile.title"), Dialog.ModalityType.APPLICATION_MODAL);
                windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), panelResourceBundle);
                namedProfilePanel.setProfileName(profile.getProfileName() + panelResourceBundle.getString("copyProfile.profilePostfix"));
                colorProfilePanel.setColorProfile(((ColorThemeProfile) profile).getColorProfile());
                controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                    if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                        if (!CodeAreaColorSettingsComponent.isValidProfileName(namedProfilePanel.getProfileName())) {
                            CodeAreaColorSettingsComponent.showInvalidNameErrorMessage(parentComponent, panelResourceBundle);
                            return;
                        }

                        result[0] = new ColorThemeProfile(
                                namedProfilePanel.getProfileName(), colorProfilePanel.getColorProfile()
                        );
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(parentComponent);

                return result[0];
            }

            @Nonnull
            @Override
            public Optional<ThemeProfile> addTemplate() {
                ThemeTemplatesPanel templatePanel = new ThemeTemplatesPanel();
                templatePanel.setController(new ThemeTemplatesPanel.Controller() {
                    @Override
                    public void updatePreview(PreviewPanel previewPanel, ThemeProfile themeProfile) {
                        previewPanel.getCodeArea().setColorsProfile(((ColorThemeProfile) themeProfile).getColorProfile());
                    }
                });

                OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                CodeAreaColorProfileOptions options = new CodeAreaColorProfileOptions(optionsModule.createMemoryStorage());
                try (InputStream stream = getClass().getResourceAsStream("/org/exbin/framework/bined/theme/resources/templates/colorTemplates.xml")) {
                    options.loadFromOptions(new CodeAreaColorOptions(optionsModule.createStreamPreferencesStorage(stream)));
                    List<ThemeProfile> profiles = new ArrayList<>();
                    List<String> profileNames = options.getProfileNames();
                    for (int index = 0; index < profileNames.size(); index++) {
                        ColorThemeProfile profile = new ColorThemeProfile(
                                profileNames.get(index),
                                options.getColorsProfile(index)
                        );
                        profiles.add(profile);
                    }

                    ThemeProfilesListModel model = templatePanel.getProfilesListModel();
                    model.setProfiles(profiles);
                } catch (IOException ex) {
                    Logger.getLogger(CodeAreaColorSettingsComponent.class.getName()).log(Level.SEVERE, null, ex);
                }

                ResourceBundle panelResourceBundle = templatePanel.getResourceBundle();
                LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
                ResourceBundle headerResourceBundle = languageModule.getResourceBundleByBundleName("org.exbin.framework.bined.theme.settings.gui.resources.ColorTemplatePanel");
                NamedProfilePanel namedProfilePanel = new NamedProfilePanel(templatePanel);
                namedProfilePanel.setProfileName("");
                templatePanel.addListSelectionListener((e) -> {
                    ColorThemeProfile selectedTemplate = (ColorThemeProfile) templatePanel.getSelectedTemplate();
                    namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
                });
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);
                JComponent parentComponent = panel;

                ColorThemeProfile[] result = new ColorThemeProfile[1];
                final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, headerResourceBundle.getString("addTemplate.title"), Dialog.ModalityType.APPLICATION_MODAL);
                windowModule.addHeaderPanel(dialog.getWindow(), templatePanel.getClass(), headerResourceBundle);
                controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                    if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                        if (!CodeAreaColorSettingsComponent.isValidProfileName(namedProfilePanel.getProfileName())) {
                            CodeAreaColorSettingsComponent.showInvalidNameErrorMessage(parentComponent, panelResourceBundle);
                            return;
                        }

                        ColorThemeProfile selectedTemplate = (ColorThemeProfile) templatePanel.getSelectedTemplate();
                        if (selectedTemplate == null) {
                            JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.noTemplate.message"), panelResourceBundle.getString("error.noTemplate.title"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        result[0] = new ColorThemeProfile(
                                namedProfilePanel.getProfileName(), selectedTemplate.getColorProfile()
                        );
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(parentComponent);

                return Optional.ofNullable(result[0]);
            }

            @Override
            public void updatePreview(PreviewPanel previewPanel, ThemeProfile themeProfile) {
                previewPanel.getCodeArea().setColorsProfile(((ColorThemeProfile) themeProfile).getColorProfile());
            }
        });
        return panel;
    }

    private static boolean isValidProfileName(@Nullable String profileName) {
        return profileName != null && !"".equals(profileName.trim());
    }

    @Nonnull
    private static String getNewProfileName(ColorProfilesSettingsPanel panel, ResourceBundle resourceBundle) {
        String profileName = resourceBundle.getString("newProfile.profilePrefix");
        int profileIndex = 1;
        while (CodeAreaColorSettingsComponent.hasProfileWithName(panel, profileName + profileIndex)) {
            profileIndex++;
        }

        return profileName + profileIndex;
    }

    private static boolean hasProfileWithName(ColorProfilesSettingsPanel panel, String profileName) {
        return panel.getProfiles().stream().anyMatch((profile) -> (profileName.equals(profile.getProfileName())));
    }

    private static void showInvalidNameErrorMessage(JComponent parentComponent, ResourceBundle panelResourceBundle) {
        JOptionPane.showMessageDialog(parentComponent, panelResourceBundle.getString("error.invalidName.message"), panelResourceBundle.getString("error.invalidName.title"), JOptionPane.ERROR_MESSAGE);
    }
}
