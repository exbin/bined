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
import org.exbin.framework.bined.viewer.settings.gui.CodeAreaSettingsPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * Code area settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaSettingsComponent implements SettingsComponentProvider<CodeAreaOptions> {

    public static final String COMPONENT_ID = "codeArea";

    private EditorProvider editorProvider;

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    @Nonnull
    @Override
    public SettingsComponent<CodeAreaOptions> createComponent() {
        BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
        ResourceBundle resourceBundle = binedViewerModule.getResourceBundle();
        CodeAreaSettingsPanel panel = new CodeAreaSettingsPanel();
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
}
