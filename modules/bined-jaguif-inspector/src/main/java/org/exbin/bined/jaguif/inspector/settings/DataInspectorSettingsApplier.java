/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.inspector.settings;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.jaguif.component.BinaryFileDocument;
import org.exbin.bined.jaguif.inspector.BasicValuesInspector;
import org.exbin.bined.jaguif.inspector.BinEdInspectorComponentExtension;
import org.exbin.bined.jaguif.inspector.gui.BasicValuesPanel;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.context.api.ContextStateProvider;

/**
 * Data inspector settings component.
 */
@ParametersAreNonnullByDefault
public class DataInspectorSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "dataInspector";

    @Override
    public void applySettings(ContextStateProvider contextProvider, SettingsOptionsProvider settingsProvider) {
        ContextDocument instance = contextProvider.getActiveState(ContextDocument.class);
        if (!(instance instanceof BinaryFileDocument)) {
            return;
        }

        DataInspectorOptions options = settingsProvider.getSettingsOptions(DataInspectorOptions.class);
        DataInspectorFontOptions fontOptions = settingsProvider.getSettingsOptions(DataInspectorFontOptions.class);

        Font defaultFont = null;
        BinaryFileDocument binaryDocument = (BinaryFileDocument) instance;
        BinEdInspectorComponentExtension componentExtension = binaryDocument.getComponentExtension(BinEdInspectorComponentExtension.class);
        componentExtension.setShowParsingPanel(options.isShowParsingPanel());
        boolean useDefaultFont = fontOptions.isUseDefaultFont();
        Map<TextAttribute, ?> fontAttributes = fontOptions.getFontAttributes();
        BasicValuesInspector basicValuesInspector = DataInspectorSettingsApplier.getBinEdInspector(binaryDocument);
        if (basicValuesInspector != null) {
            ((BasicValuesPanel) basicValuesInspector.getComponent()).setInputFieldsFont(useDefaultFont || fontAttributes == null ? defaultFont : new Font(fontAttributes));
        }
    }

    @Nullable
    private static BasicValuesInspector getBinEdInspector(BinaryFileDocument binaryDocument) {
        BinEdInspectorComponentExtension extension = binaryDocument.getComponentExtension(BinEdInspectorComponentExtension.class);
        return extension.getInspector(BasicValuesInspector.class);
    }
}
