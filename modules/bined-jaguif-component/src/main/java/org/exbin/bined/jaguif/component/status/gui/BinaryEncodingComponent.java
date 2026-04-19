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
package org.exbin.bined.jaguif.component.status.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;
import org.exbin.jaguif.text.encoding.CharsetEncodingState;
import org.exbin.jaguif.text.encoding.CharsetListEncodingState;
import org.exbin.jaguif.text.encoding.ContextEncoding;

/**
 * BinEd encoding status component.
 */
@ParametersAreNonnullByDefault
public class BinaryEncodingComponent extends AbstractStatusBarComponent {

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryEncodingComponent.class);

    protected BinEdDataComponent dataComponent = null;

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
                encodingLabelMouseClicked(evt);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                handleEncodingPopup(evt);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                handleEncodingPopup(evt);
            }
        });

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof BinEdDataComponent) {
                        dataComponent = (BinEdDataComponent) instance;
                        update();
                    } else {
                        clear();
                    }
                });
                registrar.registerChangeListener(ContextEncoding.class, (instance) -> {
                    if (instance instanceof BinEdDataComponent) {
                        dataComponent = (BinEdDataComponent) instance;
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

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }

    private void clear() {
        component.setText("");
    }

    private void update() {
        component.setText(dataComponent != null ? dataComponent.getEncoding() : "-");
    }

    private void encodingLabelMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.isShiftDown()) {
                // TODO ((EncodingsController) controller).cyclePreviousEncoding();
            } else {
                // TODO ((EncodingsController) controller).cycleNextEncoding();
            }
        } else {
            handleEncodingPopup(evt);
        }
    }

    private void handleEncodingPopup(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            // TODO ((EncodingsController) controller).encodingsPopupEncodingsMenu(evt);
        }
    }
}
