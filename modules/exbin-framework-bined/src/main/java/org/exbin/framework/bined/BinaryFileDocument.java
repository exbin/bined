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

import java.net.URI;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.document.api.ComponentDocument;
import org.exbin.framework.file.api.FileDocument;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;

/**
 * BinEd binary document.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryFileDocument implements BinaryDocument, ComponentDocument, FileDocument {

    protected BinEdComponentPanel componentPanel = createComponentPanel();
    protected URI fileUri = null;
    protected BinaryData binaryData;

    public BinaryFileDocument() {
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.ofNullable(fileUri);
    }

    @Nonnull
    @Override
    public BinaryData getBinaryData() {
        return binaryData;
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponent() {
        return componentPanel;
    }

    @Nonnull
    protected BinEdComponentPanel createComponentPanel() {
        BinEdComponentPanel componentPanel = new BinEdComponentPanel();
        return componentPanel;
    }

    @Nonnull
    public SectCodeArea getCodeArea() {
        return componentPanel.getCodeArea();
    }

    @Nonnull
    public Optional<BinaryDataUndoRedo> getUndoHandler() {
        return componentPanel.getUndoRedo();
    }

    public void setUndoHandler(BinaryDataUndoRedo undoHandler) {
        componentPanel.setUndoRedo(undoHandler);
    }

    @Nonnull
    public BinaryData getContentData() {
        SectCodeArea codeArea = componentPanel.getCodeArea();
        return codeArea.getContentData();
    }

    public void setContentData(@Nullable BinaryData data) {
        SectCodeArea codeArea = componentPanel.getCodeArea();
        codeArea.setContentData(data);
    }
    
    public void reloadFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
