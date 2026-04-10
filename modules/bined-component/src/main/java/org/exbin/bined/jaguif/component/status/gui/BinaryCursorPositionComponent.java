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
package org.exbin.bined.jaguif.component.status.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.jaguif.component.BinaryFileDocument;
import org.exbin.bined.jaguif.component.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.component.status.StatusCursorPositionFormat;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * BinEd edit mode status component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryCursorPositionComponent extends AbstractStatusBarComponent {

    protected static final String BR_TAG = "<br>";

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryCursorPositionComponent.class);

    private int octalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_OCTAL_SPACE_GROUP_SIZE;
    private int decimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_DECIMAL_SPACE_GROUP_SIZE;
    private int hexadecimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE;
    protected StatusCursorPositionFormat cursorPositionFormat = new StatusCursorPositionFormat();

    private CodeAreaCaretPosition caretPosition;
    protected SelectionRange selectionRange;

    public BinaryCursorPositionComponent() {
        component = new JLabel();
        component.setPreferredSize(new Dimension(160, component.getPreferredSize().height));
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() > 1) {
                    // TODO controller.changeCursorPosition();
                }
            }
        });
        clear();

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocument.class, (ContextDocument instance) -> {
                    if (instance instanceof BinaryFileDocument) {
                        updateForDocument((BinaryFileDocument) instance);
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextDocument.class, (ContextDocument instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryFileDocument && (updateType == BinaryFileDocument.UpdateType.CURSOR_MOVED || updateType == BinaryFileDocument.UpdateType.SELECTION_CHANGED)) {
                        updateForDocument((BinaryFileDocument) instance);
                    } else {
                        clear();
                    }
                });
            }

            private void updateForDocument(BinaryFileDocument document) {
                caretPosition = ((CaretCapable) document.getCodeArea()).getActiveCaretPosition();
                selectionRange = ((SelectionCapable) document.getCodeArea()).getSelection();
                update();
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }

    private void update() {
        updateCaretPosition();
        updateCursorPositionToolTip();
    }
    
    private void clear() {
        component.setText("-");
        component.setToolTipText(resourceBundle.getString("cursorPositionLabel.toolTipText"));
    }

    private void updateCaretPosition() {
        if (caretPosition == null) {
            component.setText("-");
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            if (selectionRange != null && !selectionRange.isEmpty()) {
                long first = selectionRange.getFirst();
                long last = selectionRange.getLast();
                labelBuilder.append(String.format(
                        resourceBundle.getString("caretPosition.text"),
                        numberToPosition(first, cursorPositionFormat.getCodeType()),
                        numberToPosition(last, cursorPositionFormat.getCodeType())
                ));
            } else {
                labelBuilder.append(numberToPosition(caretPosition.getDataPosition(), cursorPositionFormat.getCodeType()));
                if (cursorPositionFormat.isShowOffset()) {
                    labelBuilder.append(":");
                    labelBuilder.append(caretPosition.getCodeOffset());
                }
            }
            component.setText(labelBuilder.toString());
        }
    }

    private void updateCursorPositionToolTip() {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        if (caretPosition == null) {
            builder.append(resourceBundle.getString("cursorPositionLabel.toolTipText"));
        } else {
            String octalPrefix = resourceBundle.getString("codeType.octal") + ": ";
            String decimalPrefix = resourceBundle.getString("codeType.decimal") + ": ";
            String hexadecimalPrefix = resourceBundle.getString("codeType.hexadecimal") + ": ";
            if (selectionRange != null && !selectionRange.isEmpty()) {
                long first = selectionRange.getFirst();
                long last = selectionRange.getLast();
                builder.append(resourceBundle.getString("selectionFromLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(numberToPosition(first, PositionCodeType.OCTAL)).append(BR_TAG);
                builder.append(decimalPrefix).append(numberToPosition(first, PositionCodeType.DECIMAL)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(numberToPosition(first, PositionCodeType.HEXADECIMAL)).append(BR_TAG);
                builder.append(BR_TAG);
                builder.append(resourceBundle.getString("selectionToLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(numberToPosition(last, PositionCodeType.OCTAL)).append(BR_TAG);
                builder.append(decimalPrefix).append(numberToPosition(last, PositionCodeType.DECIMAL)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(numberToPosition(first, PositionCodeType.HEXADECIMAL)).append(BR_TAG);
            } else {
                long dataPosition = caretPosition.getDataPosition();
                builder.append(resourceBundle.getString("cursorPositionLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(numberToPosition(dataPosition, PositionCodeType.OCTAL)).append(BR_TAG);
                builder.append(decimalPrefix).append(numberToPosition(dataPosition, PositionCodeType.DECIMAL)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(numberToPosition(dataPosition, PositionCodeType.HEXADECIMAL));
            }
        }
        builder.append("</body></html>");

        component.setToolTipText(builder.toString());
    }

    @Nonnull
    private String numberToPosition(long value, PositionCodeType codeType) {
        if (value == 0) {
            return "0";
        }

        int spaceGroupSize = 0;
        switch (codeType) {
            case OCTAL: {
                spaceGroupSize = octalSpaceGroupSize;
                break;
            }
            case DECIMAL: {
                spaceGroupSize = decimalSpaceGroupSize;
                break;
            }
            case HEXADECIMAL: {
                spaceGroupSize = hexadecimalSpaceGroupSize;
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(codeType);
        }

        long remainder = value > 0 ? value : -value;
        StringBuilder builder = new StringBuilder();
        int base = codeType.getBase();
        int groupSize = spaceGroupSize == 0 ? -1 : spaceGroupSize;
        while (remainder > 0) {
            if (groupSize >= 0) {
                if (groupSize == 0) {
                    builder.insert(0, ' ');
                    groupSize = spaceGroupSize - 1;
                } else {
                    groupSize--;
                }
            }

            int digit = (int) (remainder % base);
            remainder = remainder / base;
            builder.insert(0, CodeAreaUtils.UPPER_HEX_CODES[digit]);
        }

        if (value < 0) {
            builder.insert(0, "-");
        }
        return builder.toString();
    }
}
