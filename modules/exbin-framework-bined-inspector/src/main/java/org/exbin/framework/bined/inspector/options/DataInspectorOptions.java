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
package org.exbin.framework.bined.inspector.options;

import java.awt.font.TextAttribute;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Data inspector options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface DataInspectorOptions {

    boolean isShowParsingPanel();

    void setShowParsingPanel(boolean showParsingPanel);

    boolean isUseDefaultFont();

    void setUseDefaultFont(boolean useDefaultFont);

    @Nullable
    Map<TextAttribute, ?> getFontAttributes();

    void setFontAttributes(@Nullable Map<TextAttribute, ?> fontAttributes);
}
