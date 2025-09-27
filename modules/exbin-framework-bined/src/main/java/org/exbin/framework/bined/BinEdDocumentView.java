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

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.ui.api.Document;
import org.exbin.framework.ui.api.DocumentView;

/**
 * BinEd binary document view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDocumentView implements DocumentView {

    protected BinEdDocument document;
    protected BinEdComponentPanel componentPanel = createComponentPanel();

    @Nonnull
    protected BinEdComponentPanel createComponentPanel() {
        return new BinEdComponentPanel();
    }

    @Nonnull
    @Override
    public BinEdDocument getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = (BinEdDocument) document;
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
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
}
