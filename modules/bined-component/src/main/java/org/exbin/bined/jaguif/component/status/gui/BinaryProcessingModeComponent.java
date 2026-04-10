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
package org.exbin.bined.jaguif.component.status.gui;

import java.awt.Dimension;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.exbin.bined.jaguif.component.BinaryFileDocument;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;

/**
 * BinEd file processing mode status component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryProcessingModeComponent extends AbstractStatusBarComponent {

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryProcessingModeComponent.class);

    public BinaryProcessingModeComponent() {
        component = new JLabel();
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        component.setText(resourceBundle.getString("processingModeLabel.text"));
        component.setToolTipText(resourceBundle.getString("processingModeLabel.toolTipText"));
        component.setPreferredSize(new Dimension(20, component.getPreferredSize().height));
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handlePopup(evt);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                handlePopup(evt);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                handlePopup(evt);
            }
        });

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextDocument.class, (ContextDocument instance) -> {
                    if (instance instanceof BinaryFileDocument) {
                        // TODO update();
                    } else {
                        clear();
                    }
                });
                /* registrar.registerStateUpdateListener(ContextDocument.class, (ContextDocument instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryFileDocument && (updateType == BinaryFileDocument.Update.ENCODING)) {
                        updateForDocument((BinaryFileDocument) instance);
                    } else {
                        clear();
                    }
                }); */
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getComponent() {
        return component;
    }
    
    private void clear() {
        // TODO
    }

    private void handlePopup(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            // TODO
        }
    }
}
