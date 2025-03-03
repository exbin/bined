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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.gui.CodeAreaOptionsPanel;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Code area options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaOptionsPage implements DefaultOptionsPage<CodeAreaOptions> {

    public static final String PAGE_ID = "codeArea";

    private EditorProvider editorProvider;
    private java.util.ResourceBundle resourceBundle;

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    
    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<CodeAreaOptions> createComponent() {
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
    public CodeAreaOptions createOptions() {
        return new CodeAreaOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, CodeAreaOptions options) {
        new CodeAreaOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, CodeAreaOptions options) {
        options.copyTo(new CodeAreaOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(CodeAreaOptions options) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            return;
        }

        SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
        CodeAreaOptions.applyToCodeArea(options, codeArea);
        // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(CodeAreaCore.class, codeArea);
    }
}
