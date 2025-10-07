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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.binary_data.BinaryData;

/**
 * Code import format.
 */
@ParametersAreNonnullByDefault
public interface CodeImportFormat {

    /**
     * Returns the display name of this format (e.g., "Java byte[]").
     *
     * @return format display name
     */
    @Nonnull
    String getFormatName();

    /**
     * Returns the programming language name (e.g., "Java", "Python", "C").
     *
     * @return language name
     */
    @Nonnull
    String getLanguageName();

    /**
     * Parses code string and extracts binary data.
     *
     * @param code code string to parse
     * @return parsed binary data
     * @throws CodeParseException if code cannot be parsed
     */
    @Nonnull
    BinaryData parseCode(String code) throws CodeParseException;

    /**
     * Checks if this parser can handle the given code. Used for auto-detection.
     *
     * @param code code string to check
     * @return true if this parser can likely parse the code
     */
    boolean canParse(String code);

    /**
     * Exception thrown when code parsing fails.
     */
    public static class CodeParseException extends Exception {

        public CodeParseException(String message) {
            super(message);
        }

        public CodeParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
