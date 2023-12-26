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
package org.exbin.framework.bined.makro.operation;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaSection;
import org.exbin.bined.basic.BasicCodeAreaSection;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.framework.bined.makro.model.MakroRecord;

/**
 * Command handler with support for makro recording.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaMakroCommandHandler extends CodeAreaOperationCommandHandler {

    private MakroRecord recordingMakro = null;

    public CodeAreaMakroCommandHandler(CodeAreaCore codeArea, BinaryDataUndoHandler undoHandler) {
        super(codeArea, undoHandler);
    }

    @Nonnull
    public static CodeAreaCommandHandler.CodeAreaCommandHandlerFactory createDefaultCodeAreaCommandHandlerFactory() {
        return (CodeAreaCore codeAreaCore) -> new CodeAreaMakroCommandHandler(codeAreaCore, new CodeAreaUndoHandler(codeAreaCore));
    }

    @Nonnull
    public Optional<MakroRecord> getRecordingMakro() {
        return Optional.ofNullable(recordingMakro);
    }

    public void setRecordingMakro(MakroRecord recordingMakro) {
        this.recordingMakro = recordingMakro;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (recordingMakro != null) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_LEFT: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("LEFT"));
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("RIGHT"));
                    break;
                }
                case KeyEvent.VK_UP: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("UP"));
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("DOWN"));
                    break;
                }
                case KeyEvent.VK_HOME: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("HOME"));
                    break;
                }
                case KeyEvent.VK_END: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("END"));
                    break;
                }
                case KeyEvent.VK_PAGE_UP: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("PAGE_UP"));
                    break;
                }
                case KeyEvent.VK_PAGE_DOWN: {
                    appendMakroOperationStep(MakroStep.CARET_MOVED, List.of("PAGE_DOWN"));
                    break;
                }
                case KeyEvent.VK_INSERT: {
                    break;
                }
            }
        }

        super.keyPressed(keyEvent);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        char keyValue = keyEvent.getKeyChar();
        if (recordingMakro != null && keyValue != KeyEvent.CHAR_UNDEFINED) {
            CodeAreaSection section = ((CaretCapable) codeArea).getActiveSection();
            if (section != BasicCodeAreaSection.TEXT_PREVIEW) {
                appendMakroOperationStep(MakroStep.KEY_PRESSED, List.of(keyValue));
            } else {
                if (keyValue > DefaultCodeAreaCommandHandler.LAST_CONTROL_CODE && keyValue != DELETE_CHAR) {
                    appendMakroOperationStep(MakroStep.KEY_PRESSED, List.of(keyValue));
                }
            }

        }

        super.keyTyped(keyEvent);
    }

    @Override
    public void enterPressed() {
        super.enterPressed();
    }

    @Override
    public void tabPressed() {
        tabPressed(SelectingMode.NONE);
    }

    @Override
    public void tabPressed(SelectingMode selectingMode) {
        super.tabPressed();
    }

    @Override
    public void backSpacePressed() {
        super.backSpacePressed();
    }

    @Override
    public void deletePressed() {
        super.deletePressed();
    }

    @Override
    public void delete() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_DELETE, List.of());
        }

        super.delete();
    }

    @Override
    public void copy() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_COPY, List.of());
        }

        super.copy();
    }

    @Override
    public void copyAsCode() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_COPY_AS_CODE, List.of());
        }

        super.copyAsCode();
    }

    @Override
    public void cut() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_CUT, List.of());
        }

        super.cut();
    }

    @Override
    public void paste() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_PASTE, List.of());
        }

        super.paste();
    }

    @Override
    public void pasteFromCode() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_PASTE_FROM_CODE, List.of());
        }

        super.pasteFromCode();
    }

    @Override
    public void selectAll() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.SELECTION_SELECT_ALL, List.of());
        }

        super.selectAll();
    }

    @Override
    public void clearSelection() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.SELECTION_CLEAR, List.of());
        }

        super.clearSelection();
    }

    public void appendMakroOperationStep(MakroStep makroStep, List<Object> parameters) {

    }
}
