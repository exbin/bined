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
package org.exbin.bined.jaguif.editor.status.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.editor.BinedEditorModule;
import org.exbin.bined.jaguif.editor.action.EditModeActions;
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
 * Binary data edit mode status component.
 */
@NullMarked
public class BinaryEditModeComponent extends AbstractStatusBarComponent {

    protected final JLabel component;
    protected final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(BinaryEditModeComponent.class);

    protected BinaryDataComponent binaryDataComponent;

    public BinaryEditModeComponent() {
        component = new JLabel();
        component.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        clear();
        component.setToolTipText(resourceBundle.getString("editModeLabel.toolTipText"));
        component.setPreferredSize(new Dimension(40, component.getPreferredSize().height));
        component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    BinedEditorModule editorModule = App.getModule(BinedEditorModule.class);
                    EditModeActions.SwitchEditOperationAction action = editorModule.createEditModeActions().createSwitchEditOperationAction();
                    action.setBinaryDataComponent(binaryDataComponent);
                    action.actionPerformed(null);
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
                    popupContextManager.changeActiveState(BinaryEditModeComponent.class, BinaryEditModeComponent.this);
                    ContextRegistration contextRegistrar = contextModule.createContextRegistrator(popupContextManager);
                    MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
                    JPopupMenu popupMenu = menuModule.getMenuBuilder().createPopupMenu();
                    menuModule.buildMenu(popupMenu, BinedEditorModule.BINARY_EDIT_MODE_MENU_ID, contextRegistrar, popupContextManager);
                    popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        putValue(KEY_CONTEXT_CHANGE, new ContextChange() {
            @Override
            public void register(ContextChangeRegistration registrar) {
                registrar.registerChangeListener(ContextComponent.class, (ContextComponent instance) -> {
                    if (instance instanceof BinEdDataComponent) {
                        updateForComponent((BinEdDataComponent) instance);
                    } else {
                        clear();
                    }
                });
                registrar.registerStateUpdateListener(ContextComponent.class, (ContextComponent instance, StateUpdateType updateType) -> {
                    if (instance instanceof BinEdDataComponent && (updateType == BinEdDataComponent.UpdateType.EDIT_MODE)) {
                        updateForComponent((BinEdDataComponent) instance);
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

    private void clear() {
        component.setText("");
    }

    private void update() {
        EditMode editMode = ((EditModeCapable) binaryDataComponent.getCodeArea()).getEditMode();
        EditOperation editOperation = ((EditModeCapable) binaryDataComponent.getCodeArea()).getActiveOperation();
        switch (editMode) {
            case READ_ONLY:
                component.setText(resourceBundle.getString("editMode.readOnly"));
                break;
            case INPLACE:
                component.setText(resourceBundle.getString("editMode.inplace"));
                break;
            case EXPANDING:
            case CAPPED:
                switch (editOperation) {
                    case INSERT:
                        component.setText(resourceBundle.getString("editMode.insert"));
                        break;
                    case OVERWRITE:
                        component.setText(resourceBundle.getString("editMode.overwrite"));
                        break;
                    default:
                        throw new AssertionError();
                }
                break;
            default:
                throw new AssertionError();
        }
    }
}
