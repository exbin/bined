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
package org.exbin.framework.bined.inspector.table.value;

import java.nio.ByteOrder;
import javax.annotation.Nonnull;
import org.exbin.framework.bined.inspector.table.api.ValueRowItem;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;

/**
 * Integer value type.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class IntegerValueRowType implements ValueRowType {

    private boolean signed = false;
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    @Nonnull
    @Override
    public ValueRowItem createRowItem() {
        return new ValueRowItem("Integer", Integer.class.getTypeName(), null) {
            @Override
            public void updateRow(byte[] values, int available) {
                if (available < 4) {
                    setValue(null);
                    return;
                }

                long intValue = signed
                        ? (byteOrder == ByteOrder.LITTLE_ENDIAN
                                ? (values[0] & 0xffL) | ((values[1] & 0xffL) << 8) | ((values[2] & 0xffL) << 16) | (values[3] << 24)
                                : (values[3] & 0xffL) | ((values[2] & 0xffL) << 8) | ((values[1] & 0xffL) << 16) | (values[0] << 24))
                        : (byteOrder == ByteOrder.LITTLE_ENDIAN
                                ? (values[0] & 0xffL) | ((values[1] & 0xffL) << 8) | ((values[2] & 0xffL) << 16) | ((values[3] & 0xffL) << 24)
                                : (values[3] & 0xffL) | ((values[2] & 0xffL) << 8) | ((values[1] & 0xffL) << 16) | ((values[0] & 0xffL) << 24));

                setValue(String.valueOf(intValue));
            }
        };
    }
}
