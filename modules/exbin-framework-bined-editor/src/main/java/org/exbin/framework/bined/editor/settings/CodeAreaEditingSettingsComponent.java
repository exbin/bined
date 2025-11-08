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
import org.exbin.framework.bined.editor.settings.gui.CodeAreaEditingSettingsPanel;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;

/**
 * Code area editing options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaEditingSettingsComponent implements SettingsComponentProvider {

    public static final String COMPONENT_ID = "codeAreaEditing";

    @Nonnull
    @Override
    public SettingsComponent createComponent() {
        CodeAreaEditingSettingsPanel panel = new CodeAreaEditingSettingsPanel();
        BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
        ResourceBundle resourceBundle = binedEditorModule.getResourceBundle();
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
        return panel;
    }
}
