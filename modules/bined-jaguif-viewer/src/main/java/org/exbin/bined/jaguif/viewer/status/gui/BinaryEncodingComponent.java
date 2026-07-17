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
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;
import org.exbin.jaguif.text.encoding.CharsetEncodingState;
import org.exbin.jaguif.text.encoding.CharsetListEncodingState;
import org.exbin.jaguif.text.encoding.ContextEncoding;
import org.exbin.jaguif.text.encoding.EncodingsManager;

/**
 * Binary data encoding status component.
 */
@NullMarked
public class BinaryEncodingComponent extends AbstractStatusBarComponent {

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryEncodingComponent.class);

    protected BinaryDataComponent binaryDataComponent;
    protected CharsetEncodingState encodingState = null;

    public BinaryEncodingComponent() {
        component = new JLabel() {

            private final BasicArrowButton basicArrowButton = new BasicArrowButton(SwingConstants.NORTH);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Dimension areaSize = getSize();

                int h = areaSize.height;
                int w = areaSize.width;
                int size = Math.min(Math.max((h - 4) / 4, 2), 10);
                basicArrowButton.paintTriangle(g, w - size * 2, (h - size) / 2 - (h / 5), size, SwingConstants.NORTH, true);
                basicArrowButton.paintTriangle(g, w - size * 2, (h - size) / 2 + (h / 5), size, SwingConstants.SOUTH, true);
            }
        };

        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setText(resourceBundle.getString("encodingLabel.text"));
        component.setToolTipText(resourceBundle.getString("encodingLabel.toolTipText"));
        component.setPreferredSize(new Dimension(148, component.getPreferredSize().height));
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && binaryDataComponent != null) {
                    EncodingsManager encodingsManager = new EncodingsManager();
                    encodingsManager.init();
                    encodingsManager.setEncodingState((BinEdDataComponent) binaryDataComponent);
                    encodingsManager.setListEncodingState((BinEdDataComponent) binaryDataComponent);
                    if (evt.isShiftDown()) {
                        encodingsManager.cyclePreviousEncoding();
                    } else {
                        encodingsManager.cycleNextEncoding();
                    }
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
                    ContextRegistration contextRegistrar = contextModule.createContextRegistrator(popupContextManager);
                    popupContextManager.changeActiveState(BinaryEncodingComponent.class, BinaryEncodingComponent.this);
                    MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                    JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();
                    if (binaryDataComponent instanceof BinEdDataComponent) {
                        EncodingsManager encodingsManager = new EncodingsManager();
                        encodingsManager.init();
                        encodingsManager.setEncodingState((BinEdDataComponent) binaryDataComponent);
                        encodingsManager.setListEncodingState((BinEdDataComponent) binaryDataComponent);
                        encodingsManager.fillEncodingsPopupMenu(popupMenu);
                    }
                    menuModule.buildMenu(popupMenu, BinedViewerModule.BINARY_ENCODING_MENU_ID, contextRegistrar, popupContextManager);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    binaryDataComponent = instance instanceof BinaryDataComponent ? (BinaryDataComponent) instance : null;
                });
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof CharsetEncodingState) {
                        encodingState = (CharsetEncodingState) instance;
                        update();
                    } else {
                        clear();
                    }
                });
                registrar.registerChangeListener(ContextEncoding.class, (instance) -> {
                    if (instance instanceof CharsetEncodingState) {
                        encodingState = (CharsetEncodingState) instance;
                        update();
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextEncoding.class, (ContextEncoding instance, StateUpdateType updateType) -> {
                    if (CharsetEncodingState.UpdateType.ENCODING.equals(updateType)) {
                        update();
                        // BinaryEncodingComponent.this.contextEncoding = instance;
                    } else if (CharsetListEncodingState.UpdateType.ENCODING_LIST.equals(updateType)) {
                        // TODO
                        // BinaryEncodingComponent.this.contextEncoding = instance;
                    }
                });
            }
        });
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

    private void clear() {
        component.setText("");
    }

    private void update() {
        component.setText(encodingState != null ? encodingState.getEncoding() : "-");
    }
}
