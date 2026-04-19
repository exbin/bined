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
package org.exbin.bined.jaguif.operation.method.basic;

import java.awt.Component;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.bined.jaguif.operation.method.basic.gui.RandomDataPanel;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.bined.jaguif.operation.method.api.InsertDataMethod;
import org.exbin.bined.jaguif.operation.method.api.PreviewDataHandler;
import org.exbin.bined.jaguif.operation.method.command.InsertFromProviderCommand;
import org.exbin.bined.jaguif.operation.method.InsertFromProviderOperation;
import org.exbin.bined.jaguif.operation.method.ReplaceDataOperation;
import org.exbin.bined.jaguif.operation.method.InsertionDataProvider;
import org.exbin.bined.jaguif.operation.method.command.ReplaceDataCommand;
import org.exbin.bined.jaguif.operation.method.gui.BinaryPreviewPanel;

/**
 * Generate random data method.
 */
@ParametersAreNonnullByDefault
public class RandomDataMethod implements InsertDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(RandomDataPanel.class);

    private PreviewDataHandler previewDataHandler;
    private long previewLengthLimit = 0;
    private BinaryPreviewPanel previewPanel;

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("method.name");
    }

    @Nonnull
    @Override
    public Component createComponent() {
        return new RandomDataPanel();
    }

    @Override
    public void initFocus(Component component) {
        ((RandomDataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createInsertCommand(Component component, CodeAreaCore codeArea, long position, EditOperation editOperation) {
        RandomDataPanel panel = (RandomDataPanel) component;
        long length = panel.getDataLength();
        AlgorithmType algorithmType = panel.getAlgorithmType();

        InsertionDataProvider dataOperationDataProvider = (EditableBinaryData binaryData, long insertPosition) -> {
            generateData(binaryData, algorithmType, insertPosition, length);
        };

        if (editOperation == EditOperation.OVERWRITE) {
            return new ReplaceDataCommand(codeArea, new ReplaceDataOperation(position, length, dataOperationDataProvider));
        } else {
            return new InsertFromProviderCommand(codeArea, new InsertFromProviderOperation(position, length, dataOperationDataProvider));
        }
    }

    public static void generateData(EditableBinaryData binaryData, AlgorithmType algorithmType, long position, long length) throws IllegalStateException {
        Random random = new Random();
        for (long pos = position; pos < position + length; pos++) {
            byte value;
            switch (algorithmType) {
                case FULL_BYTES:
                    value = (byte) random.nextInt();
                    break;
                case LOWER_HALF: {
                    value = (byte) random.nextInt(128);
                    break;
                }
                case ALPHABET_ONLY: {
                    value = (byte) random.nextInt(52);
                    value += (value < 26) ? 'A' : 'a' - 26;
                    break;
                }
                case NUMBER_DIGITS: {
                    value = (byte) (random.nextInt(10) + '0');
                    break;
                }
                default:
                    throw CodeAreaUtils.getInvalidTypeException(algorithmType);
            }
            binaryData.setByte(pos, value);
        }
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        RandomDataPanel panel = (RandomDataPanel) component;
        panel.setParamChangeListener(() -> {
            fillPreviewData(panel);
        });
        fillPreviewData(panel);
    }

    private void fillPreviewData(RandomDataPanel panel) {
        previewPanel = new BinaryPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);
        SwingUtilities.invokeLater(() -> {
            AlgorithmType algorithmType = panel.getAlgorithmType();
            long dataLength = panel.getDataLength();
            if (dataLength > previewLengthLimit) {
                dataLength = previewLengthLimit;
            }

            EditableBinaryData previewBinaryData = new ByteArrayEditableData();
            previewBinaryData.insertUninitialized(0, dataLength);
            generateData(previewBinaryData, algorithmType, 0, dataLength);
            previewPanel.setPreviewData(previewBinaryData);
        });
    }

    public enum AlgorithmType {
        FULL_BYTES,
        LOWER_HALF,
        ALPHABET_ONLY,
        NUMBER_DIGITS
    }
}
