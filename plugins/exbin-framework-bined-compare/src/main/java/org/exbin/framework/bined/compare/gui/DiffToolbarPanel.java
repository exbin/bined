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
package org.exbin.framework.bined.compare.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.auxiliary.dropdownbutton.DropDownButtonVariant;
import org.exbin.bined.CodeType;
import org.exbin.framework.App;
import org.exbin.framework.bined.viewer.options.CodeAreaOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Diff toolbar panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DiffToolbarPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle;

    private Controller controller;

    private final AbstractAction cycleCodeTypesAction;
    private final JRadioButtonMenuItem binaryCodeTypeMenuItem;
    private final JRadioButtonMenuItem octalCodeTypeMenuItem;
    private final JRadioButtonMenuItem decimalCodeTypeMenuItem;
    private final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem;
    private final ButtonGroup codeTypeButtonGroup;
    private DropDownButton codeTypeDropDown;

    public DiffToolbarPanel() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        resourceBundle = languageModule.getBundle(org.exbin.framework.bined.BinedModule.class);
        codeTypeButtonGroup = new ButtonGroup();
        Action binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        };
        binaryCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("binaryCodeTypeAction.text"));
        binaryCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("binaryCodeTypeAction.shortDescription"));
        binaryCodeTypeMenuItem = new JRadioButtonMenuItem(binaryCodeTypeAction);
        codeTypeButtonGroup.add(binaryCodeTypeMenuItem);
        Action octalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        };
        octalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("octalCodeTypeAction.text"));
        octalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("octalCodeTypeAction.shortDescription"));
        octalCodeTypeMenuItem = new JRadioButtonMenuItem(octalCodeTypeAction);
        codeTypeButtonGroup.add(octalCodeTypeMenuItem);
        Action decimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        };
        decimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("decimalCodeTypeAction.text"));
        decimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("decimalCodeTypeAction.shortDescription"));
        decimalCodeTypeMenuItem = new JRadioButtonMenuItem(decimalCodeTypeAction);
        codeTypeButtonGroup.add(decimalCodeTypeMenuItem);
        Action hexadecimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        };
        hexadecimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("hexadecimalCodeTypeAction.text"));
        hexadecimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexadecimalCodeTypeAction.shortDescription"));
        hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem(hexadecimalCodeTypeAction);
        codeTypeButtonGroup.add(hexadecimalCodeTypeMenuItem);
        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codeTypePos = controller.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                controller.setCodeType(next);
                updateCycleButtonState();
            }
        };

        initComponents();
        init();
    }

    private void init() {
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("cycleCodeTypesAction.shortDescription"));
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(octalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeMenuItem);
        codeTypeDropDown = new DropDownButton(DropDownButtonVariant.TOOL, cycleCodeTypesAction, cycleCodeTypesPopupMenu);
        codeTypeDropDown.setMaximumSize(new Dimension(codeTypeDropDown.getPreferredSize().width, Integer.MAX_VALUE));
        toolBar.add(codeTypeDropDown);
        toolBar.setFloatable(false);
    }

    public void setController(Controller controller) {
        this.controller = controller;
        updateNonprintables();
        updateCycleButtonState();
    }
    
    public void setTargetComponent(JComponent component) {
        
    }

    private void updateCycleButtonState() {
        CodeType codeType = controller.getCodeType();
        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
        switch (codeType) {
            case BINARY: {
                if (!binaryCodeTypeMenuItem.isSelected()) {
                    binaryCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case OCTAL: {
                if (!octalCodeTypeMenuItem.isSelected()) {
                    octalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case DECIMAL: {
                if (!decimalCodeTypeMenuItem.isSelected()) {
                    decimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case HEXADECIMAL: {
                if (!hexadecimalCodeTypeMenuItem.isSelected()) {
                    hexadecimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
        }
    }

    public void applyFromCodeArea() {
        updateCycleButtonState();
        updateNonprintables();
    }

    public void loadFromOptions(OptionsStorage options) {
        controller.setCodeType(new CodeAreaOptions(options).getCodeType());
        updateCycleButtonState();
        updateNonprintables();
    }

    public void updateNonprintables() {
        showNonprintablesToggleButton.setSelected(controller.isShowNonprintables());
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (codeTypeDropDown != null) {
            codeTypeDropDown.updateUI();
        }
    }

    @Nonnull
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        showNonprintablesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();

        setLayout(new java.awt.BorderLayout());

        toolBar.setRollover(true);

        showNonprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(resourceBundle.getString("viewNonprintablesToolbarAction.smallIcon"))));
        showNonprintablesToggleButton.setToolTipText(resourceBundle.getString("viewNonprintablesAction.text")); // NOI18N
        showNonprintablesToggleButton.setFocusable(false);
        showNonprintablesToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showNonprintablesToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showNonprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showNonprintablesToggleButtonActionPerformed(evt);
            }
        });
        toolBar.add(showNonprintablesToggleButton);
        toolBar.add(separator1);

        add(toolBar, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void showNonprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showNonprintablesToggleButtonActionPerformed
        controller.setShowNonprintables(showNonprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showNonprintablesToggleButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToggleButton showNonprintablesToggleButton;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        @Nonnull
        CodeType getCodeType();

        void setCodeType(CodeType codeType);

        boolean isShowNonprintables();

        void setShowNonprintables(boolean showNonprintables);

        void repaint();
    }
}
