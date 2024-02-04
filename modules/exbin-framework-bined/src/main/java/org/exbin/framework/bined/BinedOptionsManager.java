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

import java.awt.Dialog;
import java.awt.Font;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.basic.TabKeyHandlingMode;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.extended.theme.ExtendedBackgroundPaintMode;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.bined.swing.extended.layout.DefaultExtendedCodeAreaLayoutProfile;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.bined.action.CodeTypeActions;
import org.exbin.framework.bined.action.HexCharactersCaseActions;
import org.exbin.framework.bined.action.PositionCodeTypeActions;
import org.exbin.framework.bined.action.ShowUnprintablesActions;
import org.exbin.framework.bined.action.ViewModeHandlerActions;
import org.exbin.framework.bined.options.impl.BinaryAppearanceOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaColorOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaLayoutOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaOptionsImpl;
import org.exbin.framework.bined.options.impl.CodeAreaThemeOptionsImpl;
import org.exbin.framework.bined.options.impl.EditorOptionsImpl;
import org.exbin.framework.bined.options.impl.StatusOptionsImpl;
import org.exbin.framework.bined.options.gui.BinaryAppearanceOptionsPanel;
import org.exbin.framework.editor.text.gui.AddEncodingPanel;
import org.exbin.framework.editor.text.options.gui.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextFontOptionsPanel;
import org.exbin.framework.editor.text.gui.TextFontPanel;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.handler.DefaultControlHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.bined.options.gui.CodeAreaOptionsPanel;
import org.exbin.framework.bined.options.gui.ColorProfilePanel;
import org.exbin.framework.bined.options.gui.ColorProfilesOptionsPanel;
import org.exbin.framework.bined.options.gui.ColorProfilesPanel;
import org.exbin.framework.bined.options.gui.ColorTemplatePanel;
import org.exbin.framework.bined.options.gui.EditorOptionsPanel;
import org.exbin.framework.bined.options.gui.LayoutProfilePanel;
import org.exbin.framework.bined.options.gui.LayoutProfilesOptionsPanel;
import org.exbin.framework.bined.options.gui.LayoutProfilesPanel;
import org.exbin.framework.bined.options.gui.LayoutTemplatePanel;
import org.exbin.framework.bined.options.gui.NamedProfilePanel;
import org.exbin.framework.bined.options.gui.StatusOptionsPanel;
import org.exbin.framework.bined.options.gui.ThemeProfilePanel;
import org.exbin.framework.bined.options.gui.ThemeProfilesOptionsPanel;
import org.exbin.framework.bined.options.gui.ThemeProfilesPanel;
import org.exbin.framework.bined.options.gui.ThemeTemplatePanel;
import org.exbin.framework.bined.preferences.BinaryAppearancePreferences;
import org.exbin.framework.bined.preferences.CodeAreaColorPreferences;
import org.exbin.framework.bined.preferences.CodeAreaLayoutPreferences;
import org.exbin.framework.bined.preferences.CodeAreaPreferences;
import org.exbin.framework.bined.preferences.CodeAreaThemePreferences;
import org.exbin.framework.bined.preferences.EditorPreferences;
import org.exbin.framework.bined.preferences.StatusPreferences;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.bined.service.BinaryAppearanceService;
import org.exbin.framework.editor.text.options.impl.TextEncodingOptionsImpl;
import org.exbin.framework.editor.text.options.impl.TextFontOptionsImpl;
import org.exbin.framework.editor.text.service.TextFontService;
import org.exbin.framework.bined.service.EditorOptionsService;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowHandler;

