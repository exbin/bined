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
package org.exbin.framework.bined.viewer;

import java.io.File;
import java.net.URI;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.viewer.settings.BinaryAppearanceSettingsComponent;
import org.exbin.framework.bined.viewer.settings.CodeAreaSettingsComponent;
import org.exbin.framework.bined.viewer.settings.StatusSettingsComponent;
import org.exbin.framework.bined.viewer.settings.TextEncodingSettingsComponent;
import org.exbin.framework.bined.viewer.service.BinaryAppearanceService;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.text.font.settings.TextFontSettingsComponent;

/**
 * BinEd settings manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedSettingsManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedSettingsManager.class);

    private EditorProvider editorProvider;

    private TextEncodingSettingsComponent textEncodingOptionsPage;
    private TextFontSettingsComponent textFontOptionsPage;
    private BinaryAppearanceSettingsComponent binaryAppearanceOptionsPage;
    private CodeAreaSettingsComponent codeAreaOptionsPage;
    private StatusSettingsComponent statusBarOptionsPage;

    public BinedSettingsManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerSettings(EncodingsHandler encodingsHandler, BinEdFileManager fileManager, BinaryAppearanceService binaryAppearanceService) {
        // TODO: Drop parameters
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        /* OptionsGroup binaryGroup = settingsModule.createOptionsGroup("binaryEditor", resourceBundle);
        settingsManagement.registerGroup(binaryGroup);
        settingsManagement.registerGroupRule(binaryGroup, new ParentOptionsGroupRule("editor"));

        binaryAppearanceOptionsPage = new BinaryAppearanceSettingsComponent();
        binaryAppearanceOptionsPage.setBinaryAppearanceService(binaryAppearanceService);
        settingsManagement.registerPage(binaryAppearanceOptionsPage);
        settingsManagement.registerPageRule(binaryAppearanceOptionsPage, new GroupOptionsPageRule(binaryGroup));

        OptionsGroup binaryCodeAreaGroup = settingsModule.createOptionsGroup("binaryEditorCodeArea", resourceBundle);
        settingsManagement.registerGroup(binaryCodeAreaGroup);
        settingsManagement.registerGroupRule(binaryCodeAreaGroup, new ParentOptionsGroupRule(binaryGroup));
        codeAreaOptionsPage = new CodeAreaSettingsComponent();
        codeAreaOptionsPage.setEditorProvider(editorProvider);
        codeAreaOptionsPage.setResourceBundle(resourceBundle);
        settingsManagement.registerPage(codeAreaOptionsPage);
        settingsManagement.registerPageRule(codeAreaOptionsPage, new GroupOptionsPageRule(binaryCodeAreaGroup));

        OptionsGroup binaryCodeAreaEditingGroup = settingsModule.createOptionsGroup("binaryEditorEditing", resourceBundle);
        settingsManagement.registerGroup(binaryCodeAreaEditingGroup);
        settingsManagement.registerGroupRule(binaryCodeAreaEditingGroup, new ParentOptionsGroupRule(binaryGroup));

        OptionsGroup binaryEncodingGroup = settingsModule.createOptionsGroup("binaryEditorEncoding", resourceBundle);
        settingsManagement.registerGroup(binaryEncodingGroup);
        settingsManagement.registerGroupRule(binaryEncodingGroup, new ParentOptionsGroupRule(binaryGroup));
        textEncodingOptionsPage = new TextEncodingSettingsComponent();
        textEncodingOptionsPage.setEncodingsHandler(encodingsHandler);
        settingsManagement.registerPage(textEncodingOptionsPage);
        settingsManagement.registerPageRule(textEncodingOptionsPage, new GroupOptionsPageRule(binaryEncodingGroup));

        OptionsGroup binaryFontGroup = settingsModule.createOptionsGroup("binaryEditorFont", resourceBundle);
        settingsManagement.registerGroup(binaryFontGroup);
        settingsManagement.registerGroupRule(binaryFontGroup, new ParentOptionsGroupRule(binaryGroup));
        textFontOptionsPage = new TextFontOptionsPage();
        textFontOptionsPage.setTextFontService(new TextFontService() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    return ((BinEdFileHandler) fileHandler).getBinaryDataComponent().getCurrentFont();
                }

                return new JLabel().getFont();
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    return ((BinEdFileHandler) fileHandler).getBinaryDataComponent().getDefaultFont();
                }

                return new JLabel().getFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    ((BinEdFileHandler) fileHandler).getBinaryDataComponent().setCurrentFont(font);
                }
            }
        });
        settingsManagement.registerPage(textFontOptionsPage);
        settingsManagement.registerPageRule(textFontOptionsPage, new GroupOptionsPageRule(binaryFontGroup));

        OptionsGroup binaryStatusBarGroup = settingsModule.createOptionsGroup("binaryEditorStatusBar", resourceBundle);
        settingsManagement.registerGroup(binaryStatusBarGroup);
        settingsManagement.registerGroupRule(binaryStatusBarGroup, new ParentOptionsGroupRule(binaryGroup));
        statusBarOptionsPage = new StatusSettingsComponent();
        statusBarOptionsPage.setResourceBundle(resourceBundle);
        statusBarOptionsPage.setFileManager(fileManager);
        settingsManagement.registerPage(statusBarOptionsPage);
        settingsManagement.registerPageRule(statusBarOptionsPage, new GroupOptionsPageRule(binaryStatusBarGroup)); */
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }
}
