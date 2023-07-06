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
package org.exbin.framework.bined.gui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.BevelBorder;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.framework.bined.data.source.CliboardFlavorBinaryData;
import org.exbin.framework.bined.data.ObjectValueConvertor;
import org.exbin.framework.bined.data.PageProviderBinaryData;
import org.exbin.framework.bined.data.source.ByteBufferPageProvider;
import org.exbin.framework.bined.data.source.CharBufferPageProvider;
import org.exbin.framework.bined.data.source.ReaderPageProvider;
import org.exbin.framework.utils.ClipboardUtils;
import org.exbin.framework.utils.WindowUtils;

/**
 * Clipboard content panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClipboardContentPanel extends javax.swing.JPanel {
    
    private DataFlavor[] dataFlavors;
    private final DefaultComboBoxModel<String> dataListModel = new DefaultComboBoxModel<>();
    private final List<BinaryData> dataListBinaryData = new ArrayList<>();
    private final ObjectValueConvertor objectValueConvertor = new ObjectValueConvertor();

    public ClipboardContentPanel() {
        initComponents();
        init();
    }
    
    private void init() {
        dataCodeArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
        flavorsList.setModel(new DataFlavorsListModel());
        flavorsList.addListSelectionListener((e) -> {
            int selectedIndex = flavorsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                DataFlavor dataFlavor = dataFlavors[selectedIndex];
                presentableNameTextField.setText(dataFlavor.getHumanPresentableName());
                stringTypeTextField.setText(dataFlavor.toString());
                mimeTypeTextField.setText(dataFlavor.getMimeType());
                primaryMimeTypeTextField.setText(dataFlavor.getPrimaryType());
                subMimeTypeTextField.setText(dataFlavor.getSubType());
                representationClassTextField.setText(dataFlavor.getRepresentationClass().getCanonicalName());
                
                dataListModel.removeAllElements();
                dataListBinaryData.clear();
                dataCodeArea.setContentData(null);

                try {
                    Clipboard clipboard = ClipboardUtils.getClipboard();
                    Object data = clipboard.getData(dataFlavor);
                    Optional<BinaryData> convBinaryData = objectValueConvertor.process(data);
                    BinaryData binaryData = null;
                    if (convBinaryData.isPresent()) {
                        binaryData = convBinaryData.get();
                    } else {
                        if (data instanceof InputStream) {
                            try {
                                CliboardFlavorBinaryData cliboardFlavorBinaryData = new CliboardFlavorBinaryData();
                                cliboardFlavorBinaryData.setDataFlavor(dataFlavor);
                                binaryData = cliboardFlavorBinaryData;
                            } catch (ClassNotFoundException | UnsupportedFlavorException ex) {
                            }
                        } if (data instanceof ByteBuffer) {
                            binaryData = new PageProviderBinaryData(new ByteBufferPageProvider((ByteBuffer) data));
                        } if (data instanceof CharBuffer) {
                            binaryData = new PageProviderBinaryData(new CharBufferPageProvider((CharBuffer) data));
                        } if (data instanceof Reader) {
                            binaryData = new PageProviderBinaryData(new ReaderPageProvider(() -> {
                                try {
                                    return (Reader) clipboard.getData(dataFlavor);
                                } catch (UnsupportedFlavorException | IOException ex) {
                                    throw new IllegalStateException("Unable to get clipboard data");
                                }
                            }));
                        }
                    }
                    
                    if (binaryData != null && data != null) {
                        dataListBinaryData.add(binaryData);
                        dataListModel.addElement("From class: " + data.getClass().getCanonicalName());
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                }
                    
                if (dataListBinaryData.isEmpty()) {
                    CliboardFlavorBinaryData binaryData = new CliboardFlavorBinaryData();
                    try {
                        binaryData.convertDataFlavor(dataFlavor);
                        dataListBinaryData.add(binaryData);
                        dataListModel.addElement("Requested conversion to: InputStream");
                    } catch (ClassNotFoundException | UnsupportedFlavorException ex) {
                    }
                }

                if (!dataListBinaryData.isEmpty()) {
                    dataCodeArea.setContentData(dataListBinaryData.get(0));
                }
            } else {
                
            }
        });
        dataComboBox.setModel(dataListModel);
    }
    
    public void loadFromClipboard() {
        dataFlavors = ClipboardUtils.getClipboard().getAvailableDataFlavors();
        ((DataFlavorsListModel) flavorsList.getModel()).setDataFlavors(dataFlavors);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        availableFlavorsLabel = new javax.swing.JLabel();
        flavorsScrollPane = new javax.swing.JScrollPane();
        flavorsList = new javax.swing.JList<>();
        flavorContentPanel = new javax.swing.JPanel();
        flavorPanel = new javax.swing.JPanel();
        presentableNameLabel = new javax.swing.JLabel();
        presentableNameTextField = new javax.swing.JTextField();
        stringTypeLabel = new javax.swing.JLabel();
        stringTypeTextField = new javax.swing.JTextField();
        mimeTypeLabel = new javax.swing.JLabel();
        mimeTypeTextField = new javax.swing.JTextField();
        primaryMimeTypeLabel = new javax.swing.JLabel();
        primaryMimeTypeTextField = new javax.swing.JTextField();
        subMimeTypeLabel = new javax.swing.JLabel();
        subMimeTypeTextField = new javax.swing.JTextField();
        representationClassLabel = new javax.swing.JLabel();
        representationClassTextField = new javax.swing.JTextField();
        dataLabel = new javax.swing.JLabel();
        dataComboBox = new javax.swing.JComboBox<>();
        dataCodeArea = new org.exbin.bined.swing.basic.CodeArea();

        availableFlavorsLabel.setText("Available Flavors");

        flavorsScrollPane.setViewportView(flavorsList);

        flavorContentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Content"));
        flavorContentPanel.setLayout(new java.awt.BorderLayout());

        presentableNameLabel.setText("String Type");

        presentableNameTextField.setEditable(false);

        stringTypeLabel.setText("String Type");

        stringTypeTextField.setEditable(false);

        mimeTypeLabel.setText("MIME Type");

        mimeTypeTextField.setEditable(false);

        primaryMimeTypeLabel.setText("Primary Type");

        primaryMimeTypeTextField.setEditable(false);

        subMimeTypeLabel.setText("Sub Type");

        subMimeTypeTextField.setEditable(false);

        representationClassLabel.setText("Representation Class");
        representationClassLabel.setToolTipText("");

        representationClassTextField.setEditable(false);

        dataLabel.setText("Data");

        javax.swing.GroupLayout flavorPanelLayout = new javax.swing.GroupLayout(flavorPanel);
        flavorPanel.setLayout(flavorPanelLayout);
        flavorPanelLayout.setHorizontalGroup(
            flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(flavorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataCodeArea, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(presentableNameTextField)
                    .addComponent(stringTypeTextField)
                    .addComponent(mimeTypeTextField)
                    .addGroup(flavorPanelLayout.createSequentialGroup()
                        .addGroup(flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(primaryMimeTypeLabel)
                            .addComponent(primaryMimeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(flavorPanelLayout.createSequentialGroup()
                                .addComponent(subMimeTypeLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(subMimeTypeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)))
                    .addComponent(representationClassTextField)
                    .addGroup(flavorPanelLayout.createSequentialGroup()
                        .addGroup(flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(presentableNameLabel)
                            .addComponent(stringTypeLabel)
                            .addComponent(mimeTypeLabel)
                            .addComponent(representationClassLabel)
                            .addComponent(dataLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(dataComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        flavorPanelLayout.setVerticalGroup(
            flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(flavorPanelLayout.createSequentialGroup()
                .addComponent(presentableNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(presentableNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stringTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stringTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(flavorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(flavorPanelLayout.createSequentialGroup()
                        .addComponent(mimeTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mimeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(primaryMimeTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(primaryMimeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(flavorPanelLayout.createSequentialGroup()
                        .addComponent(subMimeTypeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subMimeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(representationClassLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(representationClassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dataLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataCodeArea, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );

        flavorContentPanel.add(flavorPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flavorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(availableFlavorsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(flavorContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flavorContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(availableFlavorsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(flavorsScrollPane)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new ClipboardContentPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel availableFlavorsLabel;
    private org.exbin.bined.swing.basic.CodeArea dataCodeArea;
    private javax.swing.JComboBox<String> dataComboBox;
    private javax.swing.JLabel dataLabel;
    private javax.swing.JPanel flavorContentPanel;
    private javax.swing.JPanel flavorPanel;
    private javax.swing.JList<String> flavorsList;
    private javax.swing.JScrollPane flavorsScrollPane;
    private javax.swing.JLabel mimeTypeLabel;
    private javax.swing.JTextField mimeTypeTextField;
    private javax.swing.JLabel presentableNameLabel;
    private javax.swing.JTextField presentableNameTextField;
    private javax.swing.JLabel primaryMimeTypeLabel;
    private javax.swing.JTextField primaryMimeTypeTextField;
    private javax.swing.JLabel representationClassLabel;
    private javax.swing.JTextField representationClassTextField;
    private javax.swing.JLabel stringTypeLabel;
    private javax.swing.JTextField stringTypeTextField;
    private javax.swing.JLabel subMimeTypeLabel;
    private javax.swing.JTextField subMimeTypeTextField;
    // End of variables declaration//GEN-END:variables
}
