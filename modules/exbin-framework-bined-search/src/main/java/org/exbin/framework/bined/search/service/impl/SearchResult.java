package org.exbin.framework.bined.search.service.impl;

/**
 * TODO: Integrate code
 */
public class SearchResult {
    private final long offset;
    private final int length;
    private final String preview;

    public SearchResult(long offset, int length, String preview) {
        this.offset = offset;
        this.length = length;
        this.preview = preview;
    }

    public long getOffset() { return offset; }
    public int getLength() { return length; }
    public String getPreview() { return preview; }

    @Override
    public String toString() {
        return String.format("0x%X (%d) : %s", offset, offset, preview);
    }
}
