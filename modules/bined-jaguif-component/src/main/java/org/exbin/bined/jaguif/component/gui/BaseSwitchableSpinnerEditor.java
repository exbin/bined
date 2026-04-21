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
package org.exbin.bined.jaguif.component.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;

/**
 * Spinner editor supporting multiple bases.
 */
@ParametersAreNonnullByDefault
public class BaseSwitchableSpinnerEditor extends JPanel implements ChangeListener, PropertyChangeListener, LayoutManager {

    private static final String SPINNER_PROPERTY = "value";

    private static final int LENGTH_LIMIT = 21;
    private volatile boolean adjusting;
    private int numBase = 10;

    private final char[] cache = new char[LENGTH_LIMIT];

    private final JTextField textField;
    private final JSpinner spinner;

    public BaseSwitchableSpinnerEditor(JSpinner spinner) {
        this.spinner = spinner;
        textField = new JTextField();

        init();
    }

    private void init() {
        textField.setName("Spinner.textField");
        textField.setText(getValueAsString((Long) spinner.getValue()));
        textField.addPropertyChangeListener(this);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            private final PropertyChangeEvent changeEvent = new PropertyChangeEvent(textField, SPINNER_PROPERTY, null, null);

            @Override
            public void changedUpdate(DocumentEvent e) {
                notifyChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                notifyChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                notifyChanged();
            }

            public void notifyChanged() {
                propertyChange(changeEvent);
            }
        });
        textField.setEditable(true);
        textField.setInheritsPopupMenu(true);

        String toolTipText = spinner.getToolTipText();
        if (toolTipText != null) {
            textField.setToolTipText(toolTipText);
        }

        add(textField);

        setLayout(this);
        spinner.addChangeListener(this);
    }

    @Nonnull
    private JTextField getTextField() {
        return textField;
    }

    @Nonnull
    private JSpinner getSpinner() {
        return spinner;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (adjusting) {
            return;
        }

        JSpinner sourceSpinner = (JSpinner) (e.getSource());
        SwingUtilities.invokeLater(() -> {
            textField.setText(getValueAsString((Long) sourceSpinner.getValue()));
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (adjusting) {
            return;
        }

        JSpinner sourceSpinner = getSpinner();

        Object source = e.getSource();
        String name = e.getPropertyName();
        if ((source instanceof JTextField) && SPINNER_PROPERTY.equals(name)) {
            Long lastValue = (Long) sourceSpinner.getValue();

            // Try to set the new value
            try {
                sourceSpinner.setValue(valueOfPosition(getTextField().getText()));
            } catch (IllegalArgumentException iae) {
                // SpinnerModel didn't like new value, reset
                try {
                    sourceSpinner.setValue(lastValue);
                } catch (IllegalArgumentException iae2) {
                    // Still bogus, nothing else we can do, the
                    // SpinnerModel and JFormattedTextField are now out
                    // of sync.
                }
            }
        }
    }

    public void setPositionValue(long positionValue) {
        textField.setText(getValueAsString(positionValue));
        spinner.setValue(positionValue);
    }

    public void startAdjusting() {
        adjusting = true;
    }

    public void stopAdjusting() {
        adjusting = false;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the size of the parents insets.
     */
    @Nonnull
    private Dimension insetSize(Container parent) {
        Insets insets = parent.getInsets();
        int width = insets.left + insets.right;
        int height = insets.top + insets.bottom;
        return new Dimension(width, height);
    }

    @Nonnull
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension preferredSize = insetSize(parent);
        if (parent.getComponentCount() > 0) {
            Dimension childSize = getComponent(0).getPreferredSize();
            preferredSize.width += childSize.width;
            preferredSize.height += childSize.height;
        }
        return preferredSize;
    }

    @Nonnull
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension minimumSize = insetSize(parent);
        if (parent.getComponentCount() > 0) {
            Dimension childSize = getComponent(0).getMinimumSize();
            minimumSize.width += childSize.width;
            minimumSize.height += childSize.height;
        }
        return minimumSize;
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent.getComponentCount() > 0) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth() - (insets.left + insets.right);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            getComponent(0).setBounds(insets.left, insets.top, width, height);
        }
    }

    public int getNumBase() {
        return numBase;
    }

    public void setNumBase(int numBase) {
        this.numBase = numBase;
    }

    public void requestTextFieldFocusInWindow() {
        textField.requestFocusInWindow();
    }

    public void addTextFieldFocusListener(FocusListener listener) {
        textField.addFocusListener(listener);
    }

    @Nonnull
    private String getValueAsString(long value) {
        if (value < 0) {
            return "-" + getNonNegativeValueAsString(-value);
        }
        return getNonNegativeValueAsString(value);
    }

    @Nonnull
    private String getNonNegativeValueAsString(long value) {
        Arrays.fill(cache, ' ');
        CodeAreaUtils.longToBaseCode(cache, 0, value, numBase, LENGTH_LIMIT, false, CodeCharactersCase.LOWER);
        return new String(cache).trim();
    }

    private long valueOfPosition(String position) {
        return Long.parseLong(position, numBase);
    }
}
