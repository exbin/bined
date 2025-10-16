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

import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsProvider;

/**
 * Code area viewer settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaViewerSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "codeAreaViewer";

    @Override
    public void applySettings(Object instance, SettingsProvider settingsProvider) {
        CodeAreaOptions options = settingsProvider.getSettings(CodeAreaOptions.class);
//        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
//        if (!activeFile.isPresent()) {
//            return;
//        }
//
//        SectCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
//        CodeAreaOptions.applyToCodeArea(options, codeArea);
//        // TODO App.getModule(ActionModuleApi.class).updateActionsForComponent(ActiveComponent.class, codeArea);
    }
}
