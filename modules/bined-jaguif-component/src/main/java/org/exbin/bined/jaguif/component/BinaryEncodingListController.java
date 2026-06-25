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
package org.exbin.bined.jaguif.component;

import org.jspecify.annotations.NullMarked;
import org.exbin.jaguif.text.encoding.CharsetListEncodingState;

/**
 * Binary charset encoding list controller interface.
 */
@NullMarked
public interface BinaryEncodingListController extends CharsetListEncodingState {

    /**
     * Switches to next encoding in defined list.
     */
    void cycleNextEncoding();

    /**
     * Switches to previous encoding in defined list.
     */
    void cyclePreviousEncoding();
}
