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
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoRedo;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ContextComponent;
import org.exbin.framework.action.api.clipboard.ClipboardStateListener;
import org.exbin.framework.action.api.clipboard.TextClipboardController;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.operation.undo.api.UndoRedoController;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.text.encoding.CharsetEncodingState;
import org.exbin.framework.text.encoding.CharsetListEncodingState;
import org.exbin.framework.text.font.TextFontState;

/**
 * Binary data binaryComponent.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDataComponent implements ContextComponent, BinaryDataComponent, TextClipboardController, CharsetEncodingState, CharsetListEncodingState, TextFontState, UndoRedoController {

    protected final BinEdComponentPanel binaryComponent;
    protected final CodeAreaCore codeArea;
    protected final List<BinEdComponentExtension> componentExtensions = new ArrayList<>();
    protected BinaryDataUndoRedo undoRedo;
    protected Font defaultFont;
    protected ActiveContextProvider contextProvider;
    protected List<String> encodings = new ArrayList<>();

    public BinEdDataComponent(BinEdComponentPanel binaryComponent) {
        this.binaryComponent = binaryComponent;
        this.codeArea = binaryComponent.getCodeArea();
        init();
    }

    public BinEdDataComponent(CodeAreaCore codeArea) {
        this.binaryComponent = null;
        this.codeArea = codeArea;
        init();
    }
    
    private void init() {
        defaultFont = ((FontCapable) codeArea).getCodeFont();
    }
    
    public void applySettings(SettingsOptionsProvider settingsOptionsProvider) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        settingsManager.applyOptions(ContextComponent.class, this, settingsOptionsProvider);
    }

    @Nonnull
    @Override
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
    public boolean canUndo() {
        return undoRedo != null ? undoRedo.canUndo() : false;
    }

    @Override
    public boolean canRedo() {
        return undoRedo != null ? undoRedo.canRedo() : false;
    }

    @Override
    public void performUndo() {
        undoRedo.performUndo();
    }

    @Override
    public void performRedo() {
        undoRedo.performRedo();
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

    @Nonnull
    @Override
    public Optional<BinaryDataUndoRedo> getUndoRedo() {
        // TODO Replace with context undo
        return Optional.ofNullable(undoRedo);
    }

    public void setUndoRedo(BinaryDataUndoRedo undoRedo) {
        this.undoRedo = undoRedo;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoRedo == null ? new CodeAreaUndoRedo(codeArea) : undoRedo);
        codeArea.setCommandHandler(commandHandler);

        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onUndoHandlerChange();
        }
        // TODO set ENTER KEY mode in apply options

    }

    public void addComponentExtension(BinEdComponentExtension extension) {
        componentExtensions.add(extension);
        if (undoRedo != null) {
            extension.onUndoHandlerChange();
        }
    }

    @Nonnull
    public List<BinEdComponentExtension> getComponentExtensions() {
        return componentExtensions;
    }

    @Nonnull
    @Override
    public <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz) {
        for (BinEdComponentExtension extension : componentExtensions) {
            if (clazz.isInstance(extension)) {
                return (T) clazz.cast(extension);
            }
        }

        throw new IllegalStateException("Missing extension: " + clazz.toString());
    }
}
