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
package org.exbin.framework.bined.options.page;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.gui.AddEncodingPanel;
import org.exbin.framework.editor.text.options.TextEncodingOptions;
import org.exbin.framework.editor.text.options.gui.TextEncodingOptionsPanel;
import org.exbin.framework.editor.text.options.gui.TextEncodingPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.handler.DefaultControlHandler;

/**
 * Text encoding options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingOptionsPage implements DefaultOptionsPage<TextEncodingOptions> {

    public static final String PAGE_ID = "textEncoding";

    private TextEncodingOptionsPanel panel;
    private EncodingsHandler encodingsHandler;

    public void setEncodingsHandler(EncodingsHandler encodingsHandler) {
        this.encodingsHandler = encodingsHandler;
    }

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<TextEncodingOptions> createPanel() {
        if (panel == null) {
            panel = new TextEncodingOptionsPanel();
            panel.setTextEncodingService(encodingsHandler.getTextEncodingService());
            panel.setAddEncodingsOperation((List<String> usedEncodings, TextEncodingPanel.EncodingsUpdate encodingsUpdate) -> {
                WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                final AddEncodingPanel addEncodingPanel = new AddEncodingPanel();
                addEncodingPanel.setUsedEncodings(usedEncodings);
                DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
                final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, controlPanel);
                controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                    if (actionType == DefaultControlHandler.ControlActionType.OK) {
                        encodingsUpdate.update(addEncodingPanel.getEncodings());
                    }

                    addEncodingDialog.close();
                    addEncodingDialog.dispose();
                });
                windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingPanel.getResourceBundle());
                windowModule.setWindowTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
                addEncodingDialog.showCentered(panel);
            });
        }

        return panel;
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(TextEncodingOptionsPanel.class);
    }

    @Nonnull
    @Override
    public TextEncodingOptions createOptions() {
        return new TextEncodingOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, TextEncodingOptions options) {
        new TextEncodingOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, TextEncodingOptions options) {
        options.copyTo(new TextEncodingOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(TextEncodingOptions options) {
        encodingsHandler.setSelectedEncoding(options.getSelectedEncoding());
        encodingsHandler.setEncodings(options.getEncodings());
    }
}
