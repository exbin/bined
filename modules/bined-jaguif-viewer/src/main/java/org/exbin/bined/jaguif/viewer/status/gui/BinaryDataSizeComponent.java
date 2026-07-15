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
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.jaguif.viewer.status.StatusDataSizeFormat;
import org.exbin.bined.jaguif.viewer.status.StatusNumericGrouping;
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
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * Binary data size status component.
 */
@NullMarked
public class BinaryDataSizeComponent extends AbstractStatusBarComponent {

    public static final String POPUP_MENU_ID = "binaryDataSize";
    protected static final String BR_TAG = "<br>";

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryDataSizeComponent.class);

    protected StatusNumericGrouping numericGrouping = new StatusNumericGrouping();
    protected StatusDataSizeFormat dataSizeFormat = new StatusDataSizeFormat();
    protected BinedViewerModule viewerModule;

    protected BinaryDataComponent binaryDataComponent;
    protected long dataSize;
    protected long originalDataSize = 0;
    protected SelectionRange selectionRange;

    public BinaryDataSizeComponent() {
        viewerModule = App.getModule(BinedViewerModule.class);
        component = new JLabel();
        component.setPreferredSize(new Dimension(160, component.getPreferredSize().height));
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                processPopupMenu(evt);
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
                    popupContextManager.changeActiveState(BinaryDataSizeComponent.class, BinaryDataSizeComponent.this);
                    ContextRegistration contextRegistrar = contextModule.createContextRegistrator(popupContextManager);
                    MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                    JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();
                    menuModule.buildMenu(popupMenu, POPUP_MENU_ID, contextRegistrar, popupContextManager);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        BinaryDataSizeComponent.this.clear();

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
                    if (instance instanceof BinaryDataComponent && (updateType == BinEdDataComponent.UpdateType.DATA_CONTENT || updateType == BinEdDataComponent.UpdateType.SELECTION)) {
                        updateForComponent((BinaryDataComponent) instance);
                    }
                });
                registrar.registerStateUpdateListener(BinaryDataSizeComponent.class, (instance, updateType) -> {
                    if (instance == BinaryDataSizeComponent.this && updateType == UpdateType.DATA_SIZE_FORMAT) {
                        update();
                    }
                });
            }

            private void updateForComponent(BinaryDataComponent component) {
                binaryDataComponent = component;
                dataSize = component.getCodeArea().getDataSize();
                selectionRange = ((SelectionCapable) component.getCodeArea()).getSelection();
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

    public StatusDataSizeFormat getDataSizeFormat() {
        return dataSizeFormat;
    }

    public long getOriginalDataSize() {
        return originalDataSize;
    }

    public void setOriginalDataSize(long originalDataSize) {
        this.originalDataSize = originalDataSize;
    }

    protected void update() {
        component.setText(viewerModule.getDataSizeAsText(dataSize, originalDataSize, selectionRange, numericGrouping, dataSizeFormat));
        component.setToolTipText(getDataSizeToolTip());
    }

    protected void clear() {
        component.setText("-");
        component.setToolTipText(resourceBundle.getString("dataSizeLabel.toolTipText"));
    }

    public String getDataSizeToolTip() {
        String octalPrefix = resourceBundle.getString("codeType.octal") + ": ";
        String decimalPrefix = resourceBundle.getString("codeType.decimal") + ": ";
        String hexadecimalPrefix = resourceBundle.getString("codeType.hexadecimal") + ": ";

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>");
        if (selectionRange != null && !selectionRange.isEmpty()) {
            long length = selectionRange.getLength();
            builder.append(resourceBundle.getString("selectionLengthLabel.toolTipText")).append(BR_TAG);
            builder.append(octalPrefix).append(viewerModule.getPositionAsText(length, PositionCodeType.OCTAL, numericGrouping)).append(BR_TAG);
            builder.append(decimalPrefix).append(viewerModule.getPositionAsText(length, PositionCodeType.DECIMAL, numericGrouping)).append(BR_TAG);
            builder.append(hexadecimalPrefix).append(viewerModule.getPositionAsText(length, PositionCodeType.HEXADECIMAL, numericGrouping)).append(BR_TAG);
            builder.append(BR_TAG);
        }

        builder.append(resourceBundle.getString("dataSizeLabel.toolTipText")).append(BR_TAG);
        builder.append(octalPrefix).append(viewerModule.getPositionAsText(dataSize, PositionCodeType.OCTAL, numericGrouping)).append(BR_TAG);
        builder.append(decimalPrefix).append(viewerModule.getPositionAsText(dataSize, PositionCodeType.DECIMAL, numericGrouping)).append(BR_TAG);
        builder.append(hexadecimalPrefix).append(viewerModule.getPositionAsText(dataSize, PositionCodeType.HEXADECIMAL, numericGrouping));
        builder.append("</body></html>");

        return builder.toString();
    }

    public void setDataSizeCodeType(PositionCodeType positionCodeType) {
        dataSizeFormat.setCodeType(positionCodeType);
        update();
    }

    public void setDataSizeShowRelative(boolean showRelative) {
        dataSizeFormat.setShowRelative(showRelative);
        update();
    }

    public enum UpdateType implements StateUpdateType {
        DATA_SIZE_FORMAT
    }
}
