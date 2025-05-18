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
package org.exbin.framework.bined.editor;

import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JLabel;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.basic.TabKeyHandlingMode;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.editor.options.page.CodeAreaOptionsPage;
import org.exbin.framework.bined.editor.options.page.CodeAreaEditingOptionsPage;
import org.exbin.framework.bined.editor.service.BinaryAppearanceService;
import org.exbin.framework.bined.editor.service.EditorOptionsService;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.options.api.GroupOptionsPageRule;
import org.exbin.framework.options.api.OptionsGroup;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.ParentOptionsGroupRule;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.font.options.TextFontOptionsPage;
import org.exbin.framework.text.font.service.TextFontService;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOptionsManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOptionsManager.class);

    private EditorProvider editorProvider;

    private TextFontOptionsPage textFontOptionsPage;
    private CodeAreaEditingOptionsPage codeAreaEditingOptionsPage;
    private CodeAreaOptionsPage codeAreaOptionsPage;

    public BinedOptionsManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerOptionsPanels(EncodingsHandler encodingsHandler, BinEdFileManager fileManager, BinaryAppearanceService binaryAppearanceService) {
        // TODO: Drop parameters
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(BinedEditorModule.MODULE_ID);

        OptionsGroup binaryGroup = optionsModule.createOptionsGroup("binaryEditor", resourceBundle);
        optionsPageManagement.registerGroup(binaryGroup);
        optionsPageManagement.registerGroupRule(binaryGroup, new ParentOptionsGroupRule("editor"));

        OptionsGroup binaryCodeAreaGroup = optionsModule.createOptionsGroup("binaryEditorCodeArea", resourceBundle);
        optionsPageManagement.registerGroup(binaryCodeAreaGroup);
        optionsPageManagement.registerGroupRule(binaryCodeAreaGroup, new ParentOptionsGroupRule(binaryGroup));
        codeAreaOptionsPage = new CodeAreaOptionsPage();
        codeAreaOptionsPage.setEditorProvider(editorProvider);
        codeAreaOptionsPage.setResourceBundle(resourceBundle);
        optionsPageManagement.registerPage(codeAreaOptionsPage);
        optionsPageManagement.registerPageRule(codeAreaOptionsPage, new GroupOptionsPageRule(binaryCodeAreaGroup));

        OptionsGroup binaryCodeAreaEditingGroup = optionsModule.createOptionsGroup("binaryEditorEditing", resourceBundle);
        optionsPageManagement.registerGroup(binaryCodeAreaEditingGroup);
        optionsPageManagement.registerGroupRule(binaryCodeAreaEditingGroup, new ParentOptionsGroupRule(binaryGroup));
        codeAreaEditingOptionsPage = new CodeAreaEditingOptionsPage();
        codeAreaEditingOptionsPage.setEditorOptionsService(new EditorOptionsService() {
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
        codeAreaEditingOptionsPage.setResourceBundle(resourceBundle);
        optionsPageManagement.registerPage(codeAreaEditingOptionsPage);
        optionsPageManagement.registerPageRule(codeAreaEditingOptionsPage, new GroupOptionsPageRule(binaryGroup));

        OptionsGroup binaryFontGroup = optionsModule.createOptionsGroup("binaryEditorFont", resourceBundle);
        optionsPageManagement.registerGroup(binaryFontGroup);
        optionsPageManagement.registerGroupRule(binaryFontGroup, new ParentOptionsGroupRule(binaryGroup));
        textFontOptionsPage = new TextFontOptionsPage();
        textFontOptionsPage.setTextFontService(new TextFontService() {
            @Nonnull
            @Override
            public Font getCurrentFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    return ((BinEdFileHandler) fileHandler).getTextFontHandler().getCurrentFont();
                }

                return new JLabel().getFont();
            }

            @Nonnull
            @Override
            public Font getDefaultFont() {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    return ((BinEdFileHandler) fileHandler).getTextFontHandler().getDefaultFont();
                }

                return new JLabel().getFont();
            }

            @Override
            public void setCurrentFont(Font font) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                FileHandler fileHandler = activeFile.orElse(null);
                if (fileHandler instanceof BinEdFileHandler) {
                    ((BinEdFileHandler) fileHandler).getTextFontHandler().setCurrentFont(font);
                }
            }
        });
        optionsPageManagement.registerPage(textFontOptionsPage);
        optionsPageManagement.registerPageRule(textFontOptionsPage, new GroupOptionsPageRule(binaryFontGroup));
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }
}
