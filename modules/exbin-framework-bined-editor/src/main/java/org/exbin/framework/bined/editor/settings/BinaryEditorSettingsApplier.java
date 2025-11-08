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
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Binary editor settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "binaryEditor";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        if (!(instance instanceof BinaryDataComponent)) {
            return;
        }

        BinaryEditorOptions options = settingsOptionsProvider.getSettingsOptions(BinaryEditorOptions.class);
        // TODO: This causes multiple reloads / warnings about modified files
        // Move to BinaryFileProcessing
        // editorOptionsService.setFileHandlingMode(options.getFileHandlingMode());

        CodeAreaCore codeArea = ((BinaryDataComponent) instance).getCodeArea();
        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
        if (commandHandler instanceof CodeAreaOperationCommandHandler) {
            ((CodeAreaOperationCommandHandler) commandHandler).setEnterKeyHandlingMode(options.getEnterKeyHandlingMode());
            ((CodeAreaOperationCommandHandler) commandHandler).setTabKeyHandlingMode(options.getTabKeyHandlingMode());
        } else if (commandHandler instanceof DefaultCodeAreaCommandHandler) {
            ((DefaultCodeAreaCommandHandler) commandHandler).setEnterKeyHandlingMode(options.getEnterKeyHandlingMode());
            ((DefaultCodeAreaCommandHandler) commandHandler).setTabKeyHandlingMode(options.getTabKeyHandlingMode());
        }
    }
}
