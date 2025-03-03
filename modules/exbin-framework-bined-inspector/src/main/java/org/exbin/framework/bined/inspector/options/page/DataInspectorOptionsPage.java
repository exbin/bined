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
package org.exbin.framework.bined.inspector.options.page;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.BinEdComponentInspector;
import org.exbin.framework.bined.inspector.options.DataInspectorOptions;
import org.exbin.framework.bined.inspector.options.gui.DataInspectorOptionsPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.text.font.gui.TextFontPanel;
import org.exbin.framework.text.font.options.TextFontOptions;
import org.exbin.framework.text.font.options.gui.TextFontOptionsPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;

/**
 * Data inspector options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorOptionsPage implements DefaultOptionsPage<DataInspectorOptions> {

    public static final String PAGE_ID = "dataInspector";

    private EditorProvider editorProvider;
    private Font defaultFont;

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<DataInspectorOptions> createComponent() {
        DataInspectorOptionsPanel panel = new DataInspectorOptionsPanel();
        defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        panel.setDefaultFont(defaultFont);
        panel.setFontChangeAction(new TextFontOptionsPanel.FontChangeAction() {
            @Override
            public Font changeFont(Font currentFont) {
                final Result result = new Result();
                WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
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
                            TextFontOptions textFontOptions = new TextFontOptions(preferencesModule.getAppPreferences());
                            textFontOptions.setUseDefaultFont(true);
                            textFontOptions.setFont(fontPanel.getStoredFont());
                        }
                        result.font = fontPanel.getStoredFont();
                    }

                    dialog.close();
                    dialog.dispose();
                });
                dialog.showCentered(frameModule.getFrame());

                return result.font;
            }

            class Result {

                Font font;
            }
        });

        Font currentFont = defaultFont;
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (activeFile.isPresent()) {
            FileHandler fileHandler = activeFile.get();
            if (fileHandler instanceof BinEdFileHandler) {
                BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
                BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
                currentFont = componentExtension.getInputFieldsFont();
            }
        }

        panel.setCurrentFont(currentFont);

        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(DataInspectorOptionsPanel.class);
    }

    @Nonnull
    @Override
    public DataInspectorOptions createOptions() {
        return new DataInspectorOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, DataInspectorOptions options) {
        new DataInspectorOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, DataInspectorOptions options) {
        options.copyTo(new DataInspectorOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(DataInspectorOptions options) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            return;
        }
        FileHandler fileHandler = activeFile.get();
        if (!(fileHandler instanceof BinEdFileHandler)) {
            return;
        }

        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
        BinEdComponentInspector componentExtension = component.getComponentExtension(BinEdComponentInspector.class);
        componentExtension.setShowParsingPanel(options.isShowParsingPanel());
        boolean useDefaultFont = options.isUseDefaultFont();
        Map<TextAttribute, ?> fontAttributes = options.getFontAttributes();
        componentExtension.setInputFieldsFont(useDefaultFont || fontAttributes == null ? defaultFont : new Font(fontAttributes));
    }
}
