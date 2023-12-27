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

import com.sun.tools.doclint.HtmlTag;
import com.sun.tools.javac.util.Pair;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import jdk.internal.joptsimple.internal.Strings;
import org.exbin.bined.CodeAreaSection;
import org.exbin.bined.basic.BasicCodeAreaSection;
import org.exbin.bined.basic.MovementDirection;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.basic.DefaultCodeAreaCommandHandler;
import org.exbin.framework.bined.makro.model.MakroRecord;

/**
 * Command handler with support for makro recording.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaMakroCommandHandler extends CodeAreaOperationCommandHandler {

    private final int metaMask = CodeAreaSwingUtils.getMetaMaskDown();
    private MakroRecord recordingMakro = null;
    private MakroStep lastMakroStep = null;

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
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.LEFT));
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.RIGHT));
                    break;
                }
                case KeyEvent.VK_UP: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.UP));
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.DOWN));
                    break;
                }
                case KeyEvent.VK_HOME: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of((keyEvent.getModifiersEx() & metaMask) > 0 ? MovementDirection.DOC_START : MovementDirection.ROW_START));
                    break;
                }
                case KeyEvent.VK_END: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of((keyEvent.getModifiersEx() & metaMask) > 0 ? MovementDirection.DOC_END : MovementDirection.ROW_END));
                    break;
                }
                case KeyEvent.VK_PAGE_UP: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.PAGE_UP));
                    break;
                }
                case KeyEvent.VK_PAGE_DOWN: {
                    appendMakroOperationStep(isSelecting(keyEvent) ? MakroStep.SELECTION_UPDATE : MakroStep.CARET_MOVE, List.of(MovementDirection.PAGE_DOWN));
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
            appendMakroOperationStep(MakroStep.CLIPBOARD_DELETE);
        }

        super.delete();
    }

    @Override
    public void copy() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_COPY);
        }

        super.copy();
    }

    @Override
    public void copyAsCode() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_COPY_AS_CODE);
        }

        super.copyAsCode();
    }

    @Override
    public void cut() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_CUT);
        }

        super.cut();
    }

    @Override
    public void paste() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_PASTE);
        }

        super.paste();
    }

    @Override
    public void pasteFromCode() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.CLIPBOARD_PASTE_FROM_CODE);
        }

        super.pasteFromCode();
    }

    @Override
    public void selectAll() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.SELECTION_SELECT_ALL);
        }

        super.selectAll();
    }

    @Override
    public void clearSelection() {
        if (recordingMakro != null) {
            appendMakroOperationStep(MakroStep.SELECTION_CLEAR);
        }

        super.clearSelection();
    }

    @Nonnull
    private static boolean isSelecting(KeyEvent keyEvent) {
        return (keyEvent.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0;
    }

    public void appendMakroOperationStep(MakroStep makroStep) {
        appendMakroOperationStep(makroStep, List.of());
    }

    public void appendMakroOperationStep(MakroStep makroStep, List<Object> parameters) {
        /*        if (lastMakroStep == makroStep) {
            List<String> steps = recordingMakro.getSteps();
            List<Object> lastStepParameters = 
            switch (makroStep) {
                case CARET_MOVE:
                    
                    break;
            }
        }*/

        recordingMakro.addStep(stepAsString(makroStep, parameters));
    }

    @Nonnull
    public String stepAsString(MakroStep makroStep, List<Object> parameters) {
        if (parameters.isEmpty()) {
            return makroStep.getOperationCode();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makroStep.getOperationCode());
        stringBuilder.append("(");

        boolean first = true;
        for (Object parameter : parameters) {
            if (!first) {
                stringBuilder.append(",");
            } else {
                first = false;
            }

            if (parameter instanceof String) {
                stringBuilder.append("\"");
                for (char c : ((String) parameter).toCharArray()) {
                    if (c >= 128) {
                        stringBuilder.append("\\u").append(String.format("%04X", (int) c));
                    } else {
                        switch (c) {
                            case 13: {
                                stringBuilder.append("\\n");
                                break;
                            }
                            case 10: {
                                stringBuilder.append("\\r");
                                break;
                            }
                            case 9: {
                                stringBuilder.append("\\t");
                                break;
                            }
                            case 34: {
                                stringBuilder.append("\\\"");
                                break;
                            }
                            case 92: {
                                stringBuilder.append("\\\\");
                                break;
                            }

                            default:
                                stringBuilder.append(c);
                        }
                    }
                }
                stringBuilder.append((String) parameter);
                stringBuilder.append("\"");
            } else if (parameter instanceof Integer) {
                stringBuilder.append(Integer.toString((Integer) parameter));
            } else if (parameter instanceof MovementDirection) {
                stringBuilder.append(((MovementDirection) parameter).name());
            }
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Nonnull
    public Pair<MakroStep, List<Object>> parseStep(String stepString) throws ParseException, NumberFormatException {
        String operationCode;
        List<Object> parameters = new ArrayList<>();
        int parametersStart = stepString.indexOf("(");
        if (parametersStart >= 0) {
            operationCode = stepString.substring(0, parametersStart - 1);

            int position = parametersStart + 1;
            while (position < stepString.length()) {
                char firstChar = stepString.charAt(position);
                if (firstChar == '\"') {
                    StringBuilder stringBuilder = new StringBuilder();
                    position++;
                    while (position < stepString.length()) {
                        char nextChar = stepString.charAt(position);
                        if (nextChar == 34) {
                            position++;
                            if (position == stepString.length()) {
                                throw new ParseException("Missing close bracket", stepString.length() - 1);
                            }
                            nextChar = stepString.charAt(position);
                            if (nextChar == ',' || nextChar == ')') {
                                position++;
                            } else {
                                throw new ParseException("Unexpected character", position);
                            }
                            break;
                        } else if (nextChar == 92) {
                            position++;
                            if (position == stepString.length()) {
                                throw new ParseException("Missing escaped character", stepString.length() - 1);
                            }
                            nextChar = stepString.charAt(position);
                            switch (nextChar) {
                                case 'n': {
                                    stringBuilder.append("\n");
                                    break;
                                }
                                case 'r': {
                                    stringBuilder.append("\r");
                                    break;
                                }
                                case 't': {
                                    stringBuilder.append("\t");
                                    break;
                                }
                                case '\"': {
                                    stringBuilder.append("\"");
                                    break;
                                }
                                case '\\': {
                                    stringBuilder.append("\\");
                                    break;
                                }
                                case 'u': {
                                    if (position <= stepString.length() - 5) {
                                        throw new ParseException("Incomplete unicode escape sequence", position);
                                    }
                                    int code = Integer.parseInt(
                                            String.valueOf(stepString.charAt(position + 1))
                                            + stepString.charAt(position + 2)
                                            + stepString.charAt(position + 3)
                                            + stepString.charAt(position + 4), 16);
                                    stringBuilder.append(Character.toChars(code));
                                    position += 4;
                                    break;
                                }
                                default:
                                    throw new ParseException("Unsupported escaped character", position);
                            }
                            position++;
                        } else {
                            stringBuilder.append(nextChar);
                            position++;
                        }
                    }

                } else {
                    int paramEnd = stepString.indexOf(",");
                    if (paramEnd == -1) {
                        paramEnd = stepString.indexOf(")");
                        if (paramEnd == -1) {
                            throw new ParseException("Missing close bracket", stepString.length() - 1);
                        }
                    }
                    if (firstChar >= '0' && firstChar <= '9') {
                        Integer parameter = Integer.valueOf(stepString.substring(position, paramEnd - 1));
                        parameters.add(parameter);
                    } else {
                        // Currently only direction enum is supported
                        String value = stepString.substring(position, paramEnd - 1);
                        MovementDirection parameter = null;
                        for (MovementDirection movementDirection : MovementDirection.values()) {
                            if (value.equals(movementDirection.name())) {
                                parameter = movementDirection;
                                break;
                            }
                        }
                        if (parameter != null) {
                            parameters.add(parameter);
                        } else {
                            throw new ParseException("Unknown value", position);
                        }
                    }
                    position = paramEnd + 1;
                }
            }
        } else {
            operationCode = stepString;
        }

        Optional<MakroStep> makroStep = MakroStep.findByCode(operationCode);
        return new Pair<>(makroStep.orElse(null), parameters);
    }
}
