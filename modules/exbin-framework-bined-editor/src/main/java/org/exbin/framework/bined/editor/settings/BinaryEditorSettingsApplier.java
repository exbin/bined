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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.editor.service.EditorOptionsService;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsProvider;

/**
 * Binary editor settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryEditor";

    @Override
    public void applySettings(Object instance, SettingsProvider settingsProvider) {
        EditorOptionsService editorOptionsService = null;
        BinaryEditorOptions options = settingsProvider.getSettings(BinaryEditorOptions.class);
        // TODO: This causes multiple reloads / warnings about modified files
        // editorOptionsService.setFileHandlingMode(options.getFileHandlingMode());
        editorOptionsService.setEnterKeyHandlingMode(options.getEnterKeyHandlingMode());
        editorOptionsService.setTabKeyHandlingMode(options.getTabKeyHandlingMode());
    }
}
