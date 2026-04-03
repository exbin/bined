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
package org.exbin.bined.jaguif.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.delta.SegmentsRepository;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.swing.CodeAreaUndoRedo;
import org.exbin.bined.swing.CodeAreaColorAssessor;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ContextComponent;
import org.exbin.jaguif.action.api.DeletionController;
import org.exbin.jaguif.action.api.SelectionController;
import org.exbin.jaguif.action.api.clipboard.ClipboardController;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.component.gui.BinaryStatusPanel;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.text.encoding.EncodingsManager;
import org.exbin.jaguif.operation.undo.api.ContextUndoRedo;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.operation.undo.api.UndoRedoState;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;

/**
 * File manager for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileManager {

    private BinaryStatus binaryStatus = new BinaryStatus();
    private final SegmentsRepository segmentsRepository = new SegmentsRepository(() -> new ByteArrayEditableData());
    private final List<BinEdFileExtension> binEdComponentExtensions = new ArrayList<>();
    private final List<CodeAreaColorAssessor> painterPositionColorModifiers = new ArrayList<>();
    private final List<CodeAreaColorAssessor> painterPriorityPositionColorModifiers = new ArrayList<>();
    private CodeAreaCommandHandlerProvider commandHandlerProvider = null;

    public BinEdFileManager() {
    }

    public void initDataComponent(BinEdDataComponent binaryDataComponent) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ActiveContextManagement contextManager = frameModule.getFrameHandler().getContextManager();
        BinEdComponentPanel componentPanel = (BinEdComponentPanel) binaryDataComponent.getComponent();
        for (BinEdFileExtension fileExtension : binEdComponentExtensions) {
            Optional<BinEdComponentExtension> componentExtension = fileExtension.createComponentExtension(componentPanel);
            if (componentExtension.isPresent()) {
                BinEdComponentExtension extension = componentExtension.get();
                extension.onCreate(binaryDataComponent);
                binaryDataComponent.addComponentExtension(extension);
            }
        }
        CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
        ((SelectionCapable) codeArea).addSelectionChangedListener(() -> {
            ContextComponent component = contextManager.getActiveState(ContextComponent.class);
            if (component == binaryDataComponent) {
                contextManager.updateActiveState(ContextComponent.class, component, SelectionController.UpdateType.CONTENT_STATE);
                contextManager.updateActiveState(ContextComponent.class, component, ClipboardController.UpdateType.CONTENT_STATE);
                contextManager.updateActiveState(ContextComponent.class, component, DeletionController.UpdateType.CONTENT_STATE);
            }
        });
        CodeAreaUndoRedo codeAreaUndoRedo = new CodeAreaUndoRedo(codeArea);
        codeAreaUndoRedo.addChangeListener(() -> {
            ContextUndoRedo undoRedo = contextManager.getActiveState(ContextUndoRedo.class);
            if (undoRedo == binaryDataComponent) {
                contextManager.updateActiveState(ContextUndoRedo.class, undoRedo, UndoRedoState.UpdateType.UNDO_REDO_STATE);
            }
        });
        binaryDataComponent.setUndoRedo(codeAreaUndoRedo);

        BinEdCodeAreaAssessor painter = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) componentPanel.getCodeArea().getPainter(), BinEdCodeAreaAssessor.class);
        for (CodeAreaColorAssessor modifier : painterPriorityPositionColorModifiers) {
            painter.addPriorityColorModifier(modifier);
        }
        for (CodeAreaColorAssessor modifier : painterPositionColorModifiers) {
            painter.addColorModifier(modifier);
        }
        binaryStatus.attachCodeArea(binaryDataComponent);
        binaryDataComponent.setContextProvider(contextManager);

        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = settingsManager.getSettingsOptionsProvider();
        applySettings(settingsOptionsProvider);
    }

    public void applySettings(SettingsOptionsProvider settingsOptionsProvider) {
        binaryStatus.applySettings(settingsOptionsProvider);
    }
    
    public void initCommandHandler(BinEdDataComponent binaryDataComponent) {
        CodeAreaCore codeArea = binaryDataComponent.getCodeArea();
        CodeAreaOperationCommandHandler commandHandler;
        if (commandHandlerProvider != null) {
            commandHandler = commandHandlerProvider.createCommandHandler(codeArea, binaryDataComponent.getUndoRedo().orElse(null));
        } else {
            commandHandler = new CodeAreaOperationCommandHandler(codeArea, binaryDataComponent.getUndoRedo().orElse(null));
        }
        codeArea.setCommandHandler(commandHandler);
    }

    @Nonnull
    public BinaryStatus getBinaryStatus() {
        return binaryStatus;
    }

    @Nonnull
    public SegmentsRepository getSegmentsRepository() {
        return segmentsRepository;
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
        binaryStatus.setBinaryStatusPanel(binaryStatusPanel);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.registerStatusBar(BinedComponentModule.MODULE_ID, BinedComponentModule.BINARY_STATUS_BAR_ID, binaryStatusPanel);
        frameModule.switchStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID);
    }

    public void updateTextEncodingStatus(EncodingsManager encodingsHandler) {
        binaryStatus.updateEncodingState();
    }

    public void setBinaryStatusController(BinaryStatusPanel.Controller binaryStatusController) {
        binaryStatus.setBinaryStatusController(binaryStatusController);
    }

    public void setCommandHandlerProvider(CodeAreaCommandHandlerProvider commandHandlerProvider) {
        this.commandHandlerProvider = commandHandlerProvider;
    }

    public void addBinEdComponentExtension(BinEdFileExtension extension) {
        binEdComponentExtensions.add(extension);
    }

    @Nonnull
    public Iterable<BinEdFileExtension> getBinEdComponentExtensions() {
        return binEdComponentExtensions;
    }

    @ParametersAreNonnullByDefault
    public interface BinEdFileExtension {

        @Nonnull
        Optional<BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component);
    }
}
