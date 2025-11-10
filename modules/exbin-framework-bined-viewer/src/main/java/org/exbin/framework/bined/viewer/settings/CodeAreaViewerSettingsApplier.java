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
package org.exbin.framework.bined.viewer.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.capability.CodeCharactersCaseCapable;
import org.exbin.bined.capability.CodeTypeCapable;
import org.exbin.bined.capability.ViewModeCapable;
import org.exbin.bined.highlight.swing.NonAsciiCodeAreaColorAssessor;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.section.capability.PositionCodeTypeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.BinaryDataComponent;
import org.exbin.framework.options.settings.api.SettingsApplier;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Code area viewer settings applier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaViewerSettingsApplier implements SettingsApplier {

    public static final String APPLIER_ID = "codeAreaViewer";

    @Override
    public void applySettings(Object instance, SettingsOptionsProvider settingsOptionsProvider) {
        if (!(instance instanceof BinaryDataComponent)) {
            return;
        }
        CodeAreaOptions options = settingsOptionsProvider.getSettingsOptions(CodeAreaOptions.class);
        
        CodeAreaCore codeArea = ((BinaryDataComponent) instance).getCodeArea();
        applyFromCodeArea(options, (SectCodeArea) codeArea);
        ((CodeTypeCapable) codeArea).setCodeType(options.getCodeType());
        ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(options.getCodeCharactersCase());
        ((PositionCodeTypeCapable) codeArea).setPositionCodeType(options.getPositionCodeType());
        ((ViewModeCapable) codeArea).setViewMode(options.getViewMode());
    }
    
    public static void applyFromCodeArea(CodeAreaOptions codeAreaOptions, SectCodeArea codeArea) {
        CodeAreaPainter painter = codeArea.getPainter();
        codeAreaOptions.setCodeType(((CodeTypeCapable) codeArea).getCodeType());
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            codeAreaOptions.setShowNonprintables(nonprintablesCodeAreaAssessor.isShowNonprintables());
        }
        codeAreaOptions.setCodeCharactersCase(((CodeCharactersCaseCapable) codeArea).getCodeCharactersCase());
        codeAreaOptions.setPositionCodeType(((PositionCodeTypeCapable) codeArea).getPositionCodeType());
        codeAreaOptions.setViewMode(((ViewModeCapable) codeArea).getViewMode());
        NonAsciiCodeAreaColorAssessor nonAsciiColorAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonAsciiCodeAreaColorAssessor.class);
        if (nonAsciiColorAssessor != null) {
            codeAreaOptions.setCodeColorization(nonAsciiColorAssessor.isNonAsciiHighlightingEnabled());
        }
        codeAreaOptions.setRowWrappingMode(codeArea.getRowWrapping());
        codeAreaOptions.setMaxBytesPerRow(codeArea.getMaxBytesPerRow());
        codeAreaOptions.setMinRowPositionLength(codeArea.getMinRowPositionLength());
        codeAreaOptions.setMaxRowPositionLength(codeArea.getMaxRowPositionLength());
    }

    public static void applyToCodeArea(CodeAreaOptions codeAreaOptions, SectCodeArea codeArea) {
        CodeAreaPainter painter = codeArea.getPainter();
        ((CodeTypeCapable) codeArea).setCodeType(codeAreaOptions.getCodeType());
        NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonprintablesCodeAreaAssessor.class);
        if (nonprintablesCodeAreaAssessor != null) {
            nonprintablesCodeAreaAssessor.setShowNonprintables(codeAreaOptions.isShowNonprintables());
        }
        ((CodeCharactersCaseCapable) codeArea).setCodeCharactersCase(codeAreaOptions.getCodeCharactersCase());
        ((PositionCodeTypeCapable) codeArea).setPositionCodeType(codeAreaOptions.getPositionCodeType());
        ((ViewModeCapable) codeArea).setViewMode(codeAreaOptions.getViewMode());
        NonAsciiCodeAreaColorAssessor nonAsciiColorAssessor = CodeAreaSwingUtils.findColorAssessor((ColorAssessorPainterCapable) painter, NonAsciiCodeAreaColorAssessor.class);
        if (nonAsciiColorAssessor != null) {
            nonAsciiColorAssessor.setNonAsciiHighlightingEnabled(codeAreaOptions.isCodeColorization());
        }
        codeArea.setRowWrapping(codeAreaOptions.getRowWrappingMode());
        codeArea.setMaxBytesPerRow(codeAreaOptions.getMaxBytesPerRow());
        codeArea.setMinRowPositionLength(codeAreaOptions.getMinRowPositionLength());
        codeArea.setMaxRowPositionLength(codeAreaOptions.getMaxRowPositionLength());
    }
}
