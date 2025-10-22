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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.bined.CodeAreaCaretListener;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.DataChangedListener;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.operation.BinaryDataUndoRedoChangeListener;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.bined.inspector.settings.DataInspectorOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * BinEd basic values inspector.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicValuesInspector implements BinEdInspector {

    private BasicValuesPanel component;
    private CodeAreaCore codeArea;
    private BinaryDataUndoRedo undoRedo;

    private DataChangedListener dataChangedListener;
    private CodeAreaCaretListener caretMovedListener;
    private BinaryDataUndoRedoChangeListener undoRedoChangeListener;

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (component == null) {
            component = createComponent();
            dataChangedListener = () -> {
                component.updateEditMode();
                component.updateValues();
            };
            caretMovedListener = (CodeAreaCaretPosition caretPosition) -> component.updateValues();
            undoRedoChangeListener = component::updateValues;
        }
        return component;
    }

    @Nonnull
    protected BasicValuesPanel createComponent() {
        return new BasicValuesPanel();
    }

    @Override
    public void setCodeArea(CodeAreaCore codeArea, @Nullable BinaryDataUndoRedo undoRedo) {
        this.codeArea = codeArea;
        this.undoRedo = undoRedo;
        component.setCodeArea(codeArea, undoRedo);
    }

    @Override
    public void activateSync() {
        codeArea.addDataChangedListener(dataChangedListener);
        ((CaretCapable) codeArea).addCaretMovedListener(caretMovedListener);
        if (undoRedo != null) {
            undoRedo.addChangeListener(undoRedoChangeListener);
        }
        component.updateEditMode();
        component.updateValues();
    }

    @Override
    public void deactivateSync() {
        codeArea.removeDataChangedListener(dataChangedListener);
        ((CaretCapable) codeArea).removeCaretMovedListener(caretMovedListener);
        if (undoRedo != null) {
            undoRedo.addChangeListener(undoRedoChangeListener);
        }
    }

    @Override
    public void onInitFromOptions(OptionsStorage options) {
        getComponent();
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
