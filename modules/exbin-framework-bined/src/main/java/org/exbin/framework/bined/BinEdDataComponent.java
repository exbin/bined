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
 * Binary data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDataComponent implements ContextComponent, BinaryDataComponent, TextClipboardController, CharsetEncodingState, CharsetListEncodingState, TextFontState {

    protected final BinEdComponentPanel component;
    protected Font defaultFont;
    protected ActiveContextProvider contextProvider;
    protected List<String> encodings = new ArrayList<>();

    public BinEdDataComponent(BinEdComponentPanel component) {
        this.component = component;
        defaultFont = ((FontCapable) component.getCodeArea()).getCodeFont();
    }

    @Nonnull
    public BinEdComponentPanel getComponent() {
        return component;
    }

    @Nonnull
    @Override
    public CodeAreaCore getCodeArea() {
        return component.getCodeArea();
    }

    public void setContextProvider(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public void performCut() {
        getCodeArea().cut();
    }

    @Override
    public void performCopy() {
        getCodeArea().copy();
    }

    @Override
    public void performPaste() {
        getCodeArea().paste();
    }

    @Override
    public void performDelete() {
        getCodeArea().delete();
    }

    @Override
    public void performSelectAll() {
        getCodeArea().selectAll();
    }

    @Override
    public boolean hasSelection() {
        return getCodeArea().hasSelection();
    }

    @Override
    public boolean hasDataToCopy() {
        return hasSelection();
    }

    @Override
    public boolean isEditable() {
        return getCodeArea().isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public boolean canPaste() {
        return getCodeArea().isEditable() && getCodeArea().canPaste();
    }

    @Override
    public boolean canDelete() {
        return getCodeArea().isEditable();
    }

    @Override
    public void setUpdateListener(ClipboardStateListener updateListener) {
        // componentPanel.setUpdateListener(updateListener);
    }

    @Nonnull
    @Override
    public String getEncoding() {
        return ((CharsetCapable) getCodeArea()).getCharset().name();
    }

    @Override
    public void setEncoding(String encoding) {
        ((CharsetCapable) getCodeArea()).setCharset(Charset.forName(encoding));
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
        return ((FontCapable) getCodeArea()).getCodeFont();
    }

    @Nonnull
    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Override
    public void setCurrentFont(Font font) {
        ((FontCapable) getCodeArea()).setCodeFont(font);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, TextFontState.ChangeType.FONT);
        }
    }

    @Nonnull
    @Override
    public CodeType getCodeType() {
        return ((CodeTypeCapable) getCodeArea()).getCodeType();
    }

    @Override
    public void setCodeType(CodeType codeType) {
        ((CodeTypeCapable) getCodeArea()).setCodeType(codeType);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.CODE_TYPE);
        }
    }

    @Nonnull
    @Override
    public PositionCodeType getPositionCodeType() {
        return ((PositionCodeTypeCapable) getCodeArea()).getPositionCodeType();
    }

    @Override
    public void setPositionCodeType(PositionCodeType positionCodeType) {
        ((PositionCodeTypeCapable) getCodeArea()).setPositionCodeType(positionCodeType);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.POSITION_CODE_TYPE);
        }
    }

    @Nonnull
    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return ((CodeCharactersCaseCapable) getCodeArea()).getCodeCharactersCase();
    }

    @Override
    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        ((CodeCharactersCaseCapable) getCodeArea()).setCodeCharactersCase(codeCharactersCase);
        if (contextProvider != null) {
            contextProvider.notifyStateChange(ContextComponent.class, CodeTypeState.ChangeType.HEX_CHARACTERS_CASE);
        }
    }

    @Override
    public boolean isShowNonprintables() {
        ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) ((SectCodeArea) getCodeArea()).getPainter();
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            return nonprintablesCodeAreaAssessor.isShowNonprintables();
        }
        return false;
    }

    @Override
    public void setShowNonprintables(boolean showNonprintables) {
        ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) ((SectCodeArea) getCodeArea()).getPainter();
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            nonprintablesCodeAreaAssessor.setShowNonprintables(showNonprintables);
            getCodeArea().repaint();
            if (contextProvider != null) {
                contextProvider.notifyStateChange(ContextComponent.class, NonprintablesState.ChangeType.NONPRINTABLES);
            }
        }
    }
}
