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
package org.exbin.bined.jaguif.viewer.status;

import org.jspecify.annotations.NullMarked;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaStatusOptions;

/**
 * Numeric grouping for binary data status.
 */
@NullMarked
public class StatusNumericGrouping {

    protected int octalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_OCTAL_SPACE_GROUP_SIZE;
    protected int decimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_DECIMAL_SPACE_GROUP_SIZE;
    protected int hexadecimalSpaceGroupSize = CodeAreaStatusOptions.DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE;

    public StatusNumericGrouping() {
    }

    public StatusNumericGrouping(int octalSpaceGroupSize, int decimalSpaceGroupSize, int hexadecimalSpaceGroupSize) {
        this.octalSpaceGroupSize = octalSpaceGroupSize;
        this.decimalSpaceGroupSize = decimalSpaceGroupSize;
        this.hexadecimalSpaceGroupSize = hexadecimalSpaceGroupSize;
    }

    public int getOctalSpaceGroupSize() {
        return octalSpaceGroupSize;
    }

    public void setOctalSpaceGroupSize(int octalSpaceGroupSize) {
        this.octalSpaceGroupSize = octalSpaceGroupSize;
    }

    public int getDecimalSpaceGroupSize() {
        return decimalSpaceGroupSize;
    }

    public void setDecimalSpaceGroupSize(int decimalSpaceGroupSize) {
        this.decimalSpaceGroupSize = decimalSpaceGroupSize;
    }

    public int getHexadecimalSpaceGroupSize() {
        return hexadecimalSpaceGroupSize;
    }

    public void setHexadecimalSpaceGroupSize(int hexadecimalSpaceGroupSize) {
        this.hexadecimalSpaceGroupSize = hexadecimalSpaceGroupSize;
    }
    
    public void setFromOptions(CodeAreaStatusOptions options) {
        octalSpaceGroupSize = options.getOctalSpaceGroupSize();
        decimalSpaceGroupSize = options.getDecimalSpaceGroupSize();
        hexadecimalSpaceGroupSize = options.getHexadecimalSpaceGroupSize();
    }
}
