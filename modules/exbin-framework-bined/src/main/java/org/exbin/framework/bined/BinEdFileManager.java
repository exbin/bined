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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.options.impl.StatusOptionsImpl;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.bined.preferences.CodeAreaPreferences;
import org.exbin.framework.bined.preferences.StatusPreferences;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;
import org.exbin.framework.frame.api.FrameModuleApi;

/**
 * File manager for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileManager {

    private XBApplication application;
    private EditorProvider editorProvider;

    private BinaryStatusPanel binaryStatusPanel;
    private final SegmentsRepository segmentsRepository = new SegmentsRepository();
    private final List<BinEdFileExtension> binEdComponentExtensions = new ArrayList<>();
    private final List<ActionStatusUpdateListener> actionStatusUpdateListeners = new ArrayList<>();
    private final List<BinEdCodeAreaPainter.PositionColorModifier> painterPositionColorModifiers = new ArrayList<>();
    private final List<BinEdCodeAreaPainter.PositionColorModifier> painterPriorityPositionColorModifiers = new ArrayList<>();

    public BinEdFileManager() {
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void setEditorProvider(EditorProvider editorProvider) {
        this.editorProvider = editorProvider;
    }

    public void initFileHandler(BinEdFileHandler fileHandler) {
        fileHandler.setApplication(application);
        fileHandler.setSegmentsRepository(segmentsRepository);
        BinEdComponentPanel componentPanel = fileHandler.getComponent();

        for (BinEdFileExtension fileExtension : binEdComponentExtensions) {
            Optional<BinEdComponentPanel.BinEdComponentExtension> componentExtension = fileExtension.createComponentExtension(componentPanel);
            componentExtension.ifPresent((extension) -> {
                extension.setApplication(application);
                extension.onCreate(componentPanel);
                componentPanel.addComponentExtension(extension);
            });
        }

        BinEdCodeAreaPainter painter = (BinEdCodeAreaPainter) componentPanel.getCodeArea().getPainter();
        for (BinEdCodeAreaPainter.PositionColorModifier modifier : painterPriorityPositionColorModifiers) {
            painter.addPriorityColorModifier(modifier);
        }
        for (BinEdCodeAreaPainter.PositionColorModifier modifier : painterPositionColorModifiers) {
            painter.addColorModifier(modifier);
        }

        Preferences preferences = application.getAppPreferences();
        BinaryEditorPreferences binaryEditorPreferences = new BinaryEditorPreferences(preferences);
        String encoding = binaryEditorPreferences.getEncodingPreferences().getSelectedEncoding();
        if (!encoding.isEmpty()) {
            fileHandler.setCharset(Charset.forName(encoding));
        }
        TextFontPreferences textFontPreferences = binaryEditorPreferences.getFontPreferences();
        ExtCodeArea codeArea = fileHandler.getCodeArea();
        ((FontCapable) codeArea).setCodeFont(textFontPreferences.isUseDefaultFont() ? CodeAreaPreferences.DEFAULT_FONT : textFontPreferences.getFont(CodeAreaPreferences.DEFAULT_FONT));
    }

    public void addPainterColorModifier(BinEdCodeAreaPainter.PositionColorModifier modifier) {
        painterPositionColorModifiers.add(modifier);
    }

    public void removePainterColorModifier(BinEdCodeAreaPainter.PositionColorModifier modifier) {
        painterPositionColorModifiers.remove(modifier);
    }

    public void addPainterPriorityColorModifier(BinEdCodeAreaPainter.PositionColorModifier modifier) {
        painterPriorityPositionColorModifiers.add(modifier);
    }

    public void removePainterPriorityColorModifier(BinEdCodeAreaPainter.PositionColorModifier modifier) {
        painterPriorityPositionColorModifiers.remove(modifier);
    }

    public void registerStatusBar() {
        binaryStatusPanel = new BinaryStatusPanel();
        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        frameModule.registerStatusBar(BinedModule.MODULE_ID, BinedModule.BINARY_STATUS_BAR_ID, binaryStatusPanel);
        frameModule.switchStatusBar(BinedModule.BINARY_STATUS_BAR_ID);
        ((BinEdEditorProvider) editorProvider).registerBinaryStatus(binaryStatusPanel);
        ((BinEdEditorProvider) editorProvider).registerEncodingStatus(binaryStatusPanel);
    }

    public void updateTextEncodingStatus(EncodingsHandler encodingsHandler) {
        if (binaryStatusPanel != null) {
            encodingsHandler.setTextEncodingStatus(binaryStatusPanel);
        }
    }

    public void updateActionStatus(@Nullable CodeAreaCore codeArea) {
        for (ActionStatusUpdateListener listener : actionStatusUpdateListeners) {
            listener.updateActionStatus(codeArea);
        }
    }

    public void applyPreferencesChanges(StatusOptionsImpl options) {
        binaryStatusPanel.setStatusOptions(options);
    }

    public void setStatusControlHandler(BinaryStatusPanel.StatusControlHandler statusControlHandler) {
        binaryStatusPanel.setStatusControlHandler(statusControlHandler);
    }

    public void addBinEdComponentExtension(BinEdFileExtension extension) {
        binEdComponentExtensions.add(extension);
    }

    public void addActionStatusUpdateListener(ActionStatusUpdateListener listener) {
        actionStatusUpdateListeners.add(listener);
    }

    public void loadFromPreferences(Preferences preferences) {
        binaryStatusPanel.loadFromPreferences(new StatusPreferences(preferences));
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

    @ParametersAreNonnullByDefault
    public interface ActionStatusUpdateListener {

        void updateActionStatus(CodeAreaCore codeArea);
    }
}
