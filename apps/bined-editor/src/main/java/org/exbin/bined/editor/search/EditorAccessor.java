package org.exbin.bined.editor.search;

import java.io.IOException;

public interface EditorAccessor {
    byte[] readBytes(long offset, int length) throws IOException;

    /**
     * Returns currently opened file size (number of bytes).
     */
    long getFileSize();

    /**
     * Ask the editor to highlight the given range (offset, length) and
     * bring the caret/viewport to offset.
     */
    void highlightRange(long offset, int length);

    /**
     * Navigate the editor viewport/caret to offset (no highlight).
     */
    void goToOffset(long offset);
}
