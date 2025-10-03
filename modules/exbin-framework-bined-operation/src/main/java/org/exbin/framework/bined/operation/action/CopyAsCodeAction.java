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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.bined.operation.component.api.CodeExportFormat;
import org.exbin.framework.bined.operation.component.format.JavaByteArrayFormat;
import org.exbin.framework.bined.operation.component.format.PythonBytesFormat;
import org.exbin.framework.bined.operation.component.format.CHexArrayFormat;
import org.exbin.framework.bined.operation.gui.CopyAsCodePanel;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;


@ParametersAreNonnullByDefault
public class CopyAsCodeAction extends AbstractAction {

    public static final String ACTION_ID = "copyAsCodeAction";

    private CodeAreaCore codeArea;
    private final List<CodeExportFormat> exportFormats;

    public CopyAsCodeAction() {
        // Initialize available export formats
        exportFormats = Arrays.asList(
            new JavaByteArrayFormat(),
            new PythonBytesFormat(),
            new CHexArrayFormat()
        );
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeManager manager) {
                manager.registerUpdateListener(ActiveComponent.class, (instance) -> {
                    codeArea = instance instanceof BinaryDataComponent ? ((BinaryDataComponent) instance).getCodeArea() : null;
                    boolean hasInstance = instance != null;
                    setEnabled(hasInstance);
                });
            }
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Get selected data
        BinaryData sourceData = getSelectedData();
        // Don't return early - always show dialog even if no data

        // Create panel
        final CopyAsCodePanel copyAsCodePanel = new CopyAsCodePanel();
        copyAsCodePanel.setExportFormats(exportFormats);
        if (sourceData != null && !sourceData.isEmpty()) {
            copyAsCodePanel.setSourceData(sourceData);
        }

        // Create control panel
        ResourceBundle panelResourceBundle = copyAsCodePanel.getResourceBundle();
        DefaultControlPanel controlPanel = new DefaultControlPanel(panelResourceBundle);

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        JPanel dialogPanel = windowModule.createDialogPanel(copyAsCodePanel, controlPanel);

        // Create dialog
        final WindowHandler dialog = windowModule.createWindow(dialogPanel, codeArea, "Copy as Code", Dialog.ModalityType.APPLICATION_MODAL);
        // Don't add header panel or set title from resource bundle - title already set in createWindow

        // Set controller
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType == DefaultControlController.ControlActionType.OK) {
                // Copy generated code to clipboard
                String code = copyAsCodePanel.getGeneratedCode();
                StringSelection stringSelection = new StringSelection(code);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }

            dialog.close();
            dialog.dispose();
        });

        dialog.showCentered(codeArea);
    }

    private BinaryData getSelectedData() {
        if (codeArea == null) {
            return null;
        }

        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        long position;
        long length;

        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }

        if (length == 0) {
            return null;
        }

        ByteArrayEditableData data = new ByteArrayEditableData();
        data.insert(0, codeArea.getContentData(), position, length);
        return data;
    }
}
