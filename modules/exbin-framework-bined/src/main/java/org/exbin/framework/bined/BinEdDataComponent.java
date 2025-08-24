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

import java.awt.Font;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.action.api.clipboard.ClipboardStateListener;
import org.exbin.framework.action.api.clipboard.TextClipboardController;
import org.exbin.framework.text.encoding.TextEncodingController;
import org.exbin.framework.text.font.TextFontController;

/**
 * Binary data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDataComponent implements ActiveComponent, BinaryDataComponent, TextClipboardController, TextEncodingController, TextFontController {

    private final CodeAreaCore codeArea;
    private Font defaultFont;

    public BinEdDataComponent(CodeAreaCore codeArea) {
        this.codeArea = codeArea;
        defaultFont = ((FontCapable) codeArea).getCodeFont();
    }

    @Nonnull
    @Override
    public CodeAreaCore getCodeArea() {
        return codeArea;
    }

    @Override
    public void performCut() {
        codeArea.cut();
    }

    @Override
    public void performCopy() {
        codeArea.copy();
    }

    @Override
    public void performPaste() {
        codeArea.paste();
    }

    @Override
    public void performDelete() {
        codeArea.delete();
    }

    @Override
    public void performSelectAll() {
        codeArea.selectAll();
    }

    @Override
    public boolean hasSelection() {
        return codeArea.hasSelection();
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public boolean isEditable() {
        return codeArea.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return codeArea.isEditable() && codeArea.canPaste();
    }

    @Override
    public boolean canDelete() {
        return codeArea.isEditable();
    }

    @Override
    public void setUpdateListener(ClipboardStateListener updateListener) {
        // componentPanel.setUpdateListener(updateListener);
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return ((CharsetCapable) codeArea).getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        ((CharsetCapable) codeArea).setCharset(charset);
    }

    @Nonnull
    @Override
    public Font getCurrentFont() {
        return ((FontCapable) codeArea).getCodeFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Override
    public void setCurrentFont(Font font) {
        ((FontCapable) codeArea).setCodeFont(font);
    }
}
