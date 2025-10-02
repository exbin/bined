package org.exbin.bined.editor.search;

public class BinEdEditorAccessor implements EditorAccessor {
    private final YourBinEdEditorClass editor;

    public BinEdEditorAccessor(YourBinEdEditorClass editor) { this.editor = editor; }

    @Override
    public byte[] readBytes(long offset, int length) throws IOException {
        return editor.readBytesFromFile(offset, length);
    }

    @Override
    public long getFileSize() {
        return editor.getOpenFileSize();
    }

    @Override
    public void highlightRange(long offset, int length) {
        editor.highlightAndFocus(offset, length);
    }

    @Override
    public void goToOffset(long offset) {
        editor.goToOffset(offset);
    }
}
