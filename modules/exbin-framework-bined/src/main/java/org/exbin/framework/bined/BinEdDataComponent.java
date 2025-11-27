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

import java.awt.Component;
import java.awt.Font;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.clipboard.ClipboardStateListener;
import org.exbin.framework.action.api.clipboard.TextClipboardController;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.text.encoding.CharsetEncodingState;
import org.exbin.framework.text.encoding.CharsetListEncodingState;
import org.exbin.framework.text.font.TextFontState;

/**
 * Binary data binaryComponent.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDataComponent implements ContextComponent, BinaryDataComponent, TextClipboardController, CharsetEncodingState, CharsetListEncodingState, TextFontState {

    protected final BinEdComponentPanel binaryComponent;
    protected final CodeAreaCore codeArea;
    protected Font defaultFont;
    protected ActiveContextProvider contextProvider;
    protected List<String> encodings = new ArrayList<>();

    public BinEdDataComponent(BinEdComponentPanel binaryComponent) {
        this.binaryComponent = binaryComponent;
        this.codeArea = binaryComponent.getCodeArea();
        defaultFont = ((FontCapable) codeArea).getCodeFont();
    }

    public BinEdDataComponent(CodeAreaCore codeArea) {
        this.binaryComponent = null;
        this.codeArea = codeArea;
        defaultFont = ((FontCapable) codeArea).getCodeFont();
    }
    
    @Nonnull
    public Component getComponent() {
        return binaryComponent != null ? binaryComponent : codeArea;
    }

    @Nonnull
    @Override
    public CodeAreaCore getCodeArea() {
        return codeArea;
    }

    public void setContextProvider(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
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
    public String getEncoding() {
        return ((CharsetCapable) codeArea).getCharset().name();
    }

    @Override
    public void setEncoding(String encoding) {
        ((CharsetCapable) codeArea).setCharset(Charset.forName(encoding));
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CharsetEncodingState.ChangeType.ENCODING);
        }
    }

    @Nonnull
    @Override
    public List<String> getEncodings() {
        return encodings;
    }

    @Override
    public void setEncodings(List<String> encodings) {
        this.encodings.clear();
        this.encodings.addAll(encodings);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CharsetListEncodingState.ChangeType.ENCODING_LIST);
        }
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
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, TextFontState.ChangeType.FONT);
        }
    }

    @Nonnull
    @Override
    public CodeType getCodeType() {
        return ((CodeTypeCapable) codeArea).getCodeType();
    }

    @Override
    public void setCodeType(CodeType codeType) {
        ((CodeTypeCapable) codeArea).setCodeType(codeType);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.CODE_TYPE);
        }
    }

    @Nonnull
    @Override
    public PositionCodeType getPositionCodeType() {
        return ((PositionCodeTypeCapable) codeArea).getPositionCodeType();
    }

    @Override
    public void setPositionCodeType(PositionCodeType positionCodeType) {
        ((PositionCodeTypeCapable) codeArea).setPositionCodeType(positionCodeType);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.POSITION_CODE_TYPE);
        }
    }

    @Nonnull
    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return ((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase();
    }

    @Override
    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(codeCharactersCase);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.HEX_CHARACTERS_CASE);
        }
    }

    @Override
    public boolean isShowNonprintables() {
        ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) ((SectCodeArea) codeArea).getPainter();
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            return nonprintablesCodeAreaAssessor.isShowNonprintables();
        }
        return false;
    }

    @Override
    public void setShowNonprintables(boolean showNonprintables) {
        ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) ((SectCodeArea) codeArea).getPainter();
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            nonprintablesCodeAreaAssessor.setShowNonprintables(showNonprintables);
            codeArea.repaint();
            if (contextProvider != null) {
                contextProvider.notifyStateChange(ContextComponent.class, NonprintablesState.ChangeType.NONPRINTABLES);
            }
        }
    }
}
