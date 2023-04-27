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
package org.exbin.framework.bined.gui;

import java.awt.Font;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.framework.bined.BinEdCodeAreaPainter;
import org.exbin.framework.utils.WindowUtils;

/**
 * Binary editor component panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel {

    private ExtCodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;
    private List<BinEdComponentExtension> componentExtensions = new ArrayList<>();

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new BinEdCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // TODO: Use empty undo handler instead
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, new CodeAreaUndoHandler(codeArea));
        codeArea.setCommandHandler(commandHandler);

        add(codeArea);
    }

    public void setApplication(XBApplication application) {
        binarySearchPanel.setApplication(application);
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    public boolean changeRowWrapping() {
        ((RowWrappingCapable) codeArea).setRowWrapping(((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING ? RowWrappingMode.NO_WRAPPING : RowWrappingMode.WRAPPING);
        return ((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING;
    }

    public void notifyDataChanged() {
        for (BinEdComponentExtension extension: componentExtensions) {
            extension.onDataChange();
        }
    }

    public void addComponentExtension(BinEdComponentExtension extension) {
        componentExtensions.add(extension);
    }

    @Nonnull
    public List<BinEdComponentExtension> getComponentExtensions() {
        return componentExtensions;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new BinEdComponentPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Nullable
    public CodeAreaUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setUndoHandler(CodeAreaUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler == null ? new CodeAreaUndoHandler(codeArea) : undoHandler);
        codeArea.setCommandHandler(commandHandler);
        if (valuesPanel != null) {
            valuesPanel.setCodeArea(codeArea, undoHandler);
        }
        // TODO set ENTER KEY mode in apply options

    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
    }

    public void setCodeAreaPopupMenuHandler(CodeAreaPopupMenuHandler codeAreaPopupMenuHandler) {
        binarySearchPanel.setCodeAreaPopupMenuHandler(codeAreaPopupMenuHandler);
    }

    @Nullable
    public BinaryData getContentData() {
        return codeArea.getContentData();
    }

    public void setContentData(BinaryData data) {
        codeArea.setContentData(data);
    }

    public void addBinaryAreaFocusListener(FocusListener focusListener) {
        codeArea.addFocusListener(focusListener);
    }

    public void removeBinaryAreaFocusListener(FocusListener focusListener) {
        codeArea.removeFocusListener(focusListener);
    }

    @ParametersAreNonnullByDefault
    public interface BinEdComponentExtension {

        void onCreate(BinEdComponentPanel componentPanel);

        void onDataChange();

        void onClose();
    }
}
