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
package org.exbin.framework.bined.action;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.file.api.FileDependentAction;
import org.exbin.framework.file.api.FileHandler;

/**
 * Print action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PrintAction extends AbstractAction implements FileDependentAction {

    public static final String ACTION_ID = "printAction";

    private EditorProvider editorProvider;
    private XBApplication application;
    private ResourceBundle resourceBundle;

    public PrintAction() {
    }

    public void setup(XBApplication application, EditorProvider editorProvider, ResourceBundle resourceBundle) {
        this.application = application;
        this.editorProvider = editorProvider;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, ActionUtils.getMetaMask()));
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void updateForActiveFile() {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        setEnabled(activeFile.isPresent());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Optional<FileHandler> activeFile = editorProvider.getActiveFile();
        if (!activeFile.isPresent()) {
            throw new IllegalStateException();
        }

        ExtCodeArea codeArea = ((BinEdFileHandler) activeFile.get()).getCodeArea();
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
//                PrintJob myJob = imageArea.getToolkit().getPrintJob(null, fileName, null);
//                if (myJob != null) {
                job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
                    codeArea.print(graphics);
                    if (pageIndex == 0) {
                        return Printable.PAGE_EXISTS;
                    }
                    return Printable.NO_SUCH_PAGE;
                });
                job.print();
//                }
            } catch (PrinterException ex) {
                Logger.getLogger(PrintAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
