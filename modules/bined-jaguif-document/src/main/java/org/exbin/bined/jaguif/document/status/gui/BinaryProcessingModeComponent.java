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
package org.exbin.bined.jaguif.document.status.gui;

import java.awt.Dimension;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.bined.jaguif.document.FileProcessingMode;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.statusbar.api.AbstractStatusBarComponent;
import org.jspecify.annotations.Nullable;

/**
 * Binary document processing mode status component.
 */
@NullMarked
public class BinaryProcessingModeComponent extends AbstractStatusBarComponent {

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryProcessingModeComponent.class);
    protected BinaryFileDocument binaryFileDocument;

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
                    ActiveContextManagement contextManager = binaryFileDocument.getDataComponent().getContextManagement().orElse(contextModule.getMainContextManager());
                    ActiveContextManagement popupContextManager = contextModule.createChildContextManager(contextManager);
                    popupContextManager.changeActiveState(BinaryProcessingModeComponent.class, BinaryProcessingModeComponent.this);
                    ContextRegistration contextRegistrar = contextModule.createContextRegistrator(popupContextManager);
                    MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                    JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();
                    menuModule.buildMenu(popupMenu, BinedDocumentModule.PROCESSING_MODE_MENU_ID, contextRegistrar, popupContextManager);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof BinaryDataComponent) {
                        updateForDocument(binaryFileDocument);
                    } else {
                        clear();
                    }
                });
                registrar.registerChangeListener(ContextDocument.class, (ContextDocument instance) -> {
                    if (instance instanceof BinaryFileDocument) {
                        updateForDocument((BinaryFileDocument) instance);
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextDocument.class, (ContextDocument instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinaryFileDocument && (updateType == BinaryFileDocument.UpdateType.PROCESSING_MODE)) {
                        updateForDocument((BinaryFileDocument) instance);
                    }
                });
            }

            private void updateForDocument(@Nullable BinaryFileDocument document) {
                binaryFileDocument = document;
                update();
            }
        });
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

    private void update() {
        if (binaryFileDocument == null) {
            component.setText("");
        } else {
            FileProcessingMode fileProcessingMode = binaryFileDocument.getFileProcessingMode();

            if (FileProcessingMode.MEMORY.equals(fileProcessingMode)) {
                component.setText(resourceBundle.getString("fileProcessingMode.memory"));
            } else if (FileProcessingMode.DELTA.equals(fileProcessingMode)) {
                component.setText(resourceBundle.getString("fileProcessingMode.delta"));
            }
        }
    }

    private void clear() {
        // TODO
    }
}
