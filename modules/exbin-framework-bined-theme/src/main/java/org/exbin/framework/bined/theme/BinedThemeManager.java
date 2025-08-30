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
import org.exbin.framework.bined.theme.options.BinaryThemeOptions;
import org.exbin.framework.bined.theme.options.CodeAreaColorOptions;
import org.exbin.framework.bined.theme.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.options.CodeAreaThemeOptions;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.theme.options.page.CodeAreaColorOptionsPage;
import org.exbin.framework.bined.theme.options.page.CodeAreaLayoutOptionsPage;
import org.exbin.framework.bined.theme.options.page.CodeAreaThemeOptionsPage;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;
import org.exbin.framework.options.api.VisualOptionsPageParams;
import org.exbin.framework.options.api.VisualOptionsPageRule;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedThemeManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedThemeManager.class);

    private EditorProvider editorProvider;

    private SectionCodeAreaLayoutProfile defaultLayoutProfile;
    private SectionCodeAreaThemeProfile defaultThemeProfile;
    private CodeAreaColorsProfile defaultColorProfile;

    private CodeAreaThemeOptionsPage themeProfilesOptionsPage;
    private CodeAreaLayoutOptionsPage layoutProfilesOptionsPage;
    private CodeAreaColorOptionsPage colorProfilesOptionsPage;

    public BinedThemeManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(BinedThemeModule.MODULE_ID);

        OptionsGroup binaryProfileGroup = optionsModule.createOptionsGroup("binaryEditorProfile", resourceBundle);
        optionsPageManagement.registerGroup(binaryProfileGroup);
        optionsPageManagement.registerGroupRule(binaryProfileGroup, new ParentOptionsGroupRule("binaryEditor"));

        OptionsGroup binaryThemeProfileGroup = optionsModule.createOptionsGroup("binaryEditorThemeProfile", resourceBundle);
        optionsPageManagement.registerGroup(binaryThemeProfileGroup);
        optionsPageManagement.registerGroupRule(binaryThemeProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        themeProfilesOptionsPage = new CodeAreaThemeOptionsPage();
        themeProfilesOptionsPage.setResourceBundle(resourceBundle);
        themeProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(themeProfilesOptionsPage);
        optionsPageManagement.registerPageRule(themeProfilesOptionsPage, new GroupOptionsPageRule(binaryThemeProfileGroup));
        optionsPageManagement.registerPageRule(themeProfilesOptionsPage, new VisualOptionsPageRule(new VisualOptionsPageParams(true)));

        OptionsGroup binaryLayoutProfileGroup = optionsModule.createOptionsGroup("binaryEditorLayoutProfile", resourceBundle);
        optionsPageManagement.registerGroup(binaryLayoutProfileGroup);
        optionsPageManagement.registerGroupRule(binaryLayoutProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        layoutProfilesOptionsPage = new CodeAreaLayoutOptionsPage();
        layoutProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(layoutProfilesOptionsPage);
        optionsPageManagement.registerPageRule(layoutProfilesOptionsPage, new GroupOptionsPageRule(binaryLayoutProfileGroup));
        optionsPageManagement.registerPageRule(layoutProfilesOptionsPage, new VisualOptionsPageRule(new VisualOptionsPageParams(true)));

        OptionsGroup binaryColorProfileGroup = optionsModule.createOptionsGroup("binaryEditorColorProfile", resourceBundle);
        optionsPageManagement.registerGroup(binaryColorProfileGroup);
        optionsPageManagement.registerGroupRule(binaryColorProfileGroup, new ParentOptionsGroupRule(binaryProfileGroup));
        colorProfilesOptionsPage = new CodeAreaColorOptionsPage();
        colorProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(colorProfilesOptionsPage);
        optionsPageManagement.registerPageRule(colorProfilesOptionsPage, new GroupOptionsPageRule(binaryColorProfileGroup));
        optionsPageManagement.registerPageRule(colorProfilesOptionsPage, new VisualOptionsPageRule(new VisualOptionsPageParams(true)));
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
