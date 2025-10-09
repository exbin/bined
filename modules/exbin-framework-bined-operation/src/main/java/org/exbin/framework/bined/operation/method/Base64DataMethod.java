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
package org.exbin.framework.bined.operation.method;

import java.awt.Component;
import java.util.Base64;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.method.gui.Base64DataPanel;
import org.exbin.framework.bined.operation.ConversionDataProvider;
import org.exbin.framework.bined.operation.command.ConvertDataCommand;
import org.exbin.framework.bined.operation.ConvertDataOperation;
import org.exbin.framework.bined.operation.gui.BinaryPreviewPanel;

/**
 * Base 64 data method.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class Base64DataMethod implements ConvertDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(Base64DataPanel.class);

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
        Base64DataPanel component = new Base64DataPanel();
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((Base64DataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea) {
        Base64DataPanel panel = (Base64DataPanel) component;
        OperationType operationType = panel.getOperationType();

        long position;
        long length;
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }

        ConversionDataProvider conversionDataProvider = (EditableBinaryData binaryData, long sourcePosition, long sourceLength, long targetPosition) -> {
            convertData(binaryData, sourcePosition, sourceLength, operationType, binaryData, targetPosition);
        };

        return new ConvertDataCommand(codeArea, new ConvertDataOperation(position, length, length, conversionDataProvider));
    }

    @Override
    public BinaryData performDirectConvert(Component component, CodeAreaCore codeArea) {
        Base64DataPanel panel = (Base64DataPanel) component;
        OperationType operationType = panel.getOperationType();
        long position;
        long length;
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }

        EditableBinaryData binaryData = new ByteArrayEditableData();
        convertData(codeArea.getContentData(), position, length, operationType, binaryData, 0);
        return binaryData;
    }

    public void convertData(BinaryData sourceBinaryData, long position, long length, OperationType operationType, EditableBinaryData targetBinaryData, long targetPosition) throws IllegalStateException {
        switch (operationType) {
            case BASIC_ENCODER: {
                Base64.Encoder encoder = Base64.getEncoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                byte[] output = encoder.encode(sourceData);
                targetBinaryData.insert(targetPosition, output);
                break;
            }
            case BASIC_DECODER: {
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                try {
                    byte[] output = decoder.decode(sourceData);
                    targetBinaryData.insert(targetPosition, output);
                } catch (IllegalArgumentException ex) {
                    // ignore
                }
                break;
            }
            case MIME_ENCODER: {
                Base64.Encoder encoder = Base64.getMimeEncoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                try {
                    byte[] output = encoder.encode(sourceData);
                    targetBinaryData.insert(targetPosition, output);
                } catch (IllegalArgumentException ex) {
                    // ignore
                }
                break;
            }
            case MIME_DECODER: {
                Base64.Decoder decoder = Base64.getMimeDecoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                byte[] output = decoder.decode(sourceData);
                targetBinaryData.insert(targetPosition, output);
                break;
            }
            case URL_ENCODER: {
                Base64.Encoder encoder = Base64.getUrlEncoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                try {
                    byte[] output = encoder.encode(sourceData);
                    targetBinaryData.insert(targetPosition, output);
                } catch (IllegalArgumentException ex) {
                    // ignore
                }
                break;
            }
            case URL_DECODER: {
                Base64.Decoder decoder = Base64.getUrlDecoder();
                byte[] sourceData = new byte[(int) length];
                sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);
                try {
                    byte[] output = decoder.decode(sourceData);
                    targetBinaryData.insert(targetPosition, output);
                } catch (IllegalArgumentException ex) {
                    // ignore
                }
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(operationType);
        }
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        Base64DataPanel panel = (Base64DataPanel) component;
        panel.setResultChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(Base64DataPanel panel, CodeAreaCore codeArea) {
        previewPanel = new BinaryPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);
        SwingUtilities.invokeLater(() -> {
            OperationType operationType = panel.getOperationType();

            EditableBinaryData previewBinaryData = new ByteArrayEditableData();
            previewBinaryData.clear();
            long position;
            long length;
            SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
            if (selection.isEmpty()) {
                position = 0;
                length = codeArea.getDataSize();
            } else {
                position = selection.getFirst();
                length = selection.getLength();
            }
            convertData(codeArea.getContentData(), position, length, operationType, previewBinaryData, 0);
            long previewDataSize = previewBinaryData.getDataSize();
            if (previewDataSize > previewLengthLimit) {
                previewBinaryData.remove(previewLengthLimit, previewDataSize - previewLengthLimit);
            }
            previewPanel.setPreviewData(previewBinaryData);
        });
    }

    public enum OperationType {
        BASIC_ENCODER,
        BASIC_DECODER,
        MIME_ENCODER,
        MIME_DECODER,
        URL_ENCODER,
        URL_DECODER
    }
}
