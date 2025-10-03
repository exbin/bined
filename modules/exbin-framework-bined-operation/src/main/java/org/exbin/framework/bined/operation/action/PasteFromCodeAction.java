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
package org.exbin.framework.bined.operation.action;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.command.InsertDataCommand;
import org.exbin.bined.swing.CodeAreaCommandHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.operation.component.api.CodeImportFormat;
import org.exbin.framework.bined.operation.component.format.JavaByteArrayParser;
import org.exbin.framework.bined.operation.component.format.PythonBytesParser;
import org.exbin.framework.bined.operation.component.format.CHexArrayParser;
import org.exbin.framework.bined.operation.gui.PasteFromCodePanel;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Paste from code action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PasteFromCodeAction extends AbstractAction {

    public static final String ACTION_ID = "pasteFromCodeAction";

    private CodeAreaCore codeArea;
    private final List<CodeImportFormat> importFormats;

    public PasteFromCodeAction() {
        // Initialize available import formats
        importFormats = Arrays.asList(
            new JavaByteArrayParser(),
            new PythonBytesParser(),
            new CHexArrayParser()
        );
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        // Temporarily disable shortcut to avoid conflicts
        // putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ActionUtils.getMetaMask() | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    boolean hasInstance = instance != null;
                    setEnabled(hasInstance && codeArea.isEditable());
                });
            }
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("PasteFromCodeAction triggered!"); // Debug

        // Create panel
        final PasteFromCodePanel pasteFromCodePanel = new PasteFromCodePanel();
        pasteFromCodePanel.setImportFormats(importFormats);

        // Create control panel
        ResourceBundle panelResourceBundle = pasteFromCodePanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        JPanel dialogPanel = windowModule.createDialogPanel(pasteFromCodePanel, controlPanel);

        // Create dialog
        final WindowHandler dialog = windowModule.createWindow(dialogPanel, codeArea, "Paste from Code", Dialog.ModalityType.APPLICATION_MODAL);
        // Don't add header panel or set title from resource bundle - title already set in createWindow
        // windowModule.addHeaderPanel(dialog.getWindow(), pasteFromCodePanel.getClass(), panelResourceBundle);
        // windowModule.setWindowTitle(dialog, panelResourceBundle);

        // Set controller
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            System.out.println("PasteFromCodeAction: Control action: " + actionType); // Debug
            if (actionType == DefaultControlController.ControlActionType.OK) {
                // Insert parsed binary data
                BinaryData parsedData = pasteFromCodePanel.getParsedData();
                System.out.println("PasteFromCodeAction: Parsed data: " + (parsedData != null ? parsedData.getDataSize() + " bytes" : "null")); // Debug
                if (parsedData != null && parsedData.getDataSize() > 0) {
                    insertData(parsedData);
                    System.out.println("PasteFromCodeAction: Data inserted"); // Debug
                } else {
                    System.out.println("PasteFromCodeAction: No data to insert"); // Debug
                }
            }

            System.out.println("PasteFromCodeAction: Closing dialog"); // Debug
            dialog.close();
            dialog.dispose();
        });

        dialog.showCentered(codeArea);
    }

    private void insertData(BinaryData data) {
        if (codeArea == null || data == null) {
            return;
        }

        long position = ((CaretCapable) codeArea).getDataPosition();

        // Debug: Print data being inserted
        System.out.println("PasteFromCodeAction: Inserting at position: " + position);
        StringBuilder hexDump = new StringBuilder("Data to insert (hex): ");
        for (long i = 0; i < Math.min(data.getDataSize(), 50); i++) {
            hexDump.append(String.format("%02X ", data.getByte(i)));
        }
        System.out.println(hexDump);

        // Create copy of data
        ByteArrayEditableData insertData = new ByteArrayEditableData();
        insertData.insert(0, data);

        // Create and execute insert command
        InsertDataCommand command = new InsertDataCommand(codeArea, position, 0, insertData);

        CodeAreaCommandHandler commandHandler = codeArea.getCommandHandler();
        if (commandHandler instanceof CodeAreaOperationCommandHandler) {
            ((CodeAreaOperationCommandHandler) commandHandler).getUndoRedo().execute(command);
        } else {
            command.execute();
        }

        // Move cursor to end of inserted data
        long newPosition = position + data.getDataSize();
        ((CaretCapable) codeArea).setActiveCaretPosition(newPosition);
        System.out.println("PasteFromCodeAction: Moved cursor to position: " + newPosition);

        // Force refresh
        codeArea.repaint();
        System.out.println("PasteFromCodeAction: Data insertion completed");
    }
}
