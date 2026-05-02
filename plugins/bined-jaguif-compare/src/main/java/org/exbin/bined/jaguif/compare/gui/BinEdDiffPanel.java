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
package org.exbin.bined.jaguif.compare.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.nio.charset.Charset;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.basic.color.CodeAreaColorsProfile;
import org.exbin.bined.swing.capability.CharAssessorPainterCapable;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.diff.SectCodeAreaDiffPanel;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.jaguif.App;
import org.exbin.bined.jaguif.component.BinEdCodeAreaAssessor;
import org.exbin.bined.jaguif.component.BinEdComponentExtension;
import org.exbin.bined.jaguif.component.BinEdDataComponent.UpdateType;
import org.exbin.bined.jaguif.component.BinaryDataComponent;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.editor.settings.BinaryEditorOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaColorOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaLayoutOptions;
import org.exbin.bined.jaguif.theme.settings.CodeAreaThemeOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaOptions;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaViewerSettingsApplier;
import org.exbin.bined.operation.command.BinaryDataUndoRedo;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.text.encoding.CharsetEncodingState;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.font.settings.TextFontOptions;

/**
 * BinEd diff panel to compare binary files.
 */
@ParametersAreNonnullByDefault
public class BinEdDiffPanel extends JPanel {

    protected final OptionsStorage optionsStorage;
    protected final SectCodeAreaDiffPanel diffPanel = new SectCodeAreaDiffPanel();

    protected final Font defaultFont;
    protected final SectionCodeAreaLayoutProfile defaultLayoutProfile;
    protected final SectionCodeAreaThemeProfile defaultThemeProfile;
    protected final CodeAreaColorsProfile defaultColorProfile;

    protected ContextComponent leftContextComponent;
    protected ContextComponent rightContextComponent;
    protected ContextRegistration leftContextRegistrator;
    protected ContextRegistration rightContextRegistrator;
    protected final DiffToolbarPanel toolbarPanel;
    protected final StatusBar leftStatusBar;
    protected final StatusBar rightStatusBar;
    protected GoToPositionAction goToPositionAction = new GoToPositionAction();

