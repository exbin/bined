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
package org.exbin.framework.bined.clipboard.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.clipboard.gui.ClipboardContentPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;

/**
 * Clipboard content action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardContentAction extends AbstractAction {

    public static final String ACTION_ID = "clipboardContentAction";

    private XBApplication application;
    private ResourceBundle resourceBundle;

    public ClipboardContentAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        this.application = application;
        this.resourceBundle = resourceBundle;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        ClipboardContentPanel clipboardContentPanel = new ClipboardContentPanel();
        clipboardContentPanel.loadFromClipboard();
        CloseControlPanel controlPanel = new CloseControlPanel();
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(clipboardContentPanel, controlPanel);
        frameModule.setDialogTitle(dialog, clipboardContentPanel.getResourceBundle());
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        WindowUtils.addHeaderPanel(dialog.getWindow(), clipboardContentPanel.getClass(), clipboardContentPanel.getResourceBundle());
        dialog.showCentered(frameModule.getFrame());
    }
}
