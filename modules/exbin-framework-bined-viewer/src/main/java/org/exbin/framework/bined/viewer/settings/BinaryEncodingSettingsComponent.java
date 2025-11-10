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
package org.exbin.framework.bined.viewer.settings;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.text.encoding.gui.TextEncodingListPanel;
import org.exbin.framework.text.encoding.gui.TextEncodingPanel;
import org.exbin.framework.text.encoding.settings.gui.TextEncodingSettingsPanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Text encoding settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEncodingSettingsComponent implements SettingsComponentProvider {

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        TextEncodingSettingsPanel panel = new TextEncodingSettingsPanel();
        panel.setListPanelController((List<String> usedEncodings, TextEncodingListPanel.EncodingsUpdate encodingsUpdate) -> {
            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            final TextEncodingPanel addEncodingPanel = new TextEncodingPanel();
            addEncodingPanel.setUsedEncodings(usedEncodings);
            DefaultControlPanel controlPanel = new DefaultControlPanel(addEncodingPanel.getResourceBundle());
            final WindowHandler addEncodingDialog = windowModule.createDialog(addEncodingPanel, controlPanel);
            controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
                if (actionType == DefaultControlController.ControlActionType.OK) {
                    encodingsUpdate.update(addEncodingPanel.getEncodings());
                }

                addEncodingDialog.close();
                addEncodingDialog.dispose();
            });
            windowModule.addHeaderPanel(addEncodingDialog.getWindow(), addEncodingPanel.getClass(), addEncodingPanel.getResourceBundle());
            windowModule.setWindowTitle(addEncodingDialog, addEncodingPanel.getResourceBundle());
            addEncodingDialog.showCentered(panel);
        });

        return panel;
    }
}
