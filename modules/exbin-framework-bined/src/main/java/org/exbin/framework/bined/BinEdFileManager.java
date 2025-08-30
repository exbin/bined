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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.delta.SegmentsRepository;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.swing.CodeAreaColorAssessor;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.text.font.options.TextFontOptions;

/**
 * File manager for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileManager {

    private EditorProvider editorProvider;

    private BinaryStatusPanel binaryStatusPanel;
    private final SegmentsRepository segmentsRepository = new SegmentsRepository(() -> new ByteArrayEditableData());
    private final List<BinEdFileExtension> binEdComponentExtensions = new ArrayList<>();
    private final List<CodeAreaColorAssessor> painterPositionColorModifiers = new ArrayList<>();
    private final List<CodeAreaColorAssessor> painterPriorityPositionColorModifiers = new ArrayList<>();
    private CodeAreaCommandHandlerProvider commandHandlerProvider = null;

    public BinEdFileManager() {
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void initFileHandler(BinEdFileHandler fileHandler) {
        fileHandler.setSegmentsRepository(segmentsRepository);
        BinEdComponentPanel componentPanel = fileHandler.getComponent();
        initComponentPanel(componentPanel);
    }

    public void initComponentPanel(BinEdComponentPanel componentPanel) {
        for (BinEdFileExtension fileExtension : binEdComponentExtensions) {
            Optional<BinEdComponentPanel.BinEdComponentExtension> componentExtension = fileExtension.createComponentExtension(componentPanel);
            if (componentExtension.isPresent()) {
                BinEdComponentPanel.BinEdComponentExtension extension = componentExtension.get();
                extension.onCreate(componentPanel);
                componentPanel.addComponentExtension(extension);
            }
        }

        BinEdCodeAreaAssessor painter = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) componentPanel.getCodeArea().getPainter(), BinEdCodeAreaAssessor.class);
        for (CodeAreaColorAssessor modifier : painterPriorityPositionColorModifiers) {
            painter.addPriorityColorModifier(modifier);
        }
        for (CodeAreaColorAssessor modifier : painterPositionColorModifiers) {
            painter.addColorModifier(modifier);
        }
    }

    public void initCommandHandler(BinEdComponentPanel componentPanel) {
        SectCodeArea codeArea = componentPanel.getCodeArea();
        CodeAreaOperationCommandHandler commandHandler;
        if (commandHandlerProvider != null) {
            commandHandler = commandHandlerProvider.createCommandHandler(codeArea, componentPanel.getUndoRedo().orElse(null));
        } else {
            commandHandler = new CodeAreaOperationCommandHandler(codeArea, componentPanel.getUndoRedo().orElse(null));
        }
        codeArea.setCommandHandler(commandHandler);
    }

    public void addPainterColorModifier(CodeAreaColorAssessor modifier) {
        painterPositionColorModifiers.add(modifier);
    }

    public void removePainterColorModifier(CodeAreaColorAssessor modifier) {
        painterPositionColorModifiers.remove(modifier);
    }

    public void addPainterPriorityColorModifier(CodeAreaColorAssessor modifier) {
        painterPriorityPositionColorModifiers.add(modifier);
    }

    public void removePainterPriorityColorModifier(CodeAreaColorAssessor modifier) {
        painterPriorityPositionColorModifiers.remove(modifier);
    }

    public void registerStatusBar() {
        registerStatusBar(new BinaryStatusPanel());
    }

    public void registerStatusBar(BinaryStatusPanel binaryStatusPanel) {
        this.binaryStatusPanel = binaryStatusPanel;
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.registerStatusBar(BinedModule.MODULE_ID, BinedModule.BINARY_STATUS_BAR_ID, binaryStatusPanel);
        frameModule.switchStatusBar(BinedModule.BINARY_STATUS_BAR_ID);
        if (editorProvider instanceof BinEdEditorProvider) {
            ((BinEdEditorProvider) editorProvider).registerBinaryStatus(binaryStatusPanel);
            ((BinEdEditorProvider) editorProvider).registerEncodingStatus(binaryStatusPanel);
        }
    }

    public void updateTextEncodingStatus(EncodingsHandler encodingsHandler) {
        if (binaryStatusPanel != null) {
            encodingsHandler.setTextEncodingStatus(binaryStatusPanel);
        }
    }

    public void applyPreferencesChanges(StatusOptions options) {
        if (binaryStatusPanel != null) {
            binaryStatusPanel.setStatusOptions(options);
        }
    }

    public void setStatusControlHandler(BinaryStatusPanel.Controller statusControlHandler) {
        binaryStatusPanel.setController(statusControlHandler);
    }

    public void setCommandHandlerProvider(CodeAreaCommandHandlerProvider commandHandlerProvider) {
        this.commandHandlerProvider = commandHandlerProvider;
    }

    public void addBinEdComponentExtension(BinEdFileExtension extension) {
        binEdComponentExtensions.add(extension);
    }

    public void loadFromOptions(OptionsStorage options) {
        if (binaryStatusPanel != null) {
            binaryStatusPanel.loadFromOptions(new StatusOptions(options));
        }
    }

    @Nonnull
    public Iterable<BinEdFileExtension> getBinEdComponentExtensions() {
        return binEdComponentExtensions;
    }

    @Nullable
    public BinaryStatusPanel getBinaryStatusPanel() {
        return binaryStatusPanel;
    }

    @ParametersAreNonnullByDefault
    public interface BinEdFileExtension {

        @Nonnull
        Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component);
    }
}
