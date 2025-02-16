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
package org.exbin.framework.bined;

import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JLabel;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.basic.TabKeyHandlingMode;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.action.CodeTypeActions;
import org.exbin.framework.bined.action.HexCharactersCaseActions;
import org.exbin.framework.bined.action.PositionCodeTypeActions;
import org.exbin.framework.bined.action.ShowNonprintablesActions;
import org.exbin.framework.bined.action.ViewModeHandlerActions;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.options.page.BinaryAppearanceOptionsPage;
import org.exbin.framework.bined.options.page.CodeAreaColorOptionsPage;
import org.exbin.framework.bined.options.page.CodeAreaLayoutOptionsPage;
import org.exbin.framework.bined.options.page.CodeAreaOptionsPage;
import org.exbin.framework.bined.options.page.CodeAreaThemeOptionsPage;
import org.exbin.framework.bined.options.page.EditorOptionsPage;
import org.exbin.framework.bined.options.page.StatusOptionsPage;
import org.exbin.framework.bined.options.page.TextEncodingOptionsPage;
import org.exbin.framework.bined.service.BinaryAppearanceService;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.bined.service.EditorOptionsService;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.options.TextFontOptionsPage;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOptionsManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOptionsManager.class);

    private EditorProvider editorProvider;

    private TextEncodingOptionsPage textEncodingOptionsPage;
    private TextFontOptionsPage textFontOptionsPage;
    private BinaryAppearanceOptionsPage binaryAppearanceOptionsPage;
    private EditorOptionsPage editorOptionsPage;
    private CodeAreaOptionsPage codeAreaOptionsPage;
    private StatusOptionsPage statusOptionsPage;
    private CodeAreaThemeOptionsPage themeProfilesOptionsPage;
    private CodeAreaLayoutOptionsPage layoutProfilesOptionsPage;
    private CodeAreaColorOptionsPage colorProfilesOptionsPage;

    public BinedOptionsManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerOptionsPanels(EncodingsHandler encodingsHandler, BinEdFileManager fileManager, BinaryAppearanceService binaryAppearanceService, CodeTypeActions codeTypeActions, ShowNonprintablesActions showNonprintablesActions, HexCharactersCaseActions hexCharactersCaseActions, PositionCodeTypeActions positionCodeTypeActions, ViewModeHandlerActions viewModeActions) {
        // TODO: Drop parameters
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(BinedModule.MODULE_ID);

        OptionsGroup binaryAppearanceOptionsGroup = optionsModule.createOptionsGroup("binaryAppearance", resourceBundle);
        optionsPageManagement.registerGroup(binaryAppearanceOptionsGroup);
        optionsPageManagement.registerGroupRule(binaryAppearanceOptionsGroup, new ParentOptionsGroupRule("editor"));

        binaryAppearanceOptionsPage = new BinaryAppearanceOptionsPage();
        binaryAppearanceOptionsPage.setBinaryAppearanceService(binaryAppearanceService);
        optionsPageManagement.registerPage(binaryAppearanceOptionsPage);
        optionsPageManagement.registerPageRule(binaryAppearanceOptionsPage, new GroupOptionsPageRule(binaryAppearanceOptionsGroup));

        textEncodingOptionsPage = new TextEncodingOptionsPage();
        textEncodingOptionsPage.setEncodingsHandler(encodingsHandler);
        optionsPageManagement.registerPage(textEncodingOptionsPage);

        textFontOptionsPage = new TextFontOptionsPage();
        textFontOptionsPage.setTextFontService(new TextFontService() {
            @Override
            public Font getCurrentFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    return ((BinEdFileHandler) activeFile.get()).getCurrentFont();
                }

                return new JLabel().getFont();
            }

            @Override
            public Font getDefaultFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    return ((BinEdFileHandler) activeFile.get()).getDefaultFont();
                }

                return new JLabel().getFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    ((BinEdFileHandler) activeFile.get()).setCurrentFont(font);
                }
            }
        });
        optionsPageManagement.registerPage(textFontOptionsPage);

        editorOptionsPage = new EditorOptionsPage();
        editorOptionsPage.setEditorOptionsService(new EditorOptionsService() {
            @Override
            public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                    if (!fileHandler.isModified() || editorProvider.releaseFile(fileHandler)) {
                        fileHandler.switchFileHandlingMode(fileHandlingMode);
                        ((BinEdEditorProvider) editorProvider).updateStatus();
                    }
                }
            }

            @Override
            public void setEnterKeyHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(enterKeyHandlingMode);
                }
            }

            @Override
            public void setTabKeyHandlingMode(TabKeyHandlingMode tabKeyHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    ((CodeAreaOperationCommandHandler) commandHandler).setTabKeyHandlingMode(tabKeyHandlingMode);
                }
            }
        });
        editorOptionsPage.setResourceBundle(resourceBundle);
        optionsPageManagement.registerPage(editorOptionsPage);

        codeAreaOptionsPage = new CodeAreaOptionsPage();
        codeAreaOptionsPage.setEditorProvider(editorProvider);
        codeAreaOptionsPage.setResourceBundle(resourceBundle);
        optionsPageManagement.registerPage(codeAreaOptionsPage);

        statusOptionsPage = new StatusOptionsPage();
        statusOptionsPage.setResourceBundle(resourceBundle);
        statusOptionsPage.setFileManager(fileManager);
        optionsPageManagement.registerPage(statusOptionsPage);

        themeProfilesOptionsPage = new CodeAreaThemeOptionsPage();
        themeProfilesOptionsPage.setResourceBundle(resourceBundle);
        themeProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(themeProfilesOptionsPage);

        layoutProfilesOptionsPage = new CodeAreaLayoutOptionsPage();
        layoutProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(layoutProfilesOptionsPage);

        colorProfilesOptionsPage = new CodeAreaColorOptionsPage();
        colorProfilesOptionsPage.setEditorProvider(editorProvider);
        optionsPageManagement.registerPage(colorProfilesOptionsPage);
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }
}
