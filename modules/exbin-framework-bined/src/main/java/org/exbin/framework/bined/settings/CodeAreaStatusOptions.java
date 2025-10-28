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
package org.exbin.framework.bined.settings;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.PositionCodeType;
import org.exbin.framework.bined.StatusCursorPositionFormat;
import org.exbin.framework.bined.StatusDocumentSizeFormat;
import org.exbin.framework.options.settings.api.SettingsOptions;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Code area status bar options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaStatusOptions implements SettingsOptions {

    public static int DEFAULT_OCTAL_SPACE_GROUP_SIZE = 4;
    public static int DEFAULT_DECIMAL_SPACE_GROUP_SIZE = 3;
    public static int DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE = 4;

    public static final String KEY_CURSOR_POSITION_CODE_TYPE = "statusCursorPositionFormat";
    public static final String KEY_CURSOR_POSITION_SHOW_OFFSET = "statusCursorShowOffset";
    public static final String KEY_DOCUMENT_SIZE_CODE_TYPE = "statusDocumentSizeFormat";
    public static final String KEY_DOCUMENT_SIZE_SHOW_RELATIVE = "statusDocumentShowRelative";
    public static final String KEY_OCTAL_SPACE_GROUP_SIZE = "statusOctalSpaceGroupSize";
    public static final String KEY_DECIMAL_SPACE_GROUP_SIZE = "statusDecimalSpaceGroupSize";
    public static final String KEY_HEXADECIMAL_SPACE_GROUP_SIZE = "statusHexadecimalSpaceGroupSize";

    private final OptionsStorage storage;

    public CodeAreaStatusOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public PositionCodeType getCursorPositionCodeType() {
        PositionCodeType defaultCodeType = PositionCodeType.DECIMAL;
        try {
            return PositionCodeType.valueOf(storage.get(KEY_CURSOR_POSITION_CODE_TYPE, defaultCodeType.name()));
        } catch (Exception ex) {
            Logger.getLogger(CodeAreaStatusOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setCursorPositionCodeType(PositionCodeType statusCursorPositionCodeType) {
        storage.put(KEY_CURSOR_POSITION_CODE_TYPE, statusCursorPositionCodeType.name());
    }

    public boolean isCursorShowOffset() {
        return storage.getBoolean(KEY_CURSOR_POSITION_SHOW_OFFSET, true);
    }

    public void setCursorShowOffset(boolean statusCursorShowOffset) {
        storage.putBoolean(KEY_CURSOR_POSITION_SHOW_OFFSET, statusCursorShowOffset);
    }

    @Nonnull
    public PositionCodeType getDocumentSizeCodeType() {
        PositionCodeType defaultCodeType = PositionCodeType.DECIMAL;
        try {
            return PositionCodeType.valueOf(storage.get(KEY_DOCUMENT_SIZE_CODE_TYPE, defaultCodeType.name()));
        } catch (Exception ex) {
            Logger.getLogger(CodeAreaStatusOptions.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setDocumentSizeCodeType(PositionCodeType statusDocumentSizeCodeType) {
        storage.put(KEY_DOCUMENT_SIZE_CODE_TYPE, statusDocumentSizeCodeType.name());
    }

    public boolean isDocumentSizeShowRelative() {
        return storage.getBoolean(KEY_DOCUMENT_SIZE_SHOW_RELATIVE, true);
    }

    public void setDocumentSizeShowRelative(boolean statusDocumentSizeShowRelative) {
        storage.putBoolean(KEY_DOCUMENT_SIZE_SHOW_RELATIVE, statusDocumentSizeShowRelative);
    }

    @Nonnull
    public StatusCursorPositionFormat getCursorPositionFormat() {
        return new StatusCursorPositionFormat(getCursorPositionCodeType(), isCursorShowOffset());
    }

    @Nonnull
    public StatusDocumentSizeFormat getDocumentSizeFormat() {
        return new StatusDocumentSizeFormat(getDocumentSizeCodeType(), isDocumentSizeShowRelative());
    }

    public void setCursorPositionFormat(StatusCursorPositionFormat cursorPositionFormat) {
        setCursorPositionCodeType(cursorPositionFormat.getCodeType());
        setCursorShowOffset(cursorPositionFormat.isShowOffset());
    }

    public void setDocumentSizeFormat(StatusDocumentSizeFormat documentSizeFormat) {
        setDocumentSizeCodeType(documentSizeFormat.getCodeType());
        setDocumentSizeShowRelative(documentSizeFormat.isShowRelative());
    }

    public int getOctalSpaceGroupSize() {
        return storage.getInt(KEY_OCTAL_SPACE_GROUP_SIZE, DEFAULT_OCTAL_SPACE_GROUP_SIZE);
    }

    public void setOctalSpaceGroupSize(int octalSpaceSize) {
        storage.putInt(KEY_OCTAL_SPACE_GROUP_SIZE, octalSpaceSize);
    }

    public int getDecimalSpaceGroupSize() {
        return storage.getInt(KEY_DECIMAL_SPACE_GROUP_SIZE, DEFAULT_DECIMAL_SPACE_GROUP_SIZE);
    }

    public void setDecimalSpaceGroupSize(int decimalSpaceSize) {
        storage.putInt(KEY_DECIMAL_SPACE_GROUP_SIZE, decimalSpaceSize);
    }

    public int getHexadecimalSpaceGroupSize() {
        return storage.getInt(KEY_HEXADECIMAL_SPACE_GROUP_SIZE, DEFAULT_HEXADECIMAL_SPACE_GROUP_SIZE);
    }

    public void setHexadecimalSpaceGroupSize(int hexadecimalSpaceSize) {
        storage.putInt(KEY_HEXADECIMAL_SPACE_GROUP_SIZE, hexadecimalSpaceSize);
    }

    @Override
    public void copyTo(SettingsOptions options) {
        CodeAreaStatusOptions with = (CodeAreaStatusOptions) options;
        with.setCursorPositionCodeType(getCursorPositionCodeType());
        with.setCursorShowOffset(isCursorShowOffset());
        with.setDecimalSpaceGroupSize(getDecimalSpaceGroupSize());
        with.setDocumentSizeCodeType(getDocumentSizeCodeType());
        with.setDocumentSizeShowRelative(isDocumentSizeShowRelative());
        with.setHexadecimalSpaceGroupSize(getHexadecimalSpaceGroupSize());
        with.setOctalSpaceGroupSize(getOctalSpaceGroupSize());
    }
}
