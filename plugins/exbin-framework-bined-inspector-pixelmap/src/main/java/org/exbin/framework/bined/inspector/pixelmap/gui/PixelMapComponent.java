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
package org.exbin.framework.bined.inspector.pixelmap.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.ScrollingCapable;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Pixel map component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PixelMapComponent extends JComponent {

    private int pixelSize = 5;
    private int pixelPerRow = 16;
    private CodeAreaCore codeArea;

    public PixelMapComponent() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    long dataSize = codeArea.getDataSize();
                    long position = (e.getY() / pixelSize) * pixelPerRow;
                    int rowOffset = e.getX() / pixelSize;
                    if (rowOffset >= pixelPerRow) {
                        rowOffset = pixelPerRow - 1;
                    }
                    position += rowOffset;
                    if (position >= dataSize) {
                        position = dataSize - 1;
                    }
                    ((CaretCapable) codeArea).getCodeAreaCaret().setCaretPosition(position);
                    ((ScrollingCapable) codeArea).revealCursor();
                    codeArea.requestFocus();
                    codeArea.repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle clipBounds = g.getClipBounds();
        BinaryData contentData = codeArea.getContentData();
        int rowIndex = (clipBounds.y - pixelSize + 1) / pixelSize;
        int rowY = rowIndex * pixelSize;
        int lastRowY = Integer.MAX_VALUE;
        if (lastRowY - clipBounds.height > rowY) {
            lastRowY = clipBounds.y + clipBounds.height;
        }
        long position = rowIndex * pixelPerRow;
        long dataSize = codeArea.getDataSize();
        while (rowY <= lastRowY) {
            for (int x = 0; x < pixelPerRow; x++) {
                int color = 0;
                if (position < dataSize) {
                    int byteValue = contentData.getByte(position) & 0xff;
                    color = ((byteValue & 0xe0) << 16) + ((byteValue & 0x1c) << 11) + ((byteValue & 0x3) << 6);
                }
                g.setColor(new Color(color));
                g.drawRect(x * pixelSize, rowY, pixelSize, pixelSize);
                g.fillRect(x * pixelSize, rowY, pixelSize, pixelSize);
                position++;
            }
            rowY += pixelSize;
        }
    }

    public void setCodeArea(CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        dataChanged();
    }

    public void dataChanged() {
        long dataSize = codeArea.getDataSize();
        long height = (dataSize + pixelPerRow - 1) / pixelPerRow * pixelSize;
        Dimension size = new Dimension(pixelPerRow * pixelSize, height > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) height);
        setMinimumSize(size);
        setPreferredSize(size);
        revalidate();
        repaint();
    }
}
