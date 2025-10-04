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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import org.exbin.framework.bined.operation.component.gui.DateTimeConversionPanel;
import org.exbin.framework.bined.operation.ConversionDataProvider;
import org.exbin.framework.bined.operation.command.ConvertDataCommand;
import org.exbin.framework.bined.operation.ConvertDataOperation;


@ParametersAreNonnullByDefault
public class DateTimeConversionMethod implements ConvertDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DateTimeConversionPanel.class);

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
        DateTimeConversionPanel component = new DateTimeConversionPanel();
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((DateTimeConversionPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea) {
        DateTimeConversionPanel panel = (DateTimeConversionPanel) component;
        ConversionConfig config = panel.getConversionConfig();

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
            convertData(binaryData, sourcePosition, sourceLength, config, binaryData, targetPosition);
        };

        return new ConvertDataCommand(codeArea, new ConvertDataOperation(position, length, length, conversionDataProvider));
    }

    @Override
    public BinaryData performDirectConvert(Component component, CodeAreaCore codeArea) {
        DateTimeConversionPanel panel = (DateTimeConversionPanel) component;
        ConversionConfig config = panel.getConversionConfig();

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
        convertData(codeArea.getContentData(), position, length, config, binaryData, 0);
        return binaryData;
    }

    /**
     * Converts binary data to/from date/time representation.
     *
     * @param sourceBinaryData source binary data
     * @param position starting position
     * @param length data length
     * @param config conversion configuration
     * @param targetBinaryData target binary data
     * @param targetPosition target position
     * @throws IllegalStateException on conversion error
     */
    public void convertData(BinaryData sourceBinaryData, long position, long length, ConversionConfig config,
                            EditableBinaryData targetBinaryData, long targetPosition) throws IllegalStateException {

        // Validate data length
        int expectedLength = (config.dataSize == DataSize.INT_32) ? 4 : 8;
        if (length < expectedLength) {
            String errorMsg = String.format("Error: Need %d bytes for %s timestamp, got %d bytes",
                    expectedLength, config.dataSize == DataSize.INT_32 ? "32-bit" : "64-bit", length);
            byte[] output = errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            targetBinaryData.insert(targetPosition, output);
            return;
        }

        // Only read the expected number of bytes
        byte[] sourceData = new byte[expectedLength];
        sourceBinaryData.copyToArray(position, sourceData, 0, expectedLength);

        try {
            // Parse timestamp from binary data
            long timestamp = parseTimestamp(sourceData, config);

            // Convert timestamp to formatted string
            String dateTimeString = formatDateTime(timestamp, config);

            // Write formatted string as UTF-8 bytes
            byte[] output = dateTimeString.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            targetBinaryData.insert(targetPosition, output);

        } catch (Exception ex) {
            // Handle invalid data gracefully
            String errorMsg = "Invalid timestamp data: " + ex.getMessage();
            byte[] output = errorMsg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            targetBinaryData.insert(targetPosition, output);
        }
    }

    /**
     * Parses timestamp from binary data according to configuration.
     *
     * @param data binary data
     * @param config conversion configuration
     * @return timestamp value
     */
    private long parseTimestamp(byte[] data, ConversionConfig config) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Set byte order
        if (config.byteOrder == ByteOrderType.BIG_ENDIAN) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        // Parse according to data size
        long timestamp;
        if (config.dataSize == DataSize.INT_32) {
            timestamp = buffer.getInt() & 0xFFFFFFFFL; // unsigned int
        } else {
            timestamp = buffer.getLong();
        }

        // Convert milliseconds to seconds if needed
        if (config.timeUnit == TimeUnit.MILLISECONDS) {
            timestamp = timestamp / 1000;
        }

        return timestamp;
    }

    /**
     * Formats timestamp to human-readable date/time string.
     *
     * @param timestamp Unix timestamp in seconds
     * @param config conversion configuration
     * @return formatted date/time string
     */
    private String formatDateTime(long timestamp, ConversionConfig config) {
        Instant instant = Instant.ofEpochSecond(timestamp);

        // Format in UTC
        LocalDateTime dateTimeUTC = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format in local timezone
        LocalDateTime dateTimeLocal = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        String localZone = ZoneId.systemDefault().getId();

        // Build result with comprehensive information
        StringBuilder result = new StringBuilder();
        result.append("=== Timestamp Conversion Result ===\n");
        result.append("Unix Timestamp: ").append(timestamp).append(" seconds\n");
        result.append("UTC Time:   ").append(dateTimeUTC.format(formatter)).append(" (UTC)\n");
        result.append("Local Time: ").append(dateTimeLocal.format(formatter)).append(" (").append(localZone).append(")\n");
        result.append("ISO 8601:   ").append(instant.toString()).append("\n");
        result.append("\nConfiguration:\n");
        result.append("- Byte Order: ").append(config.byteOrder).append("\n");
        result.append("- Data Size:  ").append(config.dataSize == DataSize.INT_32 ? "32-bit" : "64-bit").append("\n");
        result.append("- Time Unit:  ").append(config.timeUnit).append("\n");

        return result.toString();
    }

    @Override
    public void registerPreviewDataHandler(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        DateTimeConversionPanel panel = (DateTimeConversionPanel) component;
        panel.setConfigChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(DateTimeConversionPanel panel, CodeAreaCore codeArea) {
        SwingUtilities.invokeLater(() -> {
            ConversionConfig config = panel.getConversionConfig();

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

            // Limit length to data size requirement
            if (config.dataSize == DataSize.INT_32) {
                length = Math.min(length, 4);
            } else {
                length = Math.min(length, 8);
            }

            convertData(codeArea.getContentData(), position, length, config, previewBinaryData, 0);
            long previewDataSize = previewBinaryData.getDataSize();
            if (previewDataSize > previewLengthLimit) {
                previewBinaryData.remove(previewLengthLimit, previewDataSize - previewLengthLimit);
            }
            previewDataHandler.setPreviewData(previewBinaryData);
        });
    }

    /**
     * Byte order type enumeration.
     */
    public enum ByteOrderType {
        BIG_ENDIAN,
        LITTLE_ENDIAN
    }

    /**
     * Data size enumeration.
     */
    public enum DataSize {
        INT_32,
        LONG_64
    }

    /**
     * Time unit enumeration.
     */
    public enum TimeUnit {
        SECONDS,
        MILLISECONDS
    }

    /**
     * Conversion configuration holder.
     */
    public static class ConversionConfig {
        public ByteOrderType byteOrder;
        public DataSize dataSize;
        public TimeUnit timeUnit;

        public ConversionConfig(ByteOrderType byteOrder, DataSize dataSize, TimeUnit timeUnit) {
            this.byteOrder = byteOrder;
            this.dataSize = dataSize;
            this.timeUnit = timeUnit;
        }
    }
}
