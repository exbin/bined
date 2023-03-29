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
package org.exbin.framework.bined;

import java.awt.Color;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaSection;
import org.exbin.bined.CodeAreaSelection;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Specific painter for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdCodeAreaPainter extends ExtendedHighlightNonAsciiCodeAreaPainter {

    public BinEdCodeAreaPainter(CodeAreaCore codeArea) {
        super(codeArea);
    }

    @Nullable
    @Override
    public Color getPositionBackgroundColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean unprintables) {
        Color color = super.getPositionBackgroundColor(rowDataPosition, byteOnRow, charOnRow, section, unprintables);
//        CodeAreaSelection selectionHandler = ((SelectionCapable) codeArea).getSelectionHandler();
//        boolean inSelection = selectionHandler.isInSelection(rowDataPosition + byteOnRow);
//        if (color == null || inSelection) {
//            long dataPosition = rowDataPosition + byteOnRow;
//            if (dataPosition > 100 && dataPosition < 300) {
//                if (inSelection && color != null) {
//                    return new Color(
//                            (((int) (dataPosition * 17) % 255) + color.getRed()) / 2,
//                            (((int) (dataPosition * 37) % 255) + color.getGreen()) / 2,
//                            (((int) (dataPosition * 13) % 255) + color.getBlue()) / 2);
//                }
//                return new Color((int) (dataPosition * 17) % 255, (int) (dataPosition * 37) % 255, (int) (dataPosition * 13) % 255);
//            }
//        }

        return color;
    }

    @Nullable
    @Override
    public Color getPositionTextColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean unprintables) {
        return super.getPositionTextColor(rowDataPosition, byteOnRow, charOnRow, section, unprintables);
    }
}
