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
package org.exbin.framework.bined.operation.method;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.SelectionCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.framework.App;
import org.exbin.framework.bined.operation.CodeSeparator;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.bined.operation.api.PreviewDataHandler;
import org.exbin.framework.bined.operation.api.CopyAsDataMethod;
import org.exbin.framework.bined.operation.gui.TextPreviewPanel;
import org.exbin.framework.bined.operation.method.gui.CopyAsTextDataPanel;

/**
 * Copy as text data method.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CopyAsTextDataMethod implements CopyAsDataMethod {

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(CopyAsTextDataPanel.class);

    private PreviewDataHandler previewDataHandler;
    private long previewLengthLimit = 1024;
    private TextPreviewPanel previewPanel;

    @Nonnull
    @Override
    public String getName() {
        return resourceBundle.getString("method.name");
    }

    @Nonnull
    @Override
    public Component createComponent() {
        CopyAsTextDataPanel component = new CopyAsTextDataPanel();

        List<String> codeTypes = new ArrayList<>();
        codeTypes.add(resourceBundle.getString("codeType.binary"));
        codeTypes.add(resourceBundle.getString("codeType.octal"));
        codeTypes.add(resourceBundle.getString("codeType.decimal"));
        codeTypes.add(resourceBundle.getString("codeType.hexadecimal"));
        component.setCodeTypes(codeTypes);

        List<String> charactersCases = new ArrayList<>();
        charactersCases.add(resourceBundle.getString("charactersCase.lower"));
        charactersCases.add(resourceBundle.getString("charactersCase.higher"));
        component.setCharactersCases(charactersCases);

        List<String> codeSeparators = new ArrayList<>();
        codeSeparators.add(resourceBundle.getString("codeSeparator.none"));
        codeSeparators.add(resourceBundle.getString("codeSeparator.space"));
        codeSeparators.add(resourceBundle.getString("codeSeparator.comma"));
        component.setCodeSeparators(codeSeparators);

        component.setCodeType(CodeType.HEXADECIMAL);
        return component;
    }

    @Override
    public void initFocus(Component component) {
        ((CopyAsTextDataPanel) component).initFocus();
    }

    @Override
    public void performCopy(Component component, CodeAreaCore codeArea) {
        CopyAsTextDataPanel panel = (CopyAsTextDataPanel) component;
        CodeType codeType = panel.getCodeType();
        CodeCharactersCase codeCharactersCase = panel.getCodeCharactersCase();
        CodeSeparator codeSeparator = panel.getCodeSeparator();
        int codesPerRow = panel.getCodesPerRow();

        long position;
        long length;
        SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
        if (selection.isEmpty()) {
            position = 0;
            length = codeArea.getDataSize();
        } else {
            position = selection.getFirst();
            length = selection.getLength();
        }
        if (length > previewLengthLimit) {
            length = previewLengthLimit;
        }
        String text = generateData(codeArea.getContentData(), position, (int) length, codeType, codeCharactersCase, codeSeparator, codesPerRow);
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }

    @Override
    public void requestPreview(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit) {
        this.previewDataHandler = previewDataHandler;
        this.previewLengthLimit = lengthLimit;
        CopyAsTextDataPanel panel = (CopyAsTextDataPanel) component;
        panel.setParamChangeListener(() -> {
            fillPreviewData(panel, codeArea);
        });
        fillPreviewData(panel, codeArea);
    }

    private void fillPreviewData(CopyAsTextDataPanel panel, CodeAreaCore codeArea) {
        previewPanel = new TextPreviewPanel();
        previewDataHandler.setPreviewComponent(previewPanel);

        SwingUtilities.invokeLater(() -> {
            CodeType codeType = panel.getCodeType();
            CodeCharactersCase codeCharactersCase = panel.getCodeCharactersCase();
            CodeSeparator codeSeparator = panel.getCodeSeparator();
            int codesPerRow = panel.getCodesPerRow();

            long position;
            long length;
            SelectionRange selection = ((SelectionCapable) codeArea).getSelection();
            if (selection.isEmpty()) {
                position = 0;
                length = codeArea.getDataSize();
            } else {
                position = selection.getFirst();
                length = selection.getLength();
            }
            if (length > previewLengthLimit) {
                length = previewLengthLimit;
            }
            String previewText = generateData(codeArea.getContentData(), position, (int) length, codeType, codeCharactersCase, codeSeparator, codesPerRow);
            previewPanel.setPreviewText(previewText);
        });
    }

    @Nonnull
    public String generateData(BinaryData sourceData, long position, int length, CodeType codeType, CodeCharactersCase codeCharactersCase, CodeSeparator codeSeparator, int codesPerRow) {
        int charsPerByte = codeType.getMaxDigitsForByte();
        int textLength = (int) (length * charsPerByte);
        if (codeSeparator != CodeSeparator.NONE) {
            textLength += length > 0 ? length : length - 1;
        }
        if (codesPerRow > 0) {
            textLength += length / codesPerRow;
        }

        char[] targetData = new char[textLength];
        Arrays.fill(targetData, ' ');
        int charPosition = 0;
        int rowPosition = 0;
        for (int i = 0; i < length; i++) {
            CodeAreaUtils.byteToCharsCode(sourceData.getByte(position + i), codeType, targetData, charPosition, codeCharactersCase);
            charPosition += charsPerByte;
            if (codeSeparator != CodeSeparator.NONE && (i < length - 1)) {
                targetData[charPosition] = codeSeparator == CodeSeparator.SPACE ? ' ' : ',';
                charPosition++;
            }
            rowPosition++;
            if (rowPosition == codesPerRow) {
                rowPosition = 0;
                targetData[charPosition] = '\n';
                charPosition++;
            }
        }
        return new String(targetData);
    }
}
