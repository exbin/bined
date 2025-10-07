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
package org.exbin.framework.bined.operation.code;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Code export options.
 */
@ParametersAreNonnullByDefault
public class CodeExportOptions {

    private boolean uppercaseHex = true;
    private int bytesPerLine = 16;
    private boolean includeLineBreaks = true;
    private String indentation = "    ";
    private boolean includeVariableDeclaration = true;
    private String variableName = "data";

    public CodeExportOptions() {
    }

    public boolean isUppercaseHex() {
        return uppercaseHex;
    }

    public void setUppercaseHex(boolean uppercaseHex) {
        this.uppercaseHex = uppercaseHex;
    }

    public int getBytesPerLine() {
        return bytesPerLine;
    }

    public void setBytesPerLine(int bytesPerLine) {
        this.bytesPerLine = bytesPerLine;
    }

    public boolean isIncludeLineBreaks() {
        return includeLineBreaks;
    }

    public void setIncludeLineBreaks(boolean includeLineBreaks) {
        this.includeLineBreaks = includeLineBreaks;
    }

    public String getIndentation() {
        return indentation;
    }

    public void setIndentation(String indentation) {
        this.indentation = indentation;
    }

    public boolean isIncludeVariableDeclaration() {
        return includeVariableDeclaration;
    }

    public void setIncludeVariableDeclaration(boolean includeVariableDeclaration) {
        this.includeVariableDeclaration = includeVariableDeclaration;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
}
