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
package org.exbin.framework.bined.editor.options.page;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.bined.editor.options.EditorOptions;
import org.exbin.framework.bined.editor.options.gui.CodeAreaEditingOptionsPanel;
import org.exbin.framework.bined.editor.service.EditorOptionsService;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Code area editing options page.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaEditingOptionsPage implements DefaultOptionsPage<EditorOptions> {

    public static final String PAGE_ID = "codeAreaEditing";

    private EditorOptionsService editorOptionsService;
    private java.util.ResourceBundle resourceBundle;

    public void setEditorOptionsService(EditorOptionsService editorOptionsService) {
        this.editorOptionsService = editorOptionsService;
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
    public OptionsComponent<EditorOptions> createComponent() {
        CodeAreaEditingOptionsPanel panel = new CodeAreaEditingOptionsPanel();
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

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return App.getModule(LanguageModuleApi.class).getBundle(CodeAreaEditingOptionsPanel.class);
    }

    @Nonnull
    @Override
    public EditorOptions createOptions() {
        return new EditorOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, EditorOptions options) {
        new EditorOptions(preferences).copyTo(options);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, EditorOptions options) {
        options.copyTo(new EditorOptions(preferences));
    }

    @Override
    public void applyPreferencesChanges(EditorOptions options) {
        // TODO: This causes multiple reloads / warnings about modified files
        // editorOptionsService.setFileHandlingMode(options.getFileHandlingMode());
        editorOptionsService.setEnterKeyHandlingMode(options.getEnterKeyHandlingMode());
        editorOptionsService.setTabKeyHandlingMode(options.getTabKeyHandlingMode());
    }
}
