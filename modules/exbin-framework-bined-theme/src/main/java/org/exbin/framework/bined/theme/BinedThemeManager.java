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
import org.exbin.framework.bined.theme.settings.CodeAreaColorSettingsApplier;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.theme.settings.CodeAreaColorSettingsComponent;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutProfileOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutSettingsApplier;
import org.exbin.framework.bined.theme.settings.CodeAreaLayoutSettingsComponent;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeProfileOptions;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeSettingsApplier;
import org.exbin.framework.bined.theme.settings.CodeAreaThemeSettingsComponent;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedThemeManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedThemeManager.class);
    
    public static final String SETTINGS_GROUP_ID = "binaryEditorThemeGroup";
    public static final String SETTINGS_THEME_PAGE_ID = "binaryEditorTheme";
    public static final String SETTINGS_LAYOUT_PAGE_ID = "binaryEditorLayout";
    public static final String SETTINGS_COLOR_PAGE_ID = "binaryEditorColor";

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
        
        settingsManagement.registerApplySetting(Object.class, new ApplySettingsContribution(CodeAreaColorSettingsApplier.APPLIER_ID, new CodeAreaColorSettingsApplier()));
        settingsManagement.registerApplySetting(Object.class, new ApplySettingsContribution(CodeAreaLayoutSettingsApplier.APPLIER_ID, new CodeAreaLayoutSettingsApplier()));
        settingsManagement.registerApplySetting(Object.class, new ApplySettingsContribution(CodeAreaThemeSettingsApplier.APPLIER_ID, new CodeAreaThemeSettingsApplier()));

        GroupSequenceContribution registerGroup = settingsManagement.registerGroup(SETTINGS_GROUP_ID);
        settingsManagement.registerSettingsRule(registerGroup, new SettingsPageContributionRule("editorBinary"));

        SettingsPageContribution settingsPage = new SettingsPageContribution(SETTINGS_THEME_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(registerGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("editorBinary"));
        SettingsComponentContribution registerComponent = settingsManagement.registerComponent(CodeAreaThemeSettingsComponent.COMPONENT_ID, new CodeAreaThemeSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));

        settingsPage = new SettingsPageContribution(SETTINGS_LAYOUT_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(registerGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("editorBinary"));
        registerComponent = settingsManagement.registerComponent(CodeAreaLayoutSettingsComponent.COMPONENT_ID, new CodeAreaLayoutSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));
        
        settingsPage = new SettingsPageContribution(SETTINGS_COLOR_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(registerGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("editorBinary"));
        registerComponent = settingsManagement.registerComponent(CodeAreaColorSettingsComponent.COMPONENT_ID, new CodeAreaColorSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage));
        
        
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
