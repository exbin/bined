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
package org.exbin.framework.bined.operation.api;

import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Interface for copy as data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CopyAsDataMethod extends DataOperationMethod {

    /**
     * Performs copy of the selected data to clipboard.
     *
     * @param component visual component
     * @param codeArea code area
     */
    void performCopy(Component component, CodeAreaCore codeArea);

    /**
     * Sets editable data target for preview.
     *
     * @param previewDataHandler preview data handler
     * @param component visual component
     * @param codeArea source code area
     * @param lengthLimit limit to length of set data
     */
    void requestPreview(PreviewDataHandler previewDataHandler, Component component, CodeAreaCore codeArea, long lengthLimit);
}
