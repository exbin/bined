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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.BinaryFileDocument;
import org.exbin.framework.bined.inspector.BasicValuesInspector;
import org.exbin.framework.bined.inspector.BinEdInspectorComponentExtension;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.document.api.ContextDocument;

/**
 * Text editor font context inference.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataInspectorFontContextInference implements DataInspectorFontInference {

    protected ActiveContextProvider contextProvider;

    public DataInspectorFontContextInference(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Nonnull
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
