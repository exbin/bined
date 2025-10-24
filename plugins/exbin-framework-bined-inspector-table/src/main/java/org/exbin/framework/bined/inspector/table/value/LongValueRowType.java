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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nonnull;
import org.exbin.framework.bined.inspector.table.api.ValueRowItem;
import org.exbin.framework.bined.inspector.table.api.ValueRowType;

/**
 * Long value type.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class LongValueRowType implements ValueRowType {

    private boolean signed = false;
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    @Nonnull
    @Override
    public String getId() {
        return "long";
    }

    @Nonnull
    @Override
    public String getName() {
        return "Long";
    }

    @Nonnull
    @Override
    public ValueRowItem createRowItem() {
        return new ValueRowItem(getId(), getName(), Long.class.getTypeName(), null) {
            @Override
            public void updateRow(byte[] values, int available) {
                if (available < 8) {
                    setValue(null);
                    return;
                }

                if (signed) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(values);
                    if (byteBuffer.order() != byteOrder) {
                        byteBuffer.order(byteOrder);
                    }

                    setValue(String.valueOf(byteBuffer.getLong()));
                } else {
                    long longValue = byteOrder == ByteOrder.LITTLE_ENDIAN
                            ? (values[0] & 0xffL) | ((values[1] & 0xffL) << 8) | ((values[2] & 0xffL) << 16) | ((values[3] & 0xffL) << 24)
                            | ((values[4] & 0xffL) << 32) | ((values[5] & 0xffL) << 40) | ((values[6] & 0xffL) << 48)
                            : (values[7] & 0xffL) | ((values[6] & 0xffL) << 8) | ((values[5] & 0xffL) << 16) | ((values[4] & 0xffL) << 24)
                            | ((values[3] & 0xffL) << 32) | ((values[2] & 0xffL) << 40) | ((values[1] & 0xffL) << 48);
                    BigInteger bigInt1 = BigInteger.valueOf(values[byteOrder == ByteOrder.LITTLE_ENDIAN ? 7 : 0] & 0xffL);
                    BigInteger bigInt2 = bigInt1.shiftLeft(56);
                    BigInteger bigInt3 = bigInt2.add(BigInteger.valueOf(longValue));
                    setValue(bigInt3.toString());
                }
            }
        };
    }
}
