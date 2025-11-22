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
package org.exbin.framework.bined.inspector.settings;

import java.awt.Font;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.inspector.settings.gui.DataInspectorSettingsPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.text.font.gui.TextFontPanel;
import org.exbin.framework.text.font.settings.TextFontOptions;
import org.exbin.framework.text.font.settings.gui.TextFontSettingsPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Data inspector settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "dataInspector";

    private Font defaultFont;

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        DataInspectorSettingsPanel panel = new DataInspectorSettingsPanel();
        defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        panel.setDefaultFont(defaultFont);
        panel.setFontSettingsController(new TextFontSettingsPanel.Controller() {
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
                controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                    if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                        if (actionType == DefaultControlController.ControlActionType.OK) {
                            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                            TextFontOptions textFontOptions = new TextFontOptions(optionsModule.getAppOptions());
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

        return panel;
    }
}
