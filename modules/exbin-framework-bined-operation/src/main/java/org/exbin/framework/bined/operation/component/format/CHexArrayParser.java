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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.array.ByteArrayEditableData;
import org.exbin.framework.bined.operation.component.api.CodeImportFormat;


@ParametersAreNonnullByDefault
public class CHexArrayParser implements CodeImportFormat {

    // Pattern to match hex values like 0x1A, 0x2b, etc.
    private static final Pattern HEX_PATTERN = Pattern.compile("0x([0-9a-fA-F]{1,2})");
    // Pattern to detect C array syntax
    private static final Pattern C_PATTERN = Pattern.compile("(unsigned\\s+char|char)\\s*\\w*\\s*\\[|\\{\\s*0x");

    @Nonnull
    @Override
    public String getFormatName() {
        return "C unsigned char[]";
    }

    @Nonnull
    @Override
    public String getLanguageName() {
        return "C/C++";
    }

    @Nonnull
    @Override
    public BinaryData parseCode(String code) throws CodeParseException {
        if (code == null || code.trim().isEmpty()) {
            throw new CodeParseException("Code is empty");
        }

        List<Byte> bytes = new ArrayList<>();
        Matcher matcher = HEX_PATTERN.matcher(code);

        while (matcher.find()) {
            String hexValue = matcher.group(1);
            try {
                int value = Integer.parseInt(hexValue, 16);
                bytes.add((byte) value);
            } catch (NumberFormatException e) {
                throw new CodeParseException("Invalid hex value: 0x" + hexValue, e);
            }
        }

        if (bytes.isEmpty()) {
            throw new CodeParseException("No hex values found in code");
        }

        // Convert List<Byte> to byte[]
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }

        ByteArrayEditableData data = new ByteArrayEditableData();
        data.insert(0, byteArray);
        return data;
    }

    @Override
    public boolean canParse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        // Check if it looks like C array syntax
        Matcher cMatcher = C_PATTERN.matcher(code);
        if (cMatcher.find()) {
            // Also check if there are hex values
            Matcher hexMatcher = HEX_PATTERN.matcher(code);
            return hexMatcher.find();
        }

        return false;
    }
}
