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
package org.exbin.bined.jaguif.component.status;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeListener;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * BinEd edit mode status component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryDocumentSizeComponent extends AbstractStatusBarComponent {

    protected static final String BR_TAG = "<br>";

    protected final JLabel component;
    protected long documentSize;
    protected long initialDocumentSize;
    protected StatusCursorPositionFormat cursorPositionFormat = new StatusCursorPositionFormat();
    protected StatusDocumentSizeFormat documentSizeFormat = new StatusDocumentSizeFormat();

    public BinaryDocumentSizeComponent() {
        component = new JLabel();
        BinedComponentModule componentModule = App.getModule(BinedComponentModule.class);
        ResourceBundle resourceBundle = componentModule.getResourceBundle();
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setText("0 (0)");
        component.setToolTipText(resourceBundle.getString("documentSizeLabel.toolTipText")); // NOI18N
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocument.class, new ContextChangeListener<ContextDocument>() {
                    @Override
                    public void stateChanged(ContextDocument instance) {
                        // TODO
                    }
                });
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }
/*
    private void updateDocumentSize() {
        if (documentSize == -1) {
            component.setText(documentSizeFormat.isShowRelative() ? "0 (0)" : "0");
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            if (selectionRange != null && !selectionRange.isEmpty()) {
                labelBuilder.append(String.format(
                        resourceBundle.getString("documentSize.text"),
                        numberToPosition(selectionRange.getLength(), documentSizeFormat.getCodeType()),
                        numberToPosition(documentSize, documentSizeFormat.getCodeType())
                ));
            } else {
                labelBuilder.append(numberToPosition(documentSize, documentSizeFormat.getCodeType()));
                if (documentSizeFormat.isShowRelative()) {
                    long difference = documentSize - initialDocumentSize;
                    labelBuilder.append(difference > 0 ? " (+" : " (");
                    labelBuilder.append(numberToPosition(difference, documentSizeFormat.getCodeType()));
                    labelBuilder.append(")");

                }
            }

            component.setText(labelBuilder.toString());
        }
    }

    private void updateDocumentSizeToolTip() {
        String octalPrefix = resourceBundle.getString("codeType.octal") + ": ";
        String decimalPrefix = resourceBundle.getString("codeType.decimal") + ": ";
        String hexadecimalPrefix = resourceBundle.getString("codeType.hexadecimal") + ": ";

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        if (selectionRange != null && !selectionRange.isEmpty()) {
            long length = selectionRange.getLength();
            builder.append(resourceBundle.getString("selectionLengthLabel.toolTipText")).append(BR_TAG);
            builder.append(octalPrefix).append(numberToPosition(length, PositionCodeType.OCTAL)).append(BR_TAG);
            builder.append(decimalPrefix).append(numberToPosition(length, PositionCodeType.DECIMAL)).append(BR_TAG);
            builder.append(hexadecimalPrefix).append(numberToPosition(length, PositionCodeType.HEXADECIMAL)).append(BR_TAG);
            builder.append(BR_TAG);
        }

        builder.append(resourceBundle.getString("documentSizeLabel.toolTipText")).append(BR_TAG);
        builder.append(octalPrefix).append(numberToPosition(documentSize, PositionCodeType.OCTAL)).append(BR_TAG);
        builder.append(decimalPrefix).append(numberToPosition(documentSize, PositionCodeType.DECIMAL)).append(BR_TAG);
        builder.append(hexadecimalPrefix).append(numberToPosition(documentSize, PositionCodeType.HEXADECIMAL));
        builder.append("</body></html>");

        component.setToolTipText(builder.toString());
    } */
}
