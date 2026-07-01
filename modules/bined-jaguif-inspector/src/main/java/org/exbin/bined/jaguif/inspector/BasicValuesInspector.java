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
package org.exbin.bined.jaguif.inspector;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import org.exbin.bined.CodeAreaCaretListener;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.DataChangedListener;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.operation.BinaryDataUndoRedoChangeListener;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.jaguif.inspector.gui.BasicValuesPanel;

/**
 * BinEd basic values inspector.
 */
@NullMarked
public class BasicValuesInspector implements BinEdInspector {

    protected BasicValuesPanel component;
    protected CodeAreaCore codeArea;
    protected BinaryDataUndoRedo undoRedo;

    protected DataChangedListener dataChangedListener;
    protected CodeAreaCaretListener caretMovedListener;
    protected BinaryDataUndoRedoChangeListener undoRedoChangeListener;

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
}
