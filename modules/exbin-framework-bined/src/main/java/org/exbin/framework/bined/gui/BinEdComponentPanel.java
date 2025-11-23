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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoRedo;
import org.exbin.bined.operation.command.EmptyBinaryDataUndoRedo;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.framework.bined.BinEdCodeAreaAssessor;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.capability.CharAssessorPainterCapable;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;

/**
 * Binary editor component panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdComponentPanel extends javax.swing.JPanel {

    private SectCodeArea codeArea;
    private BinaryDataUndoRedo undoRedo;
    private final List<BinEdComponentExtension> componentExtensions = new ArrayList<>();

    public BinEdComponentPanel() {
        initComponents();
        init();
    }

    private void init() {
        codeArea = createCodeArea();
        CodeAreaPainter painter = codeArea.getPainter();
        BinEdCodeAreaAssessor codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable) painter).getColorAssessor(), ((CharAssessorPainterCapable) painter).getCharAssessor());
        ((ColorAssessorPainterCapable) painter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable) painter).setCharAssessor(codeAreaAssessor);
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, new EmptyBinaryDataUndoRedo());
        codeArea.setCommandHandler(commandHandler);

        String popupMenuId = BinedModule.BINARY_POPUP_MENU_ID + ".multi";

        JPopupMenu codeAreaPopupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                if (invoker == null) {
                    return;
                }
                
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }

                BinedModule binedModule = App.getModule(BinedModule.class);
                CodeAreaPopupMenuHandler codeAreaPopupMenuHandler = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.EDITOR);
                JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                popupMenu.show(invoker, x, y);
            }
        };
        // TODO
        /*MenuPopupModuleApi menuPopupModule = App.getModule(MenuPopupModuleApi.class);
        codeArea.setComponentPopupMenu(menuPopupModule.createComponentPopupMenu(BinedModule.CODE_AREA_POPUP_MENU_ID, () -> {
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            return actionModule.createActionContextRegistrar(frameModule.getFrameHandler().getActionManager());
        })); */
        codeArea.setComponentPopupMenu(codeAreaPopupMenu);
        add(codeArea);
    }

    @Nonnull
    protected SectCodeArea createCodeArea() {
        return new SectCodeArea();
    }

    @Nonnull
    public SectCodeArea getCodeArea() {
        return codeArea;
    }

    public void notifyDataChanged() {
        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onDataChange();
        }
    }

    /* public void onInitFromPreferences(OptionsStorage options) {
        org.exbin.framework.bined.settings.FontSizeOptions fontSizeOptions =
            new org.exbin.framework.bined.settings.FontSizeOptions(options);
        int fontSize = fontSizeOptions.getFontSize();
        Font currentFont = codeArea.getCodeFont();
        codeArea.setCodeFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize));
    } */
    public void addComponentExtension(BinEdComponentExtension extension) {
        componentExtensions.add(extension);
        if (undoRedo != null) {
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Nonnull
    public Optional<BinaryDataUndoRedo> getUndoRedo() {
        return Optional.ofNullable(undoRedo);
    }

    public void setUndoRedo(BinaryDataUndoRedo undoRedo) {
        this.undoRedo = undoRedo;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoRedo == null ? new CodeAreaUndoRedo(codeArea) : undoRedo);
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

    @Nonnull
    public <T extends BinEdComponentExtension> Optional<T> getBinEdComponentExtensions(Class<T> clazz) {
        for (BinEdComponentExtension binEdComponentExtension : componentExtensions) {
            if (clazz.isInstance(binEdComponentExtension)) {
                return Optional.of(clazz.cast(binEdComponentExtension));
            }
        }

        return Optional.empty();
    }

    @ParametersAreNonnullByDefault
    public interface BinEdComponentExtension {

        void onCreate(BinEdComponentPanel componentPanel);

        void onDataChange();

        void onClose();

        void onUndoHandlerChange();
    }
}
