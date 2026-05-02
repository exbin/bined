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
package org.exbin.bined.jaguif.viewer.status.gui;

import java.awt.Dimension;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.viewer.status.StatusDataSizeFormat;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * BinEd edit mode status component.
 */
@ParametersAreNonnullByDefault
public class BinaryDataSizeComponent extends AbstractStatusBarComponent {

    protected static final String BR_TAG = "<br>";

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryDataSizeComponent.class);

    private int octalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_OCTAL_SPACE_GROUP_SIZE;
    private int decimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_DECIMAL_SPACE_GROUP_SIZE;
    private int hexadecimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE;
    protected StatusDataSizeFormat dataSizeFormat = new StatusDataSizeFormat();

    protected long dataSize;
    protected long originalDataSize = 0;
    protected SelectionRange selectionRange;

    public BinaryDataSizeComponent() {
        component = new JLabel();
        component.setPreferredSize(new Dimension(160, component.getPreferredSize().height));
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        clear();

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof BinaryDataComponent) {
                        updateForComponent((BinaryDataComponent) instance);
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextComponent.class, (ContextComponent instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryDataComponent && (updateType == BinEdDataComponent.UpdateType.DATA_CHANGED || updateType == BinEdDataComponent.UpdateType.SELECTION_CHANGED)) {
                        updateForComponent((BinaryDataComponent) instance);
                    }
                });
            }

            private void updateForComponent(BinaryDataComponent component) {
                dataSize = component.getCodeArea().getDataSize();
                selectionRange = ((SelectionCapable) component.getCodeArea()).getSelection();
                update();
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }

    public void setOriginalDataSize(long originalDataSize) {
        this.originalDataSize = originalDataSize;
    }

    protected void update() {
        updateDataSize();
        updateDataSizeToolTip();
    }

    protected void clear() {
        component.setText("-");
        component.setToolTipText(resourceBundle.getString("dataSizeLabel.toolTipText"));
    }

    private void updateDataSize() {
        if (dataSize == -1) {
            component.setText(dataSizeFormat.isShowRelative() ? "0 (0)" : "0");
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            if (selectionRange != null && !selectionRange.isEmpty()) {
                labelBuilder.append(String.format(resourceBundle.getString("dataSize.text"),
                        numberToPosition(selectionRange.getLength(), dataSizeFormat.getCodeType()),
                        numberToPosition(dataSize, dataSizeFormat.getCodeType())
                ));
            } else {
                labelBuilder.append(numberToPosition(dataSize, dataSizeFormat.getCodeType()));
                if (dataSizeFormat.isShowRelative()) {
                    long difference = dataSize - originalDataSize;
                    labelBuilder.append(difference > 0 ? " (+" : " (");
                    labelBuilder.append(numberToPosition(difference, dataSizeFormat.getCodeType()));
                    labelBuilder.append(")");

                }
            }

            component.setText(labelBuilder.toString());
        }
    }

    private void updateDataSizeToolTip() {
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

        builder.append(resourceBundle.getString("dataSizeLabel.toolTipText")).append(BR_TAG);
        builder.append(octalPrefix).append(numberToPosition(dataSize, PositionCodeType.OCTAL)).append(BR_TAG);
        builder.append(decimalPrefix).append(numberToPosition(dataSize, PositionCodeType.DECIMAL)).append(BR_TAG);
        builder.append(hexadecimalPrefix).append(numberToPosition(dataSize, PositionCodeType.HEXADECIMAL));
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
