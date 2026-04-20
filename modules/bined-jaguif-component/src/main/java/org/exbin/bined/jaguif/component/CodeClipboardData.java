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
package org.exbin.bined.jaguif.component;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.swing.CodeAreaSwingUtils;

/**
 * Copy as code clipboard data.
 */
@ParametersAreNonnullByDefault
public class CodeClipboardData implements CodeAreaSwingUtils.ClipboardData {

    protected final BinaryData data;
    protected final DataFlavor binaryDataFlavor;
    protected final CodeType codeType;
    protected final CodeCharactersCase charactersCase;

    public CodeClipboardData(BinaryData data, DataFlavor binaryDataFlavor, CodeType codeType, CodeCharactersCase charactersCase) {
        this.data = data;
        this.binaryDataFlavor = binaryDataFlavor;
        this.codeType = codeType;
        this.charactersCase = charactersCase;
    }

    @Nonnull
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{binaryDataFlavor, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(binaryDataFlavor) || flavor.equals(DataFlavor.stringFlavor);
    }

    @Nonnull
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(binaryDataFlavor)) {
            return data;
        } else {
            int charsPerByte = codeType.getMaxDigitsForByte() + 1;
            int textLength = (int) (data.getDataSize() * charsPerByte);
            if (textLength > 0) {
                textLength--;
            }

            char[] targetData = new char[textLength];
            Arrays.fill(targetData, ' ');
            for (int i = 0; i < data.getDataSize(); i++) {
                CodeAreaUtils.byteToCharsCode(data.getByte(i), codeType, targetData, i * charsPerByte, charactersCase);
            }
            return new String(targetData);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do nothing
    }

    @Override
    public void dispose() {
        data.dispose();
    }
}
