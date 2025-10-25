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
package org.exbin.framework.bined.inspector.settings;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.inspector.BasicValuesInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorComponentExtension;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Data inspector settings component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "dataInspector";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        Font defaultFont = null;

        DataInspectorOptions options = settingsOptionsProvider.getSettingsOptions(DataInspectorOptions.class);
        Optional<FileHandler> activeFile = null; // TODO editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            return;
        }
        FileHandler fileHandler = activeFile.get();
        if (!(fileHandler instanceof BinEdFileHandler)) {
            return;
        }

        BinEdComponentPanel component = ((BinEdFileHandler) fileHandler).getComponent();
        BinEdInspectorComponentExtension componentExtension = component.getComponentExtension(BinEdInspectorComponentExtension.class);
        componentExtension.setShowParsingPanel(options.isShowParsingPanel());
        boolean useDefaultFont = options.isUseDefaultFont();
        Map<TextAttribute, ?> fontAttributes = options.getFontAttributes();
        BasicValuesInspector basicValuesInspector = DataInspectorSettingsApplier.getBinEdInspector(component);
        if (basicValuesInspector != null) {
            ((BasicValuesPanel) basicValuesInspector.getComponent()).setInputFieldsFont(useDefaultFont || fontAttributes == null ? defaultFont : new Font(fontAttributes));
        }
    }

    @Nullable
    private static BasicValuesInspector getBinEdInspector(BinEdComponentPanel component) {
        BinEdInspectorComponentExtension extension = component.getBinEdComponentExtensions(BinEdInspectorComponentExtension.class).orElse(null);
        return extension != null ? extension.getInspector(BasicValuesInspector.class) : null;
    }
}
