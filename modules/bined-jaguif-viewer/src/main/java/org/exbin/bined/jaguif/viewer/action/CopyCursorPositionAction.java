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
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.jaguif.viewer.status.StatusCursorPositionFormat;
import org.exbin.bined.jaguif.viewer.status.StatusNumericGrouping;
import org.exbin.bined.jaguif.viewer.status.gui.BinaryCursorPositionComponent;

/**
 * Copy cursor position action.
 */
@NullMarked
public class CopyCursorPositionAction extends AbstractAction {

    public static final String ACTION_ID = "copyCursorPosition";

    private BinaryDataComponent binaryDataComponent;

    public CopyCursorPositionAction() {
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
        BinaryCursorPositionComponent component = binaryDataComponent.getStatusBarComponent(BinaryCursorPositionComponent.class).orElse(null);
        BinedViewerModule viewerModule = App.getModule(BinedViewerModule.class);
        StatusNumericGrouping numericGrouping;
        StatusCursorPositionFormat cursorPositionFormat;
        if (component != null) {
            numericGrouping = component.getNumericGrouping();
            cursorPositionFormat = component.getCursorPositionFormat();
        } else {
            // TODO Load from settings
            numericGrouping = new StatusNumericGrouping();
            cursorPositionFormat = new StatusCursorPositionFormat();
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(viewerModule.getCaretPositionAsText(binaryDataComponent.getCodeArea(), numericGrouping, cursorPositionFormat)), null);
    }
}