    public BinEdDiffPanel() {
        setLayout(new java.awt.BorderLayout());

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        optionsStorage = optionsModule.getAppOptions();
        defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        SectCodeArea leftCodeArea = diffPanel.getLeftCodeArea();
        SectCodeArea rightCodeArea = diffPanel.getRightCodeArea();

        CodeAreaPainter leftPainter = leftCodeArea.getPainter();
        BinEdCodeAreaAssessor codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable) leftPainter).getColorAssessor(), ((CharAssessorPainterCapable) leftPainter).getCharAssessor());
        ((ColorAssessorPainterCapable) leftPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable) leftPainter).setCharAssessor(codeAreaAssessor);
        CodeAreaPainter rightPainter = rightCodeArea.getPainter();
        codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable) rightPainter).getColorAssessor(), ((CharAssessorPainterCapable) rightPainter).getCharAssessor());
        ((ColorAssessorPainterCapable) rightPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable) rightPainter).setCharAssessor(codeAreaAssessor);

        defaultLayoutProfile = leftCodeArea.getLayoutProfile();
        defaultThemeProfile = leftCodeArea.getThemeProfile();
        defaultColorProfile = leftCodeArea.getColorsProfile();
        toolbarPanel = new DiffToolbarPanel();
        leftContextComponent = new DiffContextComponent(leftCodeArea);
        rightContextComponent = new DiffContextComponent(rightCodeArea);
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ActiveContextManagement leftContextManager = contextModule.createContextManager();
        ActiveContextManagement rightContextManager = contextModule.createContextManager();
        attachContext(leftCodeArea, leftContextComponent, leftContextManager);
        attachContext(rightCodeArea, rightContextComponent, rightContextManager);
        ContextUpdateManagement leftUpdateManagement = contextModule.createContextUpdateManagement(leftContextManager);
        leftUpdateManagement.addGroup(BinedComponentModule.BINARY_STATUS_BAR_ID);
        ContextUpdateManagement rightUpdateManagement = contextModule.createContextUpdateManagement(rightContextManager);
        rightUpdateManagement.addGroup(BinedComponentModule.BINARY_STATUS_BAR_ID);
        leftContextRegistrator = contextModule.createContextRegistrator(BinedComponentModule.BINARY_STATUS_BAR_ID, leftUpdateManagement, leftContextManager);
        rightContextRegistrator = contextModule.createContextRegistrator(BinedComponentModule.BINARY_STATUS_BAR_ID, rightUpdateManagement, rightContextManager);
        leftStatusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, leftContextRegistrator);
        rightStatusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, rightContextRegistrator);
        toolbarPanel.setTargetComponent(diffPanel);
        toolbarPanel.setController(new DiffToolbarPanel.Controller() {
            @Nonnull
            @Override
            public CodeType getCodeType() {
                return leftCodeArea.getCodeType();
            }

            @Override
            public void setCodeType(CodeType codeType) {
                leftCodeArea.setCodeType(codeType);
                rightCodeArea.setCodeType(codeType);
            }

            @Override
            public boolean isShowNonprintables() {
                ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) leftCodeArea.getPainter();
                NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
                return CodeAreaUtils.requireNonNull(nonprintablesCodeAreaAssessor).isShowNonprintables();
            }

            @Override
            public void setShowNonprintables(boolean showNonprintables) {
                ColorAssessorPainterCapable leftPainter = (ColorAssessorPainterCapable) leftCodeArea.getPainter();
                NonprintablesCodeAreaAssessor leftNonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(leftPainter, NonprintablesCodeAreaAssessor.class);
                CodeAreaUtils.requireNonNull(leftNonprintablesCodeAreaAssessor).setShowNonprintables(showNonprintables);
                ColorAssessorPainterCapable rightPainter = (ColorAssessorPainterCapable) rightCodeArea.getPainter();
                NonprintablesCodeAreaAssessor rightNonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(rightPainter, NonprintablesCodeAreaAssessor.class);
                CodeAreaUtils.requireNonNull(rightNonprintablesCodeAreaAssessor).setShowNonprintables(showNonprintables);
            }

            @Override
            public void repaint() {
                diffPanel.repaint();
            }
        });

        init();
    }

    private void init() {
        this.add(toolbarPanel, BorderLayout.NORTH);
        goToPositionAction.init(App.getModule(LanguageModuleApi.class).getBundle(BinedComponentModule.class));

        // TODO registerBinaryStatus(leftStatusBar, diffPanel.getLeftCodeArea());
        // TODO registerBinaryStatus(rightStatusBar, diffPanel.getRightCodeArea());
        initialLoadFromPreferences();
        BinedComponentModule binedComponentModule = App.getModule(BinedComponentModule.class);
        JPopupMenu codeAreaPopupMenu = binedComponentModule.createCodeAreaPopupMenu();
        diffPanel.getLeftCodeArea().setComponentPopupMenu(codeAreaPopupMenu);
        diffPanel.getRightCodeArea().setComponentPopupMenu(codeAreaPopupMenu);

        diffPanel.getLeftPanel().add(leftStatusBar.getComponent(), BorderLayout.SOUTH);
        diffPanel.getRightPanel().add(rightStatusBar.getComponent(), BorderLayout.SOUTH);
        this.add(diffPanel, BorderLayout.CENTER);
        diffPanel.revalidate();
        diffPanel.repaint();
        revalidate();
        repaint();
    }

    private static void attachContext(SectCodeArea codeArea, ContextComponent contextComponent, ActiveContextManagement contextManagement) {
        contextManagement.changeActiveState(ContextComponent.class, contextComponent);
        codeArea.addDataChangedListener(() -> {
            contextManagement.updateActiveState(ContextComponent.class, contextComponent, UpdateType.DATA_CHANGED);
        });

        ((SelectionCapable) codeArea).addSelectionChangedListener(() -> {
            contextManagement.updateActiveState(ContextComponent.class, contextComponent, UpdateType.SELECTION_CHANGED);
        });

        ((CaretCapable) codeArea).addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            contextManagement.updateActiveState(ContextComponent.class, contextComponent, UpdateType.CURSOR_MOVED);
        });

        ((EditModeCapable) codeArea).addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            contextManagement.updateActiveState(ContextComponent.class, contextComponent, UpdateType.EDIT_MODE_CHANGED);
        });
    }

    private void initialLoadFromPreferences() {
        applyOptions(new BinEdApplyOptions() {
            @Nonnull
            @Override
            public CodeAreaOptions getCodeAreaOptions() {
                return new CodeAreaOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public TextEncodingOptions getEncodingOptions() {
                return new TextEncodingOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public TextFontOptions getFontOptions() {
                return new TextFontOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public BinaryEditorOptions getEditorOptions() {
                return new BinaryEditorOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public CodeAreaStatusOptions getStatusOptions() {
                return new CodeAreaStatusOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public CodeAreaLayoutOptions getLayoutOptions() {
                return new CodeAreaLayoutOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public CodeAreaColorOptions getColorOptions() {
                return new CodeAreaColorOptions(optionsStorage);
            }

            @Nonnull
            @Override
            public CodeAreaThemeOptions getThemeOptions() {
                return new CodeAreaThemeOptions(optionsStorage);
            }
        });

        // TODO encodingsManager.loadFromOptions(new TextEncodingOptions(optionsStorage));
        // TODO leftStatusBar.loadFromOptions(new CodeAreaStatusOptions(optionsStorage));
        // TODO rightStatusBar.loadFromOptions(new CodeAreaStatusOptions(optionsStorage));
        toolbarPanel.loadFromOptions(optionsStorage);

//        BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
//        leftStatusBar.setMemoryMode(memoryMode);
//        rightStatusBar.setMemoryMode(memoryMode);
    }

    private void applyOptions(BinEdApplyOptions applyOptions) {
        applyOptions(applyOptions, diffPanel.getLeftCodeArea());
        applyOptions(applyOptions, diffPanel.getRightCodeArea());
    }

    private void applyOptions(BinEdApplyOptions applyOptions, SectCodeArea codeArea) {
        CodeAreaViewerSettingsApplier.applyToCodeArea(applyOptions.getCodeAreaOptions(), codeArea);

        ((CharsetCapable) codeArea).setCharset(Charset.forName(applyOptions.getEncodingOptions()
                .getSelectedEncoding()));
        // TODO encodingsManager.setEncodings(applyOptions.getEncodingOptions().getEncodings());
        ((FontCapable) codeArea).setCodeFont(
                applyOptions.getFontOptions().isUseDefaultFont() ? defaultFont : applyOptions.getFontOptions().getFont(defaultFont)
        );

        BinaryEditorOptions editorOptions = applyOptions.getEditorOptions();
        //        switchShowValuesPanel(editorOptions.isShowValuesPanel());
        if (codeArea.getCommandHandler() instanceof CodeAreaOperationCommandHandler) {
            ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
        }

        CodeAreaStatusOptions statusOptions = applyOptions.getStatusOptions();
//        leftStatusBar.setStatusOptions(statusOptions);
//        rightStatusBar.setStatusOptions(statusOptions);
        toolbarPanel.applyFromCodeArea();

        CodeAreaLayoutOptions layoutOptions = applyOptions.getLayoutOptions();
        int selectedLayoutProfile = layoutOptions.getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(layoutOptions.getLayoutProfile(selectedLayoutProfile));
        } else {
            codeArea.setLayoutProfile(defaultLayoutProfile);
        }

        CodeAreaThemeOptions themeOptions = applyOptions.getThemeOptions();
        int selectedThemeProfile = themeOptions.getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(themeOptions.getThemeProfile(selectedThemeProfile));
        } else {
            codeArea.setThemeProfile(defaultThemeProfile);
        }

        CodeAreaColorOptions colorOptions = applyOptions.getColorOptions();
        int selectedColorProfile = colorOptions.getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(colorOptions.getColorsProfile(selectedColorProfile));
        } else {
            codeArea.setColorsProfile(defaultColorProfile);
        }
    }

    public void setLeftContentData(BinaryData contentData) {
        diffPanel.setLeftContentData(contentData);
        // TODO updateBinaryStatus(leftStatusBar, diffPanel.getLeftCodeArea());
    }

    public void setRightContentData(BinaryData contentData) {
        diffPanel.setRightContentData(contentData);
        // TODO updateBinaryStatus(rightStatusBar, diffPanel.getRightCodeArea());
    }

    public interface BinEdApplyOptions {

        @Nonnull
        CodeAreaOptions getCodeAreaOptions();

        @Nonnull
        TextEncodingOptions getEncodingOptions();

        @Nonnull
        TextFontOptions getFontOptions();

        @Nonnull
        BinaryEditorOptions getEditorOptions();

        @Nonnull
        CodeAreaStatusOptions getStatusOptions();

        @Nonnull
        CodeAreaLayoutOptions getLayoutOptions();

        @Nonnull
        CodeAreaColorOptions getColorOptions();

        @Nonnull
        CodeAreaThemeOptions getThemeOptions();
    }

    public static class DiffContextComponent implements BinaryDataComponent, CharsetEncodingState {

        SectCodeArea codeArea;

        public DiffContextComponent(SectCodeArea codeArea) {
            this.codeArea = codeArea;
        }

        @Nonnull
        @Override
        public CodeAreaCore getCodeArea() {
            return codeArea;
        }

        @Override
        public Optional<BinaryDataUndoRedo> getUndoRedo() {
            return Optional.empty();
        }

        @Override
        public <T extends BinEdComponentExtension> T getComponentExtension(Class<T> clazz) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Component getComponent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CodeType getCodeType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setCodeType(CodeType codeType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PositionCodeType getPositionCodeType() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPositionCodeType(PositionCodeType positionCodeType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public CodeCharactersCase getCodeCharactersCase() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isShowNonprintables() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setShowNonprintables(boolean showNonprintables) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Nonnull
        @Override
        public String getEncoding() {
            return ((CharsetCapable) getCodeArea()).getCharset().toString();
        }

        @Override
        public void setEncoding(String encodingName) {
            ((CharsetCapable) getCodeArea()).setCharset(Charset.forName(encodingName));
        }
    }
}
