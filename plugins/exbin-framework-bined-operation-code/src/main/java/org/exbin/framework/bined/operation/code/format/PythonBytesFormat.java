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
package org.exbin.framework.bined.operation.code.format;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.framework.bined.operation.code.CodeExportFormat;
import org.exbin.framework.bined.operation.code.CodeExportOptions;

/**
 * Code format for Python array.
 */
@ParametersAreNonnullByDefault
public class PythonBytesFormat implements CodeExportFormat {

    @Nonnull
    @Override
    public String getFormatName() {
        return "Python bytes";
    }

    @Nonnull
    @Override
    public String getLanguageName() {
        return "Python";
    }

    @Nonnull
    @Override
    public String generateCode(BinaryData data, CodeExportOptions options) {
        StringBuilder code = new StringBuilder();
        long dataSize = data.getDataSize();

        // Variable declaration
        if (options.isIncludeVariableDeclaration()) {
            code.append(options.getVariableName()).append(" = b'");
        } else {
            code.append("b'");
        }

        // Generate hex escape sequences
        int bytesPerLine = options.getBytesPerLine();
        boolean includeLineBreaks = options.isIncludeLineBreaks();
        String hexFormat = options.isUppercaseHex() ? "\\x%02X" : "\\x%02x";

        for (long i = 0; i < dataSize; i++) {
            // Line break and continuation
            if (includeLineBreaks && i > 0 && i % bytesPerLine == 0) {
                code.append("' \\\n").append(options.getIndentation()).append("b'");
            }

            // Byte value
            byte value = data.getByte(i);
            code.append(String.format(hexFormat, value));
        }

        // Closing
        code.append("'");

        return code.toString();
    }

    @Nonnull
    @Override
    public CodeExportOptions getDefaultOptions() {
        CodeExportOptions options = new CodeExportOptions();
        options.setUppercaseHex(false);
        options.setBytesPerLine(16);
        options.setIncludeLineBreaks(true);
        options.setIndentation("    ");
        options.setIncludeVariableDeclaration(true);
        options.setVariableName("data");
        return options;
    }
}
