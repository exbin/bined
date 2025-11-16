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
package org.exbin.framework.bined;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.context.api.StateChangeType;

/**
 * Code type state.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CodeTypeState {

    /**
     * Returns current code type.
     *
     * @return code type
     */
    @Nonnull
    CodeType getCodeType();

    /**
     * Sets current code type.
     *
     * @param codeType code type
     */
    void setCodeType(CodeType codeType);

    /**
     * Returns position code type.
     *
     * @return position code type
     */
    @Nonnull
    PositionCodeType getPositionCodeType();

    /**
     * Sets position code type.
     *
     * @param positionCodeType position code type
     */
    void setPositionCodeType(PositionCodeType positionCodeType);

    /**
     * Returns current code characters case.
     *
     * @return code characters case
     */
    @Nonnull
    CodeCharactersCase getCodeCharactersCase();

    /**
     * Sets current code characters case.
     *
     * @param codeCharactersCase code characters case
     */
    void setCodeCharactersCase(CodeCharactersCase codeCharactersCase);

    public enum ChangeType implements StateChangeType {
        CODE_TYPE,
        POSITION_CODE_TYPE,
        HEX_CHARACTERS_CASE
    }
}
