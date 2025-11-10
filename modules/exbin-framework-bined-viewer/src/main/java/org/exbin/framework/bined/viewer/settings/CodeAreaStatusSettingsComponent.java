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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.bined.viewer.settings.gui.StatusSettingsPanel;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * Status options settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaStatusSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "status";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        StatusSettingsPanel panel = new StatusSettingsPanel();
        BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
        ResourceBundle resourceBundle = binedViewerModule.getResourceBundle();

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

        return panel;
    }
}
