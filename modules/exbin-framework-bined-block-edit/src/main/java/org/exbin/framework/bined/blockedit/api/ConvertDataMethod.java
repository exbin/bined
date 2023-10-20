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
package org.exbin.framework.bined.blockedit.api;

import java.awt.Component;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.EditOperation;

/**
 * Interface for convert data component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ConvertDataMethod {

    @Nonnull
    String getName();

    @Nonnull
    Component getComponent();

    /**
     * Performs insertion of data.
     *
     * @param binaryData target binary data
     * @param position target position
     * @param length length of selected area for conversion
     * @param editOperation insertion operation mode
     */
    void performConvert(EditableBinaryData binaryData, long position, long length, EditOperation editOperation);
}
