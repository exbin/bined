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
package org.exbin.framework.bined.inspector;

import java.awt.Color;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaSection;
import org.exbin.bined.swing.CodeAreaColorAssessor;
import org.exbin.bined.swing.CodeAreaPaintState;
import org.exbin.framework.utils.UiUtils;

/**
 * Basic values inspector position color modifier.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BasicValuesPositionColorModifier implements CodeAreaColorAssessor {

    protected CodeAreaColorAssessor parentAssessor;
    protected long position = -1;
    protected long length;
    protected Color color;

    public BasicValuesPositionColorModifier() {
        this(null);
    }

    public BasicValuesPositionColorModifier(@Nullable CodeAreaColorAssessor parentAssessor) {
        this.parentAssessor = parentAssessor;
    }

    @Nullable
    @Override
    public Color getPositionBackgroundColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean inSelection) {
        if (position >= 0) {
            long dataPosition = rowDataPosition + byteOnRow;
            if (dataPosition >= position && dataPosition < position + length) {
                return color;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Color getPositionTextColor(long rowDataPosition, int byteOnRow, int charOnRow, CodeAreaSection section, boolean inSelection) {
        return null;
    }

    @Nonnull
    @Override
    public Optional<CodeAreaColorAssessor> getParentColorAssessor() {
        return Optional.ofNullable(parentAssessor);
    }

    @Override
    public void startPaint(CodeAreaPaintState codeAreaPaintState) {
        color = UiUtils.isDarkUI() ? Color.YELLOW.darker().darker() : Color.YELLOW;
    }

    public void setRange(long position, long length) {
        this.position = position;
        this.length = length;
    }

    public void clearRange() {
        this.position = -1;
        this.length = 0;
    }

    /* TODO
    public void setColor(Color color) {
        this.color = color;
    }
     */
}
