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
 * Code export format.
 */
@ParametersAreNonnullByDefault
public interface CodeExportFormat {

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
     * Generates code string from binary data.
     *
     * @param data binary data to export
     * @param options export options
     * @return generated code string
     */
    @Nonnull
    String generateCode(BinaryData data, CodeExportOptions options);

    /**
     * Returns default export options for this format.
     *
     * @return default options
     */
    @Nonnull
    CodeExportOptions getDefaultOptions();
}
