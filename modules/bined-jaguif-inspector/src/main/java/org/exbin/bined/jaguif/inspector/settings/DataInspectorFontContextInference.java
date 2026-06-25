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
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.inspector.BasicValuesInspector;
import org.exbin.bined.jaguif.inspector.BinEdInspectorComponentExtension;
import org.exbin.bined.jaguif.inspector.gui.BasicValuesPanel;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.context.api.ContextStateProvider;

/**
 * Text editor font context inference.
 */
@NullMarked
public class DataInspectorFontContextInference implements DataInspectorFontInference {

    protected ContextStateProvider contextProvider;

    public DataInspectorFontContextInference(ContextStateProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public ContextStateProvider getContextProvider() {
        return contextProvider;
    }

    @Override
    public Optional<Font> getInputFieldsFont() {
        ContextDocument contextDocument = contextProvider.getActiveState(ContextDocument.class);
        if (contextDocument instanceof BinaryFileDocument) {
            BasicValuesInspector basicValuesInspector = DataInspectorFontContextInference.getBinEdInspector((BinaryFileDocument) contextDocument);
            if (basicValuesInspector != null) {
                return Optional.of(((BasicValuesPanel) basicValuesInspector.getComponent()).getInputFieldsFont());
            }
        }

        return Optional.empty();
    }

    @Nullable
    private static BasicValuesInspector getBinEdInspector(BinaryFileDocument binaryDocument) {
        BinEdInspectorComponentExtension extension = binaryDocument.getComponentExtension(BinEdInspectorComponentExtension.class);
        return extension.getInspector(BasicValuesInspector.class);
    }
}
