/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.theme;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.color.SectionCodeAreaColorProfile;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.jaguif.App;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorProfileOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorSettingsApplier;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorSettingsComponent;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutProfileOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutSettingsApplier;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutSettingsComponent;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeProfileOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeSettingsApplier;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeSettingsComponent;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;

/**
 * Binary data theme module.
 */
@ParametersAreNonnullByDefault
public class BinedThemeModule implements Module {

    public static final String MODULE_ID = ModuleUtils.getModuleIdByApi(BinedThemeModule.class);

    private java.util.ResourceBundle resourceBundle = null;

    public static final String SETTINGS_GROUP_ID = "binaryEditorThemeGroup";
    public static final String SETTINGS_THEME_PAGE_ID = "binaryEditorTheme";
    public static final String SETTINGS_LAYOUT_PAGE_ID = "binaryEditorLayout";
    public static final String SETTINGS_COLOR_PAGE_ID = "binaryEditorColor";

    private boolean defaultProfileLoaded = false;
    private SectionCodeAreaLayoutProfile defaultLayoutProfile;
    private SectionCodeAreaThemeProfile defaultThemeProfile;
    private SectionCodeAreaColorProfile defaultColorProfile;

    public BinedThemeModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedThemeModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    public void registerSettings() {
        getResourceBundle();
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(CodeAreaColorOptions.class, (optionsStorage) -> new CodeAreaColorOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaColorProfileOptions.class, (optionsStorage) -> new CodeAreaColorProfileOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaLayoutOptions.class, (optionsStorage) -> new CodeAreaLayoutOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaLayoutProfileOptions.class, (optionsStorage) -> new CodeAreaLayoutProfileOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaThemeOptions.class, (optionsStorage) -> new CodeAreaThemeOptions(optionsStorage));
        settingsManagement.registerSettingsOptions(CodeAreaThemeProfileOptions.class, (optionsStorage) -> new CodeAreaThemeProfileOptions(optionsStorage));

        settingsManagement.registerApplySetting(CodeAreaColorOptions.class, new ApplySettingsContribution(CodeAreaColorSettingsApplier.APPLIER_ID, new CodeAreaColorSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(CodeAreaColorSettingsApplier.APPLIER_ID, new CodeAreaColorSettingsApplier()));
        settingsManagement.registerApplySetting(CodeAreaLayoutOptions.class, new ApplySettingsContribution(CodeAreaLayoutSettingsApplier.APPLIER_ID, new CodeAreaLayoutSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(CodeAreaLayoutSettingsApplier.APPLIER_ID, new CodeAreaLayoutSettingsApplier()));
        settingsManagement.registerApplySetting(CodeAreaThemeOptions.class, new ApplySettingsContribution(CodeAreaThemeSettingsApplier.APPLIER_ID, new CodeAreaThemeSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextComponent.class, new ApplySettingsContribution(CodeAreaThemeSettingsApplier.APPLIER_ID, new CodeAreaThemeSettingsApplier()));

        GroupSequenceContribution settingsGroup = settingsManagement.registerGroup(SETTINGS_GROUP_ID);
        settingsManagement.registerSettingsRule(settingsGroup, new SettingsPageContributionRule("binary"));

        SettingsPageContribution settingsPage = new SettingsPageContribution(SETTINGS_THEME_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(settingsGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("binary"));
        SettingsComponentContribution registerComponent = settingsManagement.registerComponent(CodeAreaThemeSettingsComponent.COMPONENT_ID, new CodeAreaThemeSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage, SettingsPageContributionRule.Parameter.EXPAND_VERTICALLY));

        settingsPage = new SettingsPageContribution(SETTINGS_LAYOUT_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(settingsGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("binary"));
        registerComponent = settingsManagement.registerComponent(CodeAreaLayoutSettingsComponent.COMPONENT_ID, new CodeAreaLayoutSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage, SettingsPageContributionRule.Parameter.EXPAND_VERTICALLY));

        settingsPage = new SettingsPageContribution(SETTINGS_COLOR_PAGE_ID, resourceBundle);
        settingsManagement.registerPage(settingsPage);
        settingsManagement.registerSettingsRule(settingsPage, new GroupSequenceContributionRule(settingsGroup));
        settingsManagement.registerSettingsRule(settingsPage, new SettingsPageContributionRule("binary"));
        registerComponent = settingsManagement.registerComponent(CodeAreaColorSettingsComponent.COMPONENT_ID, new CodeAreaColorSettingsComponent());
        settingsManagement.registerSettingsRule(registerComponent, new SettingsPageContributionRule(settingsPage, SettingsPageContributionRule.Parameter.EXPAND_VERTICALLY));
    }

    public void loadDefaultProfiles() {
        // Try to optimize later
        SectCodeArea codeArea = new SectCodeArea();
        defaultLayoutProfile = codeArea.getLayoutProfile();
        defaultThemeProfile = codeArea.getThemeProfile();
        defaultColorProfile = (SectionCodeAreaColorProfile) codeArea.getColorsProfile();
        defaultProfileLoaded = true;
    }

    @Nonnull
    public SectionCodeAreaLayoutProfile getDefaultLayoutProfile() {
        if (!defaultProfileLoaded) {
            loadDefaultProfiles();
        }

        return defaultLayoutProfile;
    }

    @Nonnull
    public SectionCodeAreaThemeProfile getDefaultThemeProfile() {
        if (!defaultProfileLoaded) {
            loadDefaultProfiles();
        }

        return defaultThemeProfile;
    }

    @Nonnull
    public SectionCodeAreaColorProfile getDefaultColorProfile() {
        if (!defaultProfileLoaded) {
            loadDefaultProfiles();
        }

        return defaultColorProfile;
    }
}
