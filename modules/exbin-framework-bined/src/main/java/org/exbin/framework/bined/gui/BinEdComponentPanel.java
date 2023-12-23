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
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.operation.undo.EmptyBinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.framework.bined.BinEdCodeAreaPainter;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.utils.WindowUtils;

/**
 * Binary editor component panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel {

    private ExtCodeArea codeArea;
    private BinaryDataUndoHandler undoHandler;
    private final List<BinEdComponentExtension> componentExtensions = new ArrayList<>();

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = new ExtCodeArea();
        codeArea.setPainter(new BinEdCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, new EmptyBinaryDataUndoHandler());
        codeArea.setCommandHandler(commandHandler);

        add(codeArea);
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    public void notifyDataChanged() {
        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onDataChange();
        }
    }

    public void onInitFromPreferences(BinaryEditorPreferences preferences) {
        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onInitFromPreferences(preferences);
        }
    }
            
    public void addComponentExtension(BinEdComponentExtension extension) {
        componentExtensions.add(extension);
        if (undoHandler != null) {
            extension.onUndoHandlerChange();
        }
    }

    @Nonnull
    public List<BinEdComponentExtension> getComponentExtensions() {
        return componentExtensions;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz) {
        for (BinEdComponentExtension extension : componentExtensions) {
            if (clazz.isInstance(extension)) {
                return (T) extension;
            }
        }
        throw new IllegalStateException();
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
    public BinaryDataUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void setUndoHandler(BinaryDataUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler == null ? new CodeAreaUndoHandler(codeArea) : undoHandler);
        codeArea.setCommandHandler(commandHandler);

        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onUndoHandlerChange();
        }
        // TODO set ENTER KEY mode in apply options

    }

    public void setPopupMenu(JPopupMenu menu) {
        codeArea.setComponentPopupMenu(menu);
    }

    @Nonnull
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

        void setApplication(XBApplication application);

        void onCreate(BinEdComponentPanel componentPanel);
        
        void onInitFromPreferences(BinaryEditorPreferences preferences);

        void onDataChange();

        void onClose();

        public void onUndoHandlerChange();
    }
}
