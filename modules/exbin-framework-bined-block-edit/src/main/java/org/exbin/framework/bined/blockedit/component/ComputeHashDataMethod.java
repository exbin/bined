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
package org.exbin.framework.bined.blockedit.component;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.blockedit.api.ConvertDataMethod;
import org.exbin.framework.bined.blockedit.component.gui.ComputeHashDataPanel;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.bined.blockedit.operation.DataOperationDataProvider;
import org.exbin.framework.bined.blockedit.operation.InsertDataOperation;
import org.exbin.framework.bined.blockedit.operation.ReplaceDataOperation;

/**
 * Compute CRC data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ComputeHashDataMethod implements ConvertDataMethod {

    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(ComputeHashDataPanel.class);

    private XBApplication application;
    private EditableBinaryData previewBinaryData;
    private long previewLengthLimit = 0;

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("component.name");
    }

    @Nonnull
    @Override
    public Component getComponent() {
        ComputeHashDataPanel component = new ComputeHashDataPanel();
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((ComputeHashDataPanel) component).initFocus();
    }

    @Nonnull
    @Override
    public CodeAreaCommand createConvertCommand(Component component, CodeAreaCore codeArea, long position, EditOperation editOperation) {
        ComputeHashDataPanel panel = (ComputeHashDataPanel) component;
        ComputeHashDataPanel.HashType hashType = panel.getHashType();

        DataOperationDataProvider dataOperationDataProvider = (EditableBinaryData binaryData) -> {
            generateData(binaryData, hashType, position);
        };

        // TODO
        long length = 0;
        if (editOperation == EditOperation.OVERWRITE) {
            return new ReplaceDataOperation.ReplaceDataCommand(new ReplaceDataOperation(codeArea, position, length, dataOperationDataProvider));
        } else {
            return new InsertDataOperation.InsertDataCommand(new InsertDataOperation(codeArea, position, length, dataOperationDataProvider));
        }
    }

    public void generateData(EditableBinaryData binaryData, ComputeHashDataPanel.HashType hashType, long position) throws IllegalStateException {
/*        switch (hashType) {
            case EMPTY: {
                for (long pos = position; pos < position + length; pos++) {
                    binaryData.setByte(pos, (byte) 0x0);
                }
                break;
            }
            case SPACE: {
                for (long pos = position; pos < position + length; pos++) {
                    binaryData.setByte(pos, (byte) 0x20);
                }
                break;
            }
            case SAMPLE: {
                if (sampleBinaryData.isEmpty()) {
                    for (long pos = position; pos < position + length; pos++) {
                        binaryData.setByte(pos, (byte) 0xFF);
                    }
                } else {
                    long dataSizeLimit = sampleBinaryData.getDataSize();
                    long pos = position;
                    long remain = length;
                    while (remain > 0) {
                        long seg = Math.min(remain, dataSizeLimit);
                        binaryData.replace(pos, sampleBinaryData, 0, seg);
                        pos += seg;
                        remain -= seg;
                    }
                }

                break;
            }
            default:
                throw CodeAreaUtils.getInvalidTypeException(hashType);
        } */
    }

    @Override
    public void setPreviewDataTarget(Component component, BinaryData sourceBinaryData, EditableBinaryData targetBinaryData, long lengthLimit) {
        this.previewBinaryData = targetBinaryData;
        this.previewLengthLimit = lengthLimit;
        ComputeHashDataPanel panel = (ComputeHashDataPanel) component;
        panel.setModeChangeListener(() -> {
            fillPreviewData(panel);
        });
        fillPreviewData(panel);
    }

    private void fillPreviewData(ComputeHashDataPanel panel) {
        SwingUtilities.invokeLater(() -> {
            ComputeHashDataPanel.HashType hashType = panel.getHashType();
            // TODO
//            long dataLength = panel.getDataLength();
//            if (dataLength > previewLengthLimit) {
//                dataLength = previewLengthLimit;
//            }
//            EditableBinaryData sampleBinaryData = panel.getSampleBinaryData();

            previewBinaryData.clear();
//            previewBinaryData.insertUninitialized(0, dataLength);
//            generateData(previewBinaryData, fillWithType, 0, dataLength, sampleBinaryData);
        });
    }
}
