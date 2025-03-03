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
package org.exbin.framework.bined.options.page;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.section.theme.SectionBackgroundPaintMode;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.options.gui.NamedProfilePanel;
import org.exbin.framework.bined.options.gui.ThemeProfilePanel;
import org.exbin.framework.bined.options.gui.ThemeProfilesOptionsPanel;
import org.exbin.framework.bined.options.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.options.gui.ThemeTemplatePanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;

/**
 * Theme profiles options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaThemeOptionsPage implements DefaultOptionsPage<CodeAreaThemeOptions> {

    public static final String PAGE_ID = "themeProfiles";

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
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<CodeAreaThemeOptions> createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ThemeProfilesOptionsPanel panel = new ThemeProfilesOptionsPanel();
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(ThemeProfilesOptionsPanel.class);
    }

    @Nonnull
    @Override
    public CodeAreaThemeOptions createOptions() {
        return new CodeAreaThemeOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, CodeAreaThemeOptions options) {
        new CodeAreaThemeOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, CodeAreaThemeOptions options) {
        options.copyTo(new CodeAreaThemeOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(CodeAreaThemeOptions options) {
        int selectedProfile = options.getSelectedProfile();
        if (selectedProfile >= 0) {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (!activeFile.isPresent()) {
                return;
            }

            SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            SectionCodeAreaThemeProfile profile = options.getThemeProfile(selectedProfile);
            codeArea.setThemeProfile(profile);
        }
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
