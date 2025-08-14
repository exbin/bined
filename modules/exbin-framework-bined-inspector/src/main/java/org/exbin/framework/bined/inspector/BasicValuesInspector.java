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
package org.exbin.framework.bined.inspector;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.bined.inspector.options.DataInspectorOptions;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * BinEd basic values inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicValuesInspector implements BinEdInspector {

    private BasicValuesPanel component;

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = new BasicValuesPanel();
        }
        return component;
    }

    @Override
    public void setCodeArea(CodeAreaCore codeArea, BinaryDataUndoRedo undoRedo) {
        component.setCodeArea(codeArea, undoRedo);
    }

    @Override
    public void activateSync() {
        component.activateSync();
    }

    @Override
    public void deactivateSync() {
        component.deactivateSync();
    }

    @Override
    public void onInitFromOptions(OptionsStorage options) {
        DataInspectorOptions dataInspectorPreferences = new DataInspectorOptions(options);
        boolean useDefaultFont = dataInspectorPreferences.isUseDefaultFont();
        if (useDefaultFont) {
            component.setInputFieldsFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        } else {
            Map<TextAttribute, Object> fontAttributes = dataInspectorPreferences.getFontAttributes();
            component.setInputFieldsFont(new Font(fontAttributes));
        }
    }
}
