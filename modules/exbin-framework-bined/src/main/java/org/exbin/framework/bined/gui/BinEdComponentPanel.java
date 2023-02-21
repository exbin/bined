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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.FocusListener;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.bined.service.impl.BinarySearchServiceImpl;

/**
 * Binary editor component panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel {

    private ExtCodeArea codeArea;
    private CodeAreaUndoHandler undoHandler;

    private BinarySearchPanel binarySearchPanel;
    private boolean binarySearchPanelVisible = false;
    private ValuesPanel valuesPanel;
    private boolean parsingPanelVisible = false;

    private JScrollPane valuesPanelScrollPane;

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // TODO: Use empty undo handler instead
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, new CodeAreaUndoHandler(codeArea));
        codeArea.setCommandHandler(commandHandler);

        add(codeArea);

        binarySearchPanel = new BinarySearchPanel();
        binarySearchPanel.setBinarySearchService(new BinarySearchServiceImpl(codeArea));
        binarySearchPanel.setClosePanelListener(this::hideSearchPanel);

        valuesPanel = new ValuesPanel();
        valuesPanel.setCodeArea(codeArea, null);
        valuesPanelScrollPane = new JScrollPane(valuesPanel);
        valuesPanelScrollPane.setBorder(null);
        setShowParsingPanel(true);
    }

    public void setApplication(XBApplication application) {
        binarySearchPanel.setApplication(application);
    }

    public void showSearchPanel(boolean replace) {
        if (!binarySearchPanelVisible) {
            add(binarySearchPanel, BorderLayout.SOUTH);
            revalidate();
            binarySearchPanelVisible = true;
            binarySearchPanel.requestSearchFocus();
        }
        binarySearchPanel.switchReplaceMode(replace);
    }

    public void hideSearchPanel() {
        if (binarySearchPanelVisible) {
            binarySearchPanel.cancelSearch();
            binarySearchPanel.clearSearch();
            BinEdComponentPanel.this.remove(binarySearchPanel);
            BinEdComponentPanel.this.revalidate();
            binarySearchPanelVisible = false;
        }
    }

    public void setShowParsingPanel(boolean show) {
        if (parsingPanelVisible != show) {
            if (show) {
                add(valuesPanelScrollPane, BorderLayout.EAST);
                revalidate();
                parsingPanelVisible = true;
                valuesPanel.enableUpdate();
            } else {
                valuesPanel.disableUpdate();
                BinEdComponentPanel.this.remove(valuesPanelScrollPane);
                BinEdComponentPanel.this.revalidate();
                parsingPanelVisible = false;
            }
        }
    }

    public boolean isShowParsingPanel() {
        return parsingPanelVisible;
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    public boolean changeRowWrapping() {
        ((RowWrappingCapable) codeArea).setRowWrapping(((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING ? RowWrappingMode.NO_WRAPPING : RowWrappingMode.WRAPPING);
        return ((RowWrappingCapable) codeArea).getRowWrapping() == RowWrappingMode.WRAPPING;
    }

    public void findAgain() {
        // TODO hexSearchPanel.f
    }

    public void notifyDataChanged() {
        if (binarySearchPanelVisible) {
            binarySearchPanel.dataChanged();
        }
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
}
