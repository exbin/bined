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
package org.exbin.framework.bined.editor.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.editor.BinedEditorModule;
import org.exbin.framework.bined.editor.settings.gui.CodeAreaFileProcessingSettingsPanel;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;

/**
 * Code area file processing options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaFileProcessingSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "codeAreaFileProcessing";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        CodeAreaFileProcessingSettingsPanel panel = new CodeAreaFileProcessingSettingsPanel();
        BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
        ResourceBundle resourceBundle = binedEditorModule.getResourceBundle();
        List<String> fileHandlingModes = new ArrayList<>();
        fileHandlingModes.add(resourceBundle.getString("fileProcessingMode.memory"));
        fileHandlingModes.add(resourceBundle.getString("fileProcessingMode.delta"));
        panel.setFileProcessingModes(fileHandlingModes);
        return panel;
    }
}
