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
package org.exbin.bined.jaguif.viewer.status.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.viewer.status.StatusCursorPositionFormat;
import org.exbin.bined.jaguif.viewer.status.StatusNumericGrouping;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * Binary data cursor position status component.
 */
@NullMarked
public class BinaryCursorPositionComponent extends AbstractStatusBarComponent {

    protected static final String BR_TAG = "<br>";

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryCursorPositionComponent.class);

    protected StatusNumericGrouping numericGrouping = new StatusNumericGrouping();
    protected StatusCursorPositionFormat cursorPositionFormat;
    protected BinedViewerModule viewerModule;

    protected BinaryDataComponent binaryDataComponent;

    public BinaryCursorPositionComponent() {
        viewerModule = App.getModule(BinedViewerModule.class);

        OptionsSettingsModuleApi optionsSettings = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsProvider settingsOptionsProvider = optionsSettings.getMainSettingsManager().getSettingsOptionsProvider();
        CodeAreaStatusOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaStatusOptions.class);
        cursorPositionFormat = options.getCursorPositionFormat();
        numericGrouping.setFromOptions(options);

        component = createLabel();
        component.setPreferredSize(new Dimension(160, component.getPreferredSize().height));
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() > 1) {
                    GoToPositionAction goToPositionAction = new GoToPositionAction();
                    goToPositionAction.setCodeArea(binaryDataComponent.getCodeArea());
                    goToPositionAction.actionPerformed();
                } else {
                    processPopupMenu(evt);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                processPopupMenu(evt);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                processPopupMenu(evt);
            }

            private void processPopupMenu(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
                    ActiveContextManagement contextManager = binaryDataComponent.getContextManagement().orElse(contextModule.getMainContextManager());
                    ActiveContextManagement popupContextManager = contextModule.createChildContextManager(contextManager);
                    popupContextManager.changeActiveState(BinaryCursorPositionComponent.class, BinaryCursorPositionComponent.this);
                    ContextRegistration contextRegistrar = contextModule.createContextRegistrator(popupContextManager);
                    MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                    JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();
                    menuModule.buildMenu(popupMenu, BinedViewerModule.BINARY_CURSOR_POSITION_MENU_ID, contextRegistrar, popupContextManager);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        BinaryCursorPositionComponent.this.clear();

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof BinaryDataComponent) {
                        updateForComponent((BinaryDataComponent) instance);
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextComponent.class, (ContextComponent instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryDataComponent && (updateType == BinEdDataComponent.UpdateType.CURSOR_POSITION || updateType == BinEdDataComponent.UpdateType.SELECTION)) {
                        updateForComponent((BinaryDataComponent) instance);
                    }
                });
                registrar.registerStateUpdateListener(BinaryCursorPositionComponent.class, (instance, updateType) -> {
                    if (instance == BinaryCursorPositionComponent.this && updateType == UpdateType.CURSOR_POSITION_FORMAT) {
                        update();
                    }
                });
            }

            private void updateForComponent(BinaryDataComponent component) {
                binaryDataComponent = component;
                update();
            }
        });
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

    public StatusNumericGrouping getNumericGrouping() {
        return numericGrouping;
    }

    public StatusCursorPositionFormat getCursorPositionFormat() {
        return cursorPositionFormat;
    }

    private void update() {
        component.setText(viewerModule.getCaretPositionAsText(binaryDataComponent.getCodeArea(), numericGrouping, cursorPositionFormat));
        component.setToolTipText(getCursorPositionToolTip());
    }

    protected void clear() {
        component.setText("-");
        component.setToolTipText(resourceBundle.getString("cursorPositionLabel.toolTipText"));
    }

    public String getCursorPositionToolTip() {
        CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        if (codeArea == null) {
            builder.append(resourceBundle.getString("cursorPositionLabel.toolTipText"));
        } else {
            CodeAreaCaretPosition caretPosition = ((CaretCapable) codeArea).getActiveCaretPosition();
            SelectionRange selectionRange = ((SelectionCapable) codeArea).getSelection();
            String octalPrefix = resourceBundle.getString("codeType.octal") + ": ";
            String decimalPrefix = resourceBundle.getString("codeType.decimal") + ": ";
            String hexadecimalPrefix = resourceBundle.getString("codeType.hexadecimal") + ": ";
            if (!selectionRange.isEmpty()) {
                long first = selectionRange.getFirst();
                long last = selectionRange.getLast();
                builder.append(resourceBundle.getString("selectionFromLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(viewerModule.getPositionAsText(first, PositionCodeType.OCTAL, numericGrouping)).append(BR_TAG);
                builder.append(decimalPrefix).append(viewerModule.getPositionAsText(first, PositionCodeType.DECIMAL, numericGrouping)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(viewerModule.getPositionAsText(first, PositionCodeType.HEXADECIMAL, numericGrouping)).append(BR_TAG);
                builder.append(BR_TAG);
                builder.append(resourceBundle.getString("selectionToLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(viewerModule.getPositionAsText(last, PositionCodeType.OCTAL, numericGrouping)).append(BR_TAG);
                builder.append(decimalPrefix).append(viewerModule.getPositionAsText(last, PositionCodeType.DECIMAL, numericGrouping)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(viewerModule.getPositionAsText(first, PositionCodeType.HEXADECIMAL, numericGrouping)).append(BR_TAG);
            } else {
                long dataPosition = caretPosition.getDataPosition();
                builder.append(resourceBundle.getString("cursorPositionLabel.toolTipText")).append(BR_TAG);
                builder.append(octalPrefix).append(viewerModule.getPositionAsText(dataPosition, PositionCodeType.OCTAL, numericGrouping)).append(BR_TAG);
                builder.append(decimalPrefix).append(viewerModule.getPositionAsText(dataPosition, PositionCodeType.DECIMAL, numericGrouping)).append(BR_TAG);
                builder.append(hexadecimalPrefix).append(viewerModule.getPositionAsText(dataPosition, PositionCodeType.HEXADECIMAL, numericGrouping));
            }
        }
        builder.append("</body></html>");

        return builder.toString();
    }

    public void setCursorPositionCodeType(PositionCodeType positionCodeType) {
        cursorPositionFormat.setCodeType(positionCodeType);
        save();
    }

    public void setCursorPositionShowOffset(boolean showOffset) {
        cursorPositionFormat.setShowOffset(showOffset);
        save();
    }

    public void save() {
        OptionsSettingsModuleApi optionsSettings = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsProvider settingsOptionsProvider = optionsSettings.getMainSettingsManager().getSettingsOptionsProvider();
        CodeAreaStatusOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaStatusOptions.class);
        options.setCursorPositionFormat(cursorPositionFormat);
        update();
    }
    
    protected JLabel createLabel() {
        return new JLabel();
    }

    public enum UpdateType implements StateUpdateType {
        CURSOR_POSITION_FORMAT
    }
}
