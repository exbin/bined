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
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.blockedit.component.gui.RandomDataPanel;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.bined.blockedit.api.InsertDataMethod;
import org.exbin.framework.bined.blockedit.operation.DataOperationDataProvider;
import org.exbin.framework.bined.blockedit.operation.InsertDataOperation;
import org.exbin.framework.bined.blockedit.operation.ReplaceDataOperation;

/**
 * Random data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RandomDataMethod implements InsertDataMethod {

    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(RandomDataPanel.class);

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

        DataOperationDataProvider dataOperationDataProvider = (EditableBinaryData binaryData) -> {
            RandomDataPanel.AlgorithmType algorithmType = panel.getAlgorithmType();
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
        };

        if (editOperation == EditOperation.OVERWRITE) {
            return new ReplaceDataOperation.ReplaceDataCommand(new ReplaceDataOperation(codeArea, position, length, dataOperationDataProvider));
        } else {
            return new InsertDataOperation.InsertDataCommand(new InsertDataOperation(codeArea, position, length, dataOperationDataProvider));
        }
    }

    @Override
    public void setPreviewDataTarget(Component component, EditableBinaryData binaryData, long lengthLimit) {
        this.previewBinaryData = binaryData;
        this.previewLengthLimit = lengthLimit;
        fillPreviewData(component);
//        component.addPreviewChangeListener();
    }
    
    private void fillPreviewData(Component component) {
        
    } 
}
