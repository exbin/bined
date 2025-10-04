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
package org.exbin.framework.bined.operation.component;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
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
import org.exbin.framework.bined.operation.component.gui.CompressionDataPanel;
import org.exbin.framework.bined.operation.ConversionDataProvider;
import org.exbin.framework.bined.operation.command.ConvertDataCommand;
import org.exbin.framework.bined.operation.ConvertDataOperation;


@ParametersAreNonnullByDefault
public class CompressionDataMethod implements ConvertDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CompressionDataPanel.class);

    private PreviewDataHandler previewDataHandler;
    private long previewLengthLimit = 0;

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("component.name");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        CompressionDataPanel component = new CompressionDataPanel();
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((CompressionDataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea) {
        CompressionDataPanel panel = (CompressionDataPanel) component;
        OperationType operationType = panel.getOperationType();
        CompressionAlgorithm algorithm = panel.getAlgorithm();
        boolean autoDetect = panel.isAutoDetect();

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
            convertData(binaryData, sourcePosition, sourceLength, operationType, algorithm, autoDetect, binaryData, targetPosition);
        };

        return new ConvertDataCommand(codeArea, new ConvertDataOperation(position, length, length, conversionDataProvider));
    }

    @Override
    public BinaryData performDirectConvert(Component component, CodeAreaCore codeArea) {
        CompressionDataPanel panel = (CompressionDataPanel) component;
        OperationType operationType = panel.getOperationType();
        CompressionAlgorithm algorithm = panel.getAlgorithm();
        boolean autoDetect = panel.isAutoDetect();

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
        convertData(codeArea.getContentData(), position, length, operationType, algorithm, autoDetect, binaryData, 0);
        return binaryData;
    }

    /**
     * Compresses or decompresses binary data.
     *
     * @param sourceBinaryData source binary data
     * @param position starting position
     * @param length data length
     * @param operationType compress or decompress
     * @param algorithm compression algorithm
     * @param autoDetect auto-detect compression type for decompression
     * @param targetBinaryData target binary data
     * @param targetPosition target position
     * @throws IllegalStateException on conversion error
     */
    public void convertData(BinaryData sourceBinaryData, long position, long length, OperationType operationType,
                            CompressionAlgorithm algorithm, boolean autoDetect, EditableBinaryData targetBinaryData,
                            long targetPosition) throws IllegalStateException {

        byte[] sourceData = new byte[(int) length];
        sourceBinaryData.copyToArray(position, sourceData, 0, (int) length);

        try {
            byte[] output;
            if (operationType == OperationType.COMPRESS) {
                output = compress(sourceData, algorithm);
            } else {
                // Auto-detect compression type if enabled
                CompressionAlgorithm detectedAlgorithm = algorithm;
                if (autoDetect) {
                    CompressionAlgorithm detected = detectCompressionType(sourceData);
                    if (detected != null) {
                        detectedAlgorithm = detected;
                    }
                }
                output = decompress(sourceData, detectedAlgorithm);
            }
            targetBinaryData.insert(targetPosition, output);

        } catch (Exception ex) {
            // Handle errors gracefully
            String errorMsg = "Error: " + ex.getMessage();
            byte[] output = errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            targetBinaryData.insert(targetPosition, output);
        }
    }

    /**
     * Compresses data using specified algorithm.
     *
     * @param data input data
     * @param algorithm compression algorithm
     * @return compressed data
     * @throws IOException on compression error
     */
    private byte[] compress(byte[] data, CompressionAlgorithm algorithm) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        switch (algorithm) {
            case GZIP: {
                try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                    gzip.write(data);
                }
                break;
            }
            case ZIP: {
                try (ZipOutputStream zip = new ZipOutputStream(baos)) {
                    zip.putNextEntry(new ZipEntry("data"));
                    zip.write(data);
                    zip.closeEntry();
                }
                break;
            }
            case DEFLATE: {
                try (DeflaterOutputStream deflate = new DeflaterOutputStream(baos, new Deflater())) {
                    deflate.write(data);
                }
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(algorithm);
        }

        return baos.toByteArray();
    }

    /**
     * Decompresses data using specified algorithm.
     *
     * @param data compressed data
     * @param algorithm compression algorithm
     * @return decompressed data
     * @throws IOException on decompression error
     */
    private byte[] decompress(byte[] data, CompressionAlgorithm algorithm) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        switch (algorithm) {
            case GZIP: {
                try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = gzip.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                }
                break;
            }
            case ZIP: {
                try (ZipInputStream zip = new ZipInputStream(bais)) {
                    ZipEntry entry = zip.getNextEntry();
                    if (entry != null) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zip.read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }
                    }
                }
                break;
            }
            case DEFLATE: {
                try (InflaterInputStream inflate = new InflaterInputStream(bais, new Inflater())) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = inflate.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                }
                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(algorithm);
        }

        return baos.toByteArray();
    }

    /**
     * Detects compression type by examining magic bytes.
     *
     * @param data compressed data
     * @return detected compression algorithm, or null if unknown
     */
    private CompressionAlgorithm detectCompressionType(byte[] data) {
        if (data.length < 2) {
            return null;
        }

        // GZIP magic bytes: 0x1F 0x8B
        if (data[0] == 0x1F && data[1] == (byte) 0x8B) {
            return CompressionAlgorithm.GZIP;
        }

        // ZIP magic bytes: 0x50 0x4B (PK)
        if (data.length >= 4 && data[0] == 0x50 && data[1] == 0x4B) {
            return CompressionAlgorithm.ZIP;
        }

        // DEFLATE doesn't have reliable magic bytes, return default
        return CompressionAlgorithm.DEFLATE;
    }

    @Override
    public void registerPreviewDataHandler(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        CompressionDataPanel panel = (CompressionDataPanel) component;
        panel.setConfigChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(CompressionDataPanel panel, CodeAreaCore codeArea) {
        SwingUtilities.invokeLater(() -> {
            OperationType operationType = panel.getOperationType();
            CompressionAlgorithm algorithm = panel.getAlgorithm();
            boolean autoDetect = panel.isAutoDetect();

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

            // Limit preview data size to avoid performance issues
            length = Math.min(length, 1024 * 1024); // Max 1MB for preview

            convertData(codeArea.getContentData(), position, length, operationType, algorithm, autoDetect, previewBinaryData, 0);

            // Update statistics in panel
            long originalSize = length;
            long compressedSize = previewBinaryData.getDataSize();
            panel.updateStatistics(originalSize, compressedSize);

            long previewDataSize = previewBinaryData.getDataSize();
            if (previewDataSize > previewLengthLimit) {
                previewBinaryData.remove(previewLengthLimit, previewDataSize - previewLengthLimit);
            }
            previewDataHandler.setPreviewData(previewBinaryData);
        });
    }

    /**
     * Operation type enumeration.
     */
    public enum OperationType {
        COMPRESS,
        DECOMPRESS
    }

    /**
     * Compression algorithm enumeration.
     */
    public enum CompressionAlgorithm {
        GZIP,
        ZIP,
        DEFLATE
    }
}
