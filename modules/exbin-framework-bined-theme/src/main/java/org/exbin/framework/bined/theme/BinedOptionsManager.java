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

import java.io.File;
import java.net.URI;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.theme.options.page.CodeAreaColorOptionsPage;
import org.exbin.framework.bined.theme.options.page.CodeAreaLayoutOptionsPage;
import org.exbin.framework.bined.theme.options.page.CodeAreaThemeOptionsPage;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;
import org.exbin.framework.options.api.VisualOptionsPageParams;
import org.exbin.framework.options.api.VisualOptionsPageRule;
import org.exbin.framework.text.encoding.EncodingsHandler;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOptionsManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOptionsManager.class);

    private EditorProvider editorProvider;

    private CodeAreaThemeOptionsPage themeProfilesOptionsPage;
    private CodeAreaLayoutOptionsPage layoutProfilesOptionsPage;
    private CodeAreaColorOptionsPage colorProfilesOptionsPage;

    public BinedOptionsManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerOptionsPanels(EncodingsHandler encodingsHandler) {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(BinedThemeModule.MODULE_ID);

        OptionsGroup binaryGroup = optionsModule.createOptionsGroup("binaryEditor", resourceBundle);
        optionsPageManagement.registerGroup(binaryGroup);
        optionsPageManagement.registerGroupRule(binaryGroup, new ParentOptionsGroupRule("editor"));

        OptionsGroup binaryProfileGroup = optionsModule.createOptionsGroup("binaryEditorProfile", resourceBundle);
        optionsPageManagement.registerGroup(binaryProfileGroup);
        optionsPageManagement.registerGroupRule(binaryProfileGroup, new ParentOptionsGroupRule(binaryGroup));

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

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }
}
