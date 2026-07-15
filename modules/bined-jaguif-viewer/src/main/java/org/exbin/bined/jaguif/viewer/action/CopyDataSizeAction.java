/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.bined.jaguif.viewer.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.jaguif.viewer.status.StatusDataSizeFormat;
import org.exbin.bined.jaguif.viewer.status.StatusNumericGrouping;
import org.exbin.bined.jaguif.viewer.status.gui.BinaryDataSizeComponent;

/**
 * Copy data size action.
 */
@NullMarked
public class CopyDataSizeAction extends AbstractAction {

    public static final String ACTION_ID = "copyDataSize";

    private BinaryDataComponent binaryDataComponent;

    public CopyDataSizeAction() {
    }

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.PUSH);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (instance) -> {
                    binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                    setEnabled(binaryDataComponent != null);
                });
            }
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BinaryDataSizeComponent component = binaryDataComponent.getStatusBarComponent(BinaryDataSizeComponent.class).orElse(null);
        BinedViewerModule viewerModule = App.getModule(BinedViewerModule.class);
        StatusNumericGrouping numericGrouping;
        StatusDataSizeFormat dataSizeFormat;
        long originalDataSize;
        if (component != null) {
            numericGrouping = component.getNumericGrouping();
            dataSizeFormat = component.getDataSizeFormat();
            originalDataSize = component.getOriginalDataSize();
        } else {
            // TODO Load from settings
            numericGrouping = new StatusNumericGrouping();
            dataSizeFormat = new StatusDataSizeFormat();
            originalDataSize = 0;
        }

        long dataSize = binaryDataComponent.getCodeArea().getDataSize();
        SelectionRange selectionRange = ((SelectionCapable) binaryDataComponent.getCodeArea()).getSelection();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(viewerModule.getDataSizeAsText(dataSize, originalDataSize, selectionRange, numericGrouping, dataSizeFormat)), null);
    }
}
