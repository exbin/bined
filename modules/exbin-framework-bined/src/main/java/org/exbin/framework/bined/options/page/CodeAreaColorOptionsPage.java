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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.options.CodeAreaColorOptions;
import org.exbin.framework.bined.options.gui.ColorProfilePanel;
import org.exbin.framework.bined.options.gui.ColorProfilesOptionsPanel;
import org.exbin.framework.bined.options.gui.ColorProfilesPanel;
import org.exbin.framework.bined.options.gui.ColorTemplatePanel;
import org.exbin.framework.bined.options.gui.NamedProfilePanel;
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
 * Color profiles options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaColorOptionsPage implements DefaultOptionsPage<CodeAreaColorOptions> {

    public static final String PAGE_ID = "colorProfiles";

    private EditorProvider editorProvider;

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
    public OptionsComponent<CodeAreaColorOptions> createComponent() {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ColorProfilesOptionsPanel panel = new ColorProfilesOptionsPanel();
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
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

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(ColorProfilesOptionsPanel.class);
    }

    @Nonnull
    @Override
    public CodeAreaColorOptions createOptions() {
        return new CodeAreaColorOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, CodeAreaColorOptions options) {
        new CodeAreaColorOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, CodeAreaColorOptions options) {
        options.copyTo(new CodeAreaColorOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(CodeAreaColorOptions options) {
        int selectedProfile = options.getSelectedProfile();
        if (selectedProfile >= 0) {
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (!activeFile.isPresent()) {
                return;
            }

            SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
            SectionCodeAreaColorProfile profile = options.getColorsProfile(selectedProfile);
            codeArea.setColorsProfile(profile);
        }
    }
}
