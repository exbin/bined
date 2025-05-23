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
package org.exbin.framework.bined.editor.service;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.bined.basic.TabKeyHandlingMode;
import org.exbin.framework.bined.FileHandlingMode;

/**
 * Editor options service interface.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface EditorOptionsService {

    void setFileHandlingMode(FileHandlingMode fileHandlingMode);

    void setEnterKeyHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode);

    void setTabKeyHandlingMode(TabKeyHandlingMode tabKeyHandlingMode);
}
