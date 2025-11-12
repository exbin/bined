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
package org.exbin.framework.bined.theme.settings.gui;

import org.exbin.framework.bined.theme.gui.ProfileSelectionPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.theme.model.LayoutThemeProfile;
import org.exbin.framework.bined.theme.model.ThemeProfile;
import org.exbin.framework.bined.theme.model.ThemeProfilesListModel;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutProfileOptions;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.options.settings.api.VerticallyExpandable;

/**
 * Layout profiles settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LayoutProfilesSettingsPanel extends javax.swing.JPanel implements SettingsComponent, VerticallyExpandable {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(LayoutProfilesSettingsPanel.class);

    private final ProfileSelectionPanel selectionPanel;
    private final ThemeProfilesPanel profilesPanel;

    public LayoutProfilesSettingsPanel() {
        this.profilesPanel = new ThemeProfilesPanel();
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

    @Nonnull
    public List<ThemeProfile> getProfiles() {
        return profilesPanel.getProfilesListModel().getProfiles();
    }

    public void setThemeProfileController(ThemeProfilesPanel.Controller controller) {
        profilesPanel.setController(controller);
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        CodeAreaLayoutOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaLayoutOptions.class);
        CodeAreaLayoutProfileOptions layoutProfileOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaLayoutProfileOptions.class);
        layoutProfileOptions.loadFromOptions(options);

        List<ThemeProfile> profiles = new ArrayList<>();
        List<String> profileNames = layoutProfileOptions.getProfileNames();
        for (int index = 0; index < profileNames.size(); index++) {
            LayoutThemeProfile profile = new LayoutThemeProfile(
                    profileNames.get(index),
                    layoutProfileOptions.getLayoutProfile(index)
            );
            profiles.add(profile);
        }

        ThemeProfilesListModel model = profilesPanel.getProfilesListModel();
        model.setProfiles(profiles);

        selectionPanel.setDefaultProfile(options.getSelectedProfile());
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        CodeAreaLayoutOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaLayoutOptions.class);
        CodeAreaLayoutProfileOptions layoutProfileOptions = settingsOptionsProvider.getSettingsOptions(CodeAreaLayoutProfileOptions.class);

        layoutProfileOptions.clearProfiles();
        ThemeProfilesListModel model = profilesPanel.getProfilesListModel();
        List<ThemeProfile> profiles = model.getProfiles();
        for (int index = 0; index < profiles.size(); index++) {
            ThemeProfile profile = profiles.get(index);
            layoutProfileOptions.addProfile(profile.getProfileName(), ((LayoutThemeProfile) profile).getLayoutProfile());
        }

        layoutProfileOptions.saveToOptions(options);
        layoutProfileOptions.setSelectedProfile(selectionPanel.getDefaultProfile());
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {

    }
}
