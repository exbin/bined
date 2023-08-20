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
package org.exbin.framework.bined;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;

/**
 * Component for BinEd editor instances.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdEditorComponent {

    private BinEdComponentPanel componentPanel = new BinEdComponentPanel();

    public BinEdEditorComponent() {
    }

    public void setApplication(XBApplication application) {
        
    };

    @Nonnull
    public BinEdComponentPanel getComponentPanel() {
        return componentPanel;
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return componentPanel.getCodeArea();
    }

    @Nullable
    public BinaryDataUndoHandler getUndoHandler() {
        return componentPanel.getUndoHandler();
    }

    public void setUndoHandler(BinaryDataUndoHandler undoHandler) {
        componentPanel.setUndoHandler(undoHandler);
//        ExtCodeArea codeArea = componentPanel.getCodeArea();
//        toolbarPanel.setUndoHandler(undoHandler);
//        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
//        codeArea.setCommandHandler(commandHandler);
//        if (valuesPanel != null) {
//            valuesPanel.setCodeArea(codeArea, undoHandler);
//        }
////        insertDataAction.setUndoHandler(undoHandler);
//        // TODO set ENTER KEY mode in apply options
//
//        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
//            @Override
//            public void undoCommandPositionChanged() {
//                codeArea.repaint();
//                updateCurrentDocumentSize();
//                notifyModified();
//            }
//
//            @Override
//            public void undoCommandAdded(@Nonnull final BinaryDataCommand command) {
//                updateCurrentDocumentSize();
//                notifyModified();
//            }
//        });
    }

    @Nullable
    public BinaryData getContentData() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        return codeArea.getContentData();
    }

    public void setContentData(@Nullable BinaryData data) {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        codeArea.setContentData(data);

//        documentOriginalSize = codeArea.getDataSize();
//        updateCurrentDocumentSize();
//        updateCurrentMemoryMode();

        // Autodetect encoding using IDE mechanism
        //        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
        //        if (charsetChangeListener != null) {
        //            charsetChangeListener.charsetChanged();
        //        }
        //        codeArea.setCharset(charset);
    }

}