/**
 * BinEd options manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinedOptionsManager {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinedOptionsManager.class);

    private EditorProvider editorProvider;

    private DefaultOptionsPage<TextEncodingOptionsImpl> textEncodingOptionsPage;
    private DefaultOptionsPage<TextFontOptionsImpl> textFontOptionsPage;
    private DefaultOptionsPage<BinaryAppearanceOptionsImpl> binaryAppearanceOptionsPage;
    private DefaultOptionsPage<EditorOptionsImpl> editorOptionsPage;
    private DefaultOptionsPage<CodeAreaOptionsImpl> codeAreaOptionsPage;
    private DefaultOptionsPage<StatusOptionsImpl> statusOptionsPage;
    private DefaultOptionsPage<CodeAreaThemeOptionsImpl> themeProfilesOptionsPage;
    private DefaultOptionsPage<CodeAreaLayoutOptionsImpl> layoutProfilesOptionsPage;
    private DefaultOptionsPage<CodeAreaColorOptionsImpl> colorProfilesOptionsPage;

    public BinedOptionsManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void registerOptionsPanels(EncodingsHandler encodingsHandler, BinEdFileManager fileManager, BinaryAppearanceService binaryAppearanceService, CodeTypeActions codeTypeActions, ShowUnprintablesActions showUnprintablesActions, HexCharactersCaseActions hexCharactersCaseActions, PositionCodeTypeActions positionCodeTypeActions, ViewModeHandlerActions viewModeActions) {
        // TODO: Drop parameters
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);

        binaryAppearanceOptionsPage = new DefaultOptionsPage<BinaryAppearanceOptionsImpl>() {

            private BinaryAppearanceOptionsPanel panel;

            @Nonnull
            @Override
            public OptionsComponent<BinaryAppearanceOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new BinaryAppearanceOptionsPanel();
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(BinaryAppearanceOptionsPanel.class);
            }

            @Nonnull
            @Override
            public BinaryAppearanceOptionsImpl createOptions() {
                return new BinaryAppearanceOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, BinaryAppearanceOptionsImpl options) {
                options.loadFromPreferences(new BinaryAppearancePreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, BinaryAppearanceOptionsImpl options) {
                options.saveToPreferences(new BinaryAppearancePreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(BinaryAppearanceOptionsImpl options) {
                binaryAppearanceService.setWordWrapMode(options.isLineWrapping());
            }
        };
        optionsModule.extendAppearanceOptionsPage(binaryAppearanceOptionsPage);

        textEncodingOptionsPage = new DefaultOptionsPage<TextEncodingOptionsImpl>() {
            private TextEncodingOptionsPanel panel;

            @Override
            public OptionsComponent<TextEncodingOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new TextEncodingOptionsPanel();
                    panel.setTextEncodingService(encodingsHandler.getTextEncodingService());
                    panel.setAddEncodingsOperation((List<String> usedEncodings) -> {
                        final List<String> result = new ArrayList<>();
                        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                        final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                        addEncodingPanel.setUsedEncodings(usedEncodings);
                        DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                        final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, controlPanel);
                        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                result.addAll(addEncodingPanel.getEncodings());
                            }

                            addEncodingDialog.close();
                            addEncodingDialog.dispose();
                        });
                        windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingPanel.getResourceBundle());
                        windowModule.setWindowTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
                        addEncodingDialog.showCentered(panel);
                        return result;
                    });
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(TextEncodingOptionsPanel.class);
            }

            @Override
            public TextEncodingOptionsImpl createOptions() {
                return new TextEncodingOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextEncodingOptionsImpl options) {
                options.loadFromPreferences(new TextEncodingPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextEncodingOptionsImpl options) {
                options.saveToPreferences(new TextEncodingPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextEncodingOptionsImpl options) {
                encodingsHandler.setSelectedEncoding(options.getSelectedEncoding());
                encodingsHandler.setEncodings(options.getEncodings());
            }
        };
        optionsModule.addOptionsPage(textEncodingOptionsPage);

        TextFontService textFontService = new TextFontService() {
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
        };
        textFontOptionsPage = new DefaultOptionsPage<TextFontOptionsImpl>() {
            private TextFontOptionsPanel panel;

            @Override
            public OptionsComponent<TextFontOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new TextFontOptionsPanel();
                    panel.setTextFontService(textFontService);
                    panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
                        @Override
                        public Font changeFont(Font currentFont) {
                            final FontResult result = new FontResult();
                            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                            final TextFontPanel fontPanel = new TextFontPanel();
                            fontPanel.setStoredFont(currentFont);
                            DefaultControlPanel controlPanel = new DefaultControlPanel();
                            final WindowHandler dialog = windowModule.createDialog(fontPanel, controlPanel);
                            windowModule.addHeaderPanel(dialog.getWindow(), fontPanel.getClass(), fontPanel.getResourceBundle());
                            windowModule.setWindowTitle(dialog, fontPanel.getResourceBundle());
                            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                                if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                                        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                                        TextFontPreferences parameters = new TextFontPreferences(preferencesModule.getAppPreferences());
                                        parameters.setUseDefaultFont(false);
                                        parameters.setFont(fontPanel.getStoredFont());
                                    }
                                    result.font = fontPanel.getStoredFont();
                                }

                                dialog.close();
                                dialog.dispose();
                            });
                            dialog.showCentered(panel);

                            return result.font;
                        }

                        class FontResult {

                            Font font;
                        }
                    });
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(TextFontOptionsPanel.class);
            }

            @Nonnull
            @Override
            public TextFontOptionsImpl createOptions() {
                return new TextFontOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, TextFontOptionsImpl options) {
                options.loadFromPreferences(new TextFontPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, TextFontOptionsImpl options) {
                options.saveToPreferences(new TextFontPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(TextFontOptionsImpl options) {
                textFontService.setCurrentFont(options.isUseDefaultFont() ? textFontService.getDefaultFont() : options.getFont(textFontService.getDefaultFont()));

                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    ((FontCapable) codeArea).setCodeFont(options.isUseDefaultFont() ? CodeAreaPreferences.DEFAULT_FONT : options.getFont(CodeAreaPreferences.DEFAULT_FONT));
                }
            }
        };
        optionsModule.addOptionsPage(textFontOptionsPage);

        EditorOptionsService editorOptionsService = new EditorOptionsService() {
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
                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(enterKeyHandlingMode);
                }
            }

            @Override
            public void setTabKeyHandlingMode(TabKeyHandlingMode tabKeyHandlingMode) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (activeFile.isPresent()) {
                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
                    ((CodeAreaOperationCommandHandler) commandHandler).setTabKeyHandlingMode(tabKeyHandlingMode);
                }
            }
        };
        editorOptionsPage = new DefaultOptionsPage<EditorOptionsImpl>() {
            private EditorOptionsPanel panel;

            @Nonnull
            @Override
            public OptionsComponent<EditorOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new EditorOptionsPanel();
                    List<String> fileHandlingModes = new ArrayList<>();
                    fileHandlingModes.add(resourceBundle.getString("fileHandlingMode.memory"));
                    fileHandlingModes.add(resourceBundle.getString("fileHandlingMode.delta"));
                    panel.setFileHandlingModes(fileHandlingModes);
                    List<String> enderKeyHandlingModes = new ArrayList<>();
                    enderKeyHandlingModes.add(resourceBundle.getString("enterKeyHandlingMode.platformSpecific"));
                    enderKeyHandlingModes.add(resourceBundle.getString("enterKeyHandlingMode.cr"));
                    enderKeyHandlingModes.add(resourceBundle.getString("enterKeyHandlingMode.lf"));
                    enderKeyHandlingModes.add(resourceBundle.getString("enterKeyHandlingMode.crlf"));
                    enderKeyHandlingModes.add(resourceBundle.getString("enterKeyHandlingMode.ignore"));
                    panel.setEnterKeyHandlingModes(enderKeyHandlingModes);
                    List<String> tabKeyHandlingModes = new ArrayList<>();
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.platformSpecific"));
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.insertTab"));
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.insertSpaces"));
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.cycleToNextSection"));
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.cycleToPreviousSection"));
                    tabKeyHandlingModes.add(resourceBundle.getString("tabKeyHandlingMode.ignore"));
                    panel.setTabKeyHandlingModes(tabKeyHandlingModes);
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(EditorOptionsPanel.class);
            }

            @Nonnull
            @Override
            public EditorOptionsImpl createOptions() {
                return new EditorOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, EditorOptionsImpl options) {
                options.loadFromPreferences(new EditorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, EditorOptionsImpl options) {
                options.saveToPreferences(new EditorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(EditorOptionsImpl options) {
                // TODO: This causes multiple reloads / warnings about modified files
                // editorOptionsService.setFileHandlingMode(options.getFileHandlingMode());
                editorOptionsService.setEnterKeyHandlingMode(options.getEnterKeyHandlingMode());
                editorOptionsService.setTabKeyHandlingMode(options.getTabKeyHandlingMode());
            }
        };
        optionsModule.addOptionsPage(editorOptionsPage);

        codeAreaOptionsPage = new DefaultOptionsPage<CodeAreaOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsComponent<CodeAreaOptionsImpl> createPanel() {
                CodeAreaOptionsPanel panel = new CodeAreaOptionsPanel();
                List<String> viewModes = new ArrayList<>();
                viewModes.add(resourceBundle.getString("codeAreaViewMode.dual"));
                viewModes.add(resourceBundle.getString("codeAreaViewMode.codeMatrix"));
                viewModes.add(resourceBundle.getString("codeAreaViewMode.textPreview"));
                panel.setViewModes(viewModes);

                List<String> codeTypes = new ArrayList<>();
                codeTypes.add(resourceBundle.getString("codeAreaCodeType.binary"));
                codeTypes.add(resourceBundle.getString("codeAreaCodeType.octal"));
                codeTypes.add(resourceBundle.getString("codeAreaCodeType.decimal"));
                codeTypes.add(resourceBundle.getString("codeAreaCodeType.hexadecimal"));
                panel.setCodeTypes(codeTypes);

                List<String> positionCodeTypes = new ArrayList<>();
                positionCodeTypes.add(resourceBundle.getString("positionCodeAreaCodeType.octal"));
                positionCodeTypes.add(resourceBundle.getString("positionCodeAreaCodeType.decimal"));
                positionCodeTypes.add(resourceBundle.getString("positionCodeAreaCodeType.hexadecimal"));
                panel.setPositionCodeTypes(positionCodeTypes);

                List<String> charactersCases = new ArrayList<>();
                charactersCases.add(resourceBundle.getString("codeAreaCharactersCase.lower"));
                charactersCases.add(resourceBundle.getString("codeAreaCharactersCase.higher"));
                panel.setCharactersCases(charactersCases);

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(CodeAreaOptionsPanel.class);
            }

            @Nonnull
            @Override
            public CodeAreaOptionsImpl createOptions() {
                return new CodeAreaOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, CodeAreaOptionsImpl options) {
                options.loadFromPreferences(new CodeAreaPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, CodeAreaOptionsImpl options) {
                options.saveToPreferences(new CodeAreaPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(CodeAreaOptionsImpl options) {
                Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                if (!activeFile.isPresent()) {
                    return;
                }

                ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                CodeAreaOptionsImpl.applyToCodeArea(options, codeArea);
                // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(CodeAreaCore.class, codeArea);
            }
        };
        optionsModule.addOptionsPage(codeAreaOptionsPage);

        statusOptionsPage = new DefaultOptionsPage<StatusOptionsImpl>() {
            private StatusOptionsPanel panel;

            @Nonnull
            @Override
            public OptionsComponent<StatusOptionsImpl> createPanel() {
                if (panel == null) {
                    panel = new StatusOptionsPanel();

                    List<String> cursorPositionCodeTypes = new ArrayList<>();
                    cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.octal"));
                    cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.decimal"));
                    cursorPositionCodeTypes.add(resourceBundle.getString("cursorPositionCodeType.hexadecimal"));
                    panel.setCursorPositionCodeTypes(cursorPositionCodeTypes);

                    List<String> documentSizeCodeTypes = new ArrayList<>();
                    documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.octal"));
                    documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.decimal"));
                    documentSizeCodeTypes.add(resourceBundle.getString("documentSizeCodeType.hexadecimal"));
                    panel.setDocumentSizeCodeTypes(documentSizeCodeTypes);
                }

                return panel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(StatusOptionsPanel.class);
            }

            @Nonnull
            @Override
            public StatusOptionsImpl createOptions() {
                return new StatusOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, StatusOptionsImpl options) {
                options.loadFromPreferences(new StatusPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, StatusOptionsImpl options) {
                options.saveToPreferences(new StatusPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(StatusOptionsImpl options) {
                fileManager.applyPreferencesChanges(options);
            }
        };
        optionsModule.addOptionsPage(statusOptionsPage);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        themeProfilesOptionsPage = new DefaultOptionsPage<CodeAreaThemeOptionsImpl>() {

            @Nonnull
            @Override
            public OptionsComponent<CodeAreaThemeOptionsImpl> createPanel() {
                ThemeProfilesOptionsPanel panel = new ThemeProfilesOptionsPanel();
                panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
                    ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
                    themeProfilePanel.setThemeProfile(new ExtendedCodeAreaThemeProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
                    namedProfilePanel.setProfileName(profileName);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ThemeProfileResult result = new ThemeProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), themeProfilePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new ThemeProfilesPanel.ThemeProfile(
                                    namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setEditProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord) -> {
                    ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ThemeProfileResult result = new ThemeProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Edit Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), themeProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName());
                    themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new ThemeProfilesPanel.ThemeProfile(
                                    namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setCopyProfileOperation((JComponent parentComponent, ThemeProfilesPanel.ThemeProfile profileRecord) -> {
                    ThemeProfilePanel themeProfilePanel = createThemeProfilePanel();
                    themeProfilePanel.setThemeProfile(new ExtendedCodeAreaThemeProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeProfilePanel);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ThemeProfileResult result = new ThemeProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Copy Theme Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), themeProfilePanel.getClass(), themeProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName() + " #copy");
                    themeProfilePanel.setThemeProfile(profileRecord.getThemeProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new ThemeProfilesPanel.ThemeProfile(
                                    namedProfilePanel.getProfileName(), themeProfilePanel.getThemeProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setTemplateProfileOperation((JComponent parentComponent) -> {
                    ThemeTemplatePanel themeTemplatePanel = new ThemeTemplatePanel();
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(themeTemplatePanel);
                    namedProfilePanel.setProfileName("");
                    themeTemplatePanel.addListSelectionListener((e) -> {
                        ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                        namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
                    });
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ThemeProfileResult result = new ThemeProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Theme Template", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), themeTemplatePanel.getClass(), themeTemplatePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            ThemeTemplatePanel.ThemeProfile selectedTemplate = themeTemplatePanel.getSelectedTemplate();
                            if (selectedTemplate == null) {
                                JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new ThemeProfilesPanel.ThemeProfile(
                                    namedProfilePanel.getProfileName(), selectedTemplate.getThemeProfile()
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

            class ThemeProfileResult {

                ThemeProfilesPanel.ThemeProfile profile;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(ThemeProfilesOptionsPanel.class);
            }

            @Nonnull
            @Override
            public CodeAreaThemeOptionsImpl createOptions() {
                return new CodeAreaThemeOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, CodeAreaThemeOptionsImpl options) {
                options.loadFromPreferences(new CodeAreaThemePreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, CodeAreaThemeOptionsImpl options) {
                options.saveToPreferences(new CodeAreaThemePreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(CodeAreaThemeOptionsImpl options) {
                int selectedProfile = options.getSelectedProfile();
                if (selectedProfile >= 0) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        return;
                    }

                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    ExtendedCodeAreaThemeProfile profile = options.getThemeProfile(selectedProfile);
                    codeArea.setThemeProfile(profile);
                }
            }
        };
        optionsModule.addOptionsPage(themeProfilesOptionsPage);

        layoutProfilesOptionsPage = new DefaultOptionsPage<CodeAreaLayoutOptionsImpl>() {
            @Override
            public OptionsComponent<CodeAreaLayoutOptionsImpl> createPanel() {
                LayoutProfilesOptionsPanel panel = new LayoutProfilesOptionsPanel();
                panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
                    LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
                    layoutProfilePanel.setLayoutProfile(new DefaultExtendedCodeAreaLayoutProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
                    namedProfilePanel.setProfileName(profileName);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    LayoutProfileResult result = new LayoutProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), layoutProfilePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new LayoutProfilesPanel.LayoutProfile(
                                    namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setEditProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord) -> {
                    LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    LayoutProfileResult result = new LayoutProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Edit Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), layoutProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName());
                    layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new LayoutProfilesPanel.LayoutProfile(
                                    namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setCopyProfileOperation((JComponent parentComponent, LayoutProfilesPanel.LayoutProfile profileRecord) -> {
                    LayoutProfilePanel layoutProfilePanel = new LayoutProfilePanel();
                    layoutProfilePanel.setLayoutProfile(new DefaultExtendedCodeAreaLayoutProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutProfilePanel);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    LayoutProfileResult result = new LayoutProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Copy Layout Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), layoutProfilePanel.getClass(), layoutProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName() + " #copy");
                    layoutProfilePanel.setLayoutProfile(profileRecord.getLayoutProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new LayoutProfilesPanel.LayoutProfile(
                                    namedProfilePanel.getProfileName(), layoutProfilePanel.getLayoutProfile()
                            );
                        }

                        dialog.close();
                        dialog.dispose();
                    });
                    dialog.showCentered(parentComponent);

                    return result.profile;
                });
                panel.setTemplateProfileOperation((JComponent parentComponent) -> {
                    LayoutTemplatePanel layoutTemplatePanel = new LayoutTemplatePanel();
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(layoutTemplatePanel);
                    namedProfilePanel.setProfileName("");
                    layoutTemplatePanel.addListSelectionListener((e) -> {
                        LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                        namedProfilePanel.setProfileName(selectedTemplate != null ? selectedTemplate.getProfileName() : "");
                    });
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    LayoutProfileResult result = new LayoutProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Layout Template", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), layoutTemplatePanel.getClass(), layoutTemplatePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            LayoutTemplatePanel.LayoutProfile selectedTemplate = layoutTemplatePanel.getSelectedTemplate();
                            if (selectedTemplate == null) {
                                JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            result.profile = new LayoutProfilesPanel.LayoutProfile(
                                    namedProfilePanel.getProfileName(), selectedTemplate.getLayoutProfile()
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

            class LayoutProfileResult {

                LayoutProfilesPanel.LayoutProfile profile;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(LayoutProfilesOptionsPanel.class);
            }

            @Nonnull
            @Override
            public CodeAreaLayoutOptionsImpl createOptions() {
                return new CodeAreaLayoutOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, CodeAreaLayoutOptionsImpl options) {
                options.loadFromPreferences(new CodeAreaLayoutPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, CodeAreaLayoutOptionsImpl options) {
                options.saveToPreferences(new CodeAreaLayoutPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(CodeAreaLayoutOptionsImpl options) {
                int selectedProfile = options.getSelectedProfile();
                if (selectedProfile >= 0) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        return;
                    }

                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    ExtendedCodeAreaLayoutProfile profile = options.getLayoutProfile(selectedProfile);
                    codeArea.setLayoutProfile(profile);
                }
            }
        };
        optionsModule.addOptionsPage(layoutProfilesOptionsPage);

        colorProfilesOptionsPage = new DefaultOptionsPage<CodeAreaColorOptionsImpl>() {
            @Override
            public OptionsComponent<CodeAreaColorOptionsImpl> createPanel() {
                ColorProfilesOptionsPanel panel = new ColorProfilesOptionsPanel();
                panel.setAddProfileOperation((JComponent parentComponent, String profileName) -> {
                    ColorProfilePanel colorProfilePanel = new ColorProfilePanel();
                    colorProfilePanel.setColorProfile(new ExtendedCodeAreaColorProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
                    namedProfilePanel.setProfileName(profileName);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ColorProfileResult result = new ColorProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), colorProfilePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
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

                    ColorProfileResult result = new ColorProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Edit Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), colorProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName());
                    colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
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
                    colorProfilePanel.setColorProfile(new ExtendedCodeAreaColorProfile());
                    NamedProfilePanel namedProfilePanel = new NamedProfilePanel(colorProfilePanel);
                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    JPanel dialogPanel = windowModule.createDialogPanel(namedProfilePanel, controlPanel);

                    ColorProfileResult result = new ColorProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Copy Colors Profile", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), colorProfilePanel.getClass(), colorProfilePanel.getResourceBundle());
                    namedProfilePanel.setProfileName(profileRecord.getProfileName() + " #copy");
                    colorProfilePanel.setColorProfile(profileRecord.getColorProfile());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Edit Error", JOptionPane.ERROR_MESSAGE);
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

                    ColorProfileResult result = new ColorProfileResult();
                    final WindowHandler dialog = windowModule.createWindow(dialogPanel, parentComponent, "Add Colors Template", Dialog.ModalityType.APPLICATION_MODAL);
                    windowModule.addHeaderPanel(dialog.getWindow(), colorTemplatePanel.getClass(), colorTemplatePanel.getResourceBundle());
                    controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType != DefaultControlHandler.ControlActionType.CANCEL) {
                            if (!isValidProfileName(namedProfilePanel.getProfileName())) {
                                JOptionPane.showMessageDialog(parentComponent, "Invalid profile name", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            ColorTemplatePanel.ColorProfile selectedTemplate = colorTemplatePanel.getSelectedTemplate();
                            if (selectedTemplate == null) {
                                JOptionPane.showMessageDialog(parentComponent, "No template selected", "Profile Template Error", JOptionPane.ERROR_MESSAGE);
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
            public CodeAreaColorOptionsImpl createOptions() {
                return new CodeAreaColorOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, CodeAreaColorOptionsImpl options) {
                options.loadFromPreferences(new CodeAreaColorPreferences(preferences));
            }

            @Override
            public void saveToPreferences(Preferences preferences, CodeAreaColorOptionsImpl options) {
                options.saveToPreferences(new CodeAreaColorPreferences(preferences));
            }

            @Override
            public void applyPreferencesChanges(CodeAreaColorOptionsImpl options) {
                int selectedProfile = options.getSelectedProfile();
                if (selectedProfile >= 0) {
                    Optional<FileHandler> activeFile = editorProvider.getActiveFile();
                    if (!activeFile.isPresent()) {
                        return;
                    }

                    ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
                    ExtendedCodeAreaColorProfile profile = options.getColorsProfile(selectedProfile);
                    codeArea.setColorsProfile(profile);
                }
            }
        };
        optionsModule.addOptionsPage(colorProfilesOptionsPage);
    }

    public void startWithFile(String filePath) {
        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        URI uri = new File(filePath).toURI();
        fileModule.loadFromFile(uri);
    }

    @Nonnull
    private ThemeProfilePanel createThemeProfilePanel() {
        ThemeProfilePanel themeProfilePanel = new ThemeProfilePanel();
        List<String> backgroundModes = new ArrayList<>();
        for (ExtendedBackgroundPaintMode mode : ExtendedBackgroundPaintMode.values()) {
            backgroundModes.add(resourceBundle.getString("backgroundPaintMode." + mode.name().toLowerCase()));
        }
        themeProfilePanel.setBackgroundModes(backgroundModes);
        return themeProfilePanel;
    }
}
