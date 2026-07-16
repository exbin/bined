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

import java.awt.Component;
import java.awt.Font;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.capability.SelectionCapable;
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
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.action.api.clipboard.TextClipboardOperationController;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.operation.undo.api.UndoRedoController;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarComponent;
import org.exbin.jaguif.text.encoding.CharsetEncodingState;
import org.exbin.jaguif.text.encoding.CharsetListEncodingState;
import org.exbin.jaguif.text.encoding.ContextEncoding;
import org.exbin.jaguif.text.font.TextFontState;
import org.jspecify.annotations.Nullable;

/**
 * Binary data component.
 */
@NullMarked
public class BinEdDataComponent implements ContextComponent, BinaryDataComponent, TextClipboardOperationController, CharsetEncodingState, CharsetListEncodingState, TextFontState, UndoRedoController {

    protected final BinEdComponentPanel binaryComponent;
    protected final CodeAreaCore codeArea;
    protected final List<BinEdComponentExtension> componentExtensions = new ArrayList<>();
    protected BinaryDataUndoRedo undoRedo;
    protected Font defaultFont;
    protected ActiveContextManagement contextManagement;
    protected List<String> encodings = new ArrayList<>();
    protected StatusBar statusBar = null;

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
        defaultFont = ((SectCodeArea) codeArea).getCodeFont();
        codeArea.addDataChangedListener(() -> {
            if (contextManagement != null) {
                contextManagement.updateActiveState(ContextComponent.class, this, UpdateType.DATA_CONTENT);
            }
        });

        ((SelectionCapable) codeArea).addSelectionChangedListener(() -> {
            if (contextManagement != null) {
                contextManagement.updateActiveState(ContextComponent.class, this, UpdateType.SELECTION);
            }
        });

        ((CaretCapable) codeArea).addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            if (contextManagement != null) {
                contextManagement.updateActiveState(ContextComponent.class, this, UpdateType.CURSOR_POSITION);
            }
        });

        ((EditModeCapable) codeArea).addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            if (contextManagement != null) {
                contextManagement.updateActiveState(ContextComponent.class, this, UpdateType.EDIT_MODE);
            }
        });
    }

    public void applySettings(SettingsOptionsProvider settingsOptionsProvider) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        settingsManager.applyContextOptions(ContextComponent.class, this, settingsOptionsProvider);
    }

    @Override
    public Component getComponent() {
        return binaryComponent != null ? binaryComponent : codeArea;
    }

    @Override
    public CodeAreaCore getCodeArea() {
        return codeArea;
    }

    @Override
    public Optional<ActiveContextManagement> getContextManagement() {
        return Optional.ofNullable(contextManagement);
    }

    public void setContextManagement(ActiveContextManagement contextManagement) {
        this.contextManagement = contextManagement;
    }

    public void setContextManager(ActiveContextManagement contextManagement) {
        this.contextManagement = contextManagement;
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
    public boolean isValidForPaste() {
        return codeArea.isEditable() && codeArea.canPaste();
    }

    @Override
    public boolean canDelete() {
        return codeArea.isEditable() && codeArea.hasSelection();
    }

    @Override
    public String getEncoding() {
        return ((CharsetCapable) codeArea).getCharset().name();
    }

    @Override
    public void setEncoding(String encoding) {
        ((CharsetCapable) codeArea).setCharset(Charset.forName(encoding));
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextEncoding.class, this, CharsetEncodingState.UpdateType.ENCODING);
        }
    }

    @Override
    public List<String> getEncodings() {
        return encodings;
    }

    @Override
    public void setEncodings(List<String> encodings) {
        this.encodings.clear();
        this.encodings.addAll(encodings);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextEncoding.class, this, CharsetListEncodingState.UpdateType.ENCODING_LIST);
        }
    }

    @Override
    public Font getCurrentFont() {
        return ((FontCapable) codeArea).getCodeFont();
    }

    @Override
    public Font getDefaultFont() {
        return defaultFont;
    }

    @Override
    public void setCurrentFont(Font font) {
        ((FontCapable) codeArea).setCodeFont(font);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextComponent.class, this, TextFontState.UpdateType.FONT);
        }
    }

    @Override
    public CodeType getCodeType() {
        return ((CodeTypeCapable) codeArea).getCodeType();
    }

    @Override
    public void setCodeType(CodeType codeType) {
        ((CodeTypeCapable) codeArea).setCodeType(codeType);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextComponent.class, this, CodeTypeState.UpdateType.CODE_TYPE);
        }
    }

    @Override
    public PositionCodeType getPositionCodeType() {
        return ((PositionCodeTypeCapable) codeArea).getPositionCodeType();
    }

    @Override
    public void setPositionCodeType(PositionCodeType positionCodeType) {
        ((PositionCodeTypeCapable) codeArea).setPositionCodeType(positionCodeType);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextComponent.class, this, CodeTypeState.UpdateType.POSITION_CODE_TYPE);
        }
    }

    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return ((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase();
    }

    @Override
    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(codeCharactersCase);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextComponent.class, this, CodeTypeState.UpdateType.HEX_CHARACTERS_CASE);
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
            if (contextManagement != null) {
                contextManagement.updateActiveState(ContextComponent.class, this, NonprintablesState.UpdateType.NONPRINTABLES);
            }
        }
    }

    @Override
    public void setEditOperation(EditOperation editOperation) {
        ((EditModeCapable) codeArea).setEditOperation(editOperation);
        if (contextManagement != null) {
            contextManagement.updateActiveState(ContextComponent.class, this, UpdateType.EDIT_MODE);
        }
    }

    @Override
    public Optional<BinaryDataUndoRedo> getUndoRedo() {
        // TODO Replace with context undo
        return Optional.ofNullable(undoRedo);
    }

    public void setUndoRedo(@Nullable BinaryDataUndoRedo undoRedo) {
        this.undoRedo = undoRedo;
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoRedo == null ? new CodeAreaUndoRedo(codeArea) : undoRedo);
        codeArea.setCommandHandler(commandHandler);

        for (BinEdComponentExtension extension : componentExtensions) {
            extension.onUndoHandlerChange();
        }
    }

    public void addComponentExtension(BinEdComponentExtension extension) {
        componentExtensions.add(extension);
        if (undoRedo != null) {
            extension.onUndoHandlerChange();
        }
    }

    public List<BinEdComponentExtension> getComponentExtensions() {
        return componentExtensions;
    }

    public Optional<StatusBar> getStatusBar() {
        return Optional.ofNullable(statusBar);
    }

    public void setStatusBar(@Nullable StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz) {
        for (BinEdComponentExtension extension : componentExtensions) {
            if (clazz.isInstance(extension)) {
                return (T) clazz.cast(extension);
            }
        }

        throw new IllegalStateException("Missing extension: " + clazz.toString());
    }

    @Override
    public <T extends StatusBarComponent> Optional<T> getStatusBarComponent(Class<T> clazz) {
        for (int i = 0; i < statusBar.getItemsCount(); i++) {
            StatusBarComponent component = statusBar.getItem(i);
            if (clazz.isInstance(component)) {
                return Optional.of(clazz.cast(component));
            }
        }
        return Optional.empty();
    }

    public enum UpdateType implements StateUpdateType {
        DATA_CONTENT,
        SELECTION,
        CURSOR_POSITION,
        EDIT_MODE
    }
}
