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
package org.exbin.framework.bined.theme;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.basic.color.CodeAreaColorsProfile;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.theme.settings.BinaryThemeOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaColorOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaColorProfileOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.theme.settings.CodeAreaColorSettingsComponent;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutProfileOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutSettingsComponent;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeProfileOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeSettingsComponent;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsPageContribution;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedThemeManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedThemeManager.class);
    
    public static final String PAGE_ID = "binaryEditorTheme";

    private EditorProvider editorProvider;

    private SectionCodeAreaLayoutProfile defaultLayoutProfile;
    private SectionCodeAreaThemeProfile defaultThemeProfile;
    private CodeAreaColorsProfile defaultColorProfile;

    private CodeAreaThemeSettingsComponent themeProfilesSettingsComponent;
    private CodeAreaLayoutSettingsComponent layoutProfilesSettingsComponent;
    private CodeAreaColorSettingsComponent colorProfilesSettingsComponent;

    public BinedThemeManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();
        
        settingsManagement.registerOptionsSettings(CodeAreaColorOptions.class, (optionsStorage) -> new CodeAreaColorOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(CodeAreaColorProfileOptions.class, (optionsStorage) -> new CodeAreaColorProfileOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(CodeAreaLayoutOptions.class, (optionsStorage) -> new CodeAreaLayoutOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(CodeAreaLayoutProfileOptions.class, (optionsStorage) -> new CodeAreaLayoutProfileOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(CodeAreaThemeOptions.class, (optionsStorage) -> new CodeAreaThemeOptions(optionsStorage));
        settingsManagement.registerOptionsSettings(CodeAreaThemeProfileOptions.class, (optionsStorage) -> new CodeAreaThemeProfileOptions(optionsStorage));
        
        SettingsPageContribution settingsPage = new SettingsPageContribution(PAGE_ID, resourceBundle);

        /* OptionsGroup binaryProfileGroup = settingsModule.createOptionsGroup("binaryEditorProfile", resourceBundle);
        settingsManagement.registerGroup(binaryProfileGroup);
        settingsManagement.registerGroupRule(binaryProfileGroup, new ParentOptionsGroupRule("binaryEditor"));

        OptionsGroup binaryThemeProfileGroup = settingsModule.createOptionsGroup("binaryEditorThemeProfile", resourceBundle);
        settingsManagement.registerGroup(binaryThemeProfileGroup);
        settingsManagement.registerGroupRule(binaryThemeProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        themeProfilesSettingsComponent = new CodeAreaThemeSettingsComponent();
        themeProfilesSettingsComponent.setResourceBundle(resourceBundle);
        themeProfilesSettingsComponent.setEditorProvider(editorProvider);
        settingsManagement.registerPage(themeProfilesSettingsComponent);
        settingsManagement.registerPageRule(themeProfilesSettingsComponent, new GroupOptionsPageRule(binaryThemeProfileGroup));

        OptionsGroup binaryLayoutProfileGroup = settingsModule.createOptionsGroup("binaryEditorLayoutProfile", resourceBundle);
        settingsManagement.registerGroup(binaryLayoutProfileGroup);
        settingsManagement.registerGroupRule(binaryLayoutProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        layoutProfilesSettingsComponent = new CodeAreaLayoutSettingsComponent();
        layoutProfilesSettingsComponent.setEditorProvider(editorProvider);
        settingsManagement.registerPage(layoutProfilesSettingsComponent);
        settingsManagement.registerPageRule(layoutProfilesSettingsComponent, new GroupOptionsPageRule(binaryLayoutProfileGroup));

        OptionsGroup binaryColorProfileGroup = settingsModule.createOptionsGroup("binaryEditorColorProfile", resourceBundle);
        settingsManagement.registerGroup(binaryColorProfileGroup);
        settingsManagement.registerGroupRule(binaryColorProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        colorProfilesSettingsComponent = new CodeAreaColorSettingsComponent();
        colorProfilesSettingsComponent.setEditorProvider(editorProvider);
        settingsManagement.registerPage(colorProfilesSettingsComponent);
        settingsManagement.registerPageRule(colorProfilesSettingsComponent, new GroupOptionsPageRule(binaryColorProfileGroup)); */
    }

    public void loadDefaults(SectCodeArea codeArea) {
        defaultLayoutProfile = codeArea.getLayoutProfile();
        defaultThemeProfile = codeArea.getThemeProfile();
        defaultColorProfile = codeArea.getColorsProfile();
    }

    public void applyProfileFromPreferences(SectCodeArea codeArea, BinaryThemeOptions preferences) {
        CodeAreaLayoutOptions layoutOptions = preferences.getLayoutOptions();
        int selectedLayoutProfile = layoutOptions.getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(layoutOptions.getLayoutProfile(selectedLayoutProfile));
        } else if (defaultLayoutProfile != null) {
            codeArea.setLayoutProfile(defaultLayoutProfile);
        }

        CodeAreaThemeOptions themeOptions = preferences.getThemeOptions();
        int selectedThemeProfile = themeOptions.getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(themeOptions.getThemeProfile(selectedThemeProfile));
        } else if (defaultThemeProfile != null) {
            codeArea.setThemeProfile(defaultThemeProfile);
        }

        CodeAreaColorOptions colorOptions = preferences.getColorOptions();
        int selectedColorProfile = colorOptions.getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(colorOptions.getColorsProfile(selectedColorProfile));
        } else if (defaultColorProfile != null) {
            codeArea.setColorsProfile(defaultColorProfile);
        }
    }
}
