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
package org.exbin.framework.bined.operation.component.format;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.framework.bined.operation.component.api.CodeExportFormat;
import org.exbin.framework.bined.operation.component.api.CodeExportOptions;


@ParametersAreNonnullByDefault
public class JavaByteArrayFormat implements CodeExportFormat {

    @Nonnull
    @Override
    public String getFormatName() {
        return "Java byte[]";
    }

    @Nonnull
    @Override
    public String getLanguageName() {
        return "Java";
    }

    @Nonnull
    @Override
    public String generateCode(BinaryData data, CodeExportOptions options) {
        StringBuilder code = new StringBuilder();
        long dataSize = data.getDataSize();

        // Variable declaration
        if (options.isIncludeVariableDeclaration()) {
            code.append("byte[] ").append(options.getVariableName()).append(" = {");
        } else {
            code.append("{");
        }

        // Generate hex bytes
        int bytesPerLine = options.getBytesPerLine();
        boolean includeLineBreaks = options.isIncludeLineBreaks();
        String hexFormat = options.isUppercaseHex() ? "0x%02X" : "0x%02x";

        for (long i = 0; i < dataSize; i++) {
            // Line break and indentation
            if (includeLineBreaks && i % bytesPerLine == 0) {
                code.append("\n").append(options.getIndentation());
            }

            // Byte value
            byte value = data.getByte(i);
            code.append(String.format(hexFormat, value));

            // Comma separator
            if (i < dataSize - 1) {
                code.append(", ");
            }
        }

        // Closing
        if (includeLineBreaks && dataSize > 0) {
            code.append("\n");
        }
        code.append("};");

        return code.toString();
    }

    @Nonnull
    @Override
    public CodeExportOptions getDefaultOptions() {
        CodeExportOptions options = new CodeExportOptions();
        options.setUppercaseHex(true);
        options.setBytesPerLine(16);
        options.setIncludeLineBreaks(true);
        options.setIndentation("    ");
        options.setIncludeVariableDeclaration(true);
        options.setVariableName("data");
        return options;
    }
}
