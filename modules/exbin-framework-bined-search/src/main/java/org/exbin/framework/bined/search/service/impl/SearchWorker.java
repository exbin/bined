package org.exbin.framework.bined.search.service.impl;

import javax.swing.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.IOException;
import java.util.concurrent.CancellationException;

/**
 * TODO: Integrate code
 * <p>
 * Background search worker. Publishes SearchResult objects incrementally and progress (0..100).
 */
public class SearchWorker extends SwingWorker<Void, SearchResult> {
    public enum Mode { TEXT, REGEX, HEX }

    private final EditorAccessor accessor;
    private final Mode mode;
    private final String query;
    private final Charset charset;
    private final boolean caseSensitive;
    private final int bufferSize = 1024 * 1024; // 1 MB read buffer

    private final List<SearchResult> results = new ArrayList<>();
    private final Pattern textPattern; // for TEXT/REGEX
    private final BytePattern hexPattern; // for HEX

    public SearchWorker(EditorAccessor accessor, Mode mode, String query, Charset charset, boolean caseSensitive) {
        this.accessor = accessor;
        this.mode = mode;
        this.query = query;
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.caseSensitive = caseSensitive;

        if (mode == Mode.REGEX) {
            int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
            textPattern = Pattern.compile(query, flags | Pattern.DOTALL);
            hexPattern = null;
        } else if (mode == Mode.TEXT) {
            String q = caseSensitive ? Pattern.quote(query) : "(?i)" + Pattern.quote(query);
            textPattern = Pattern.compile(q, Pattern.DOTALL);
            hexPattern = null;
        } else {
            textPattern = null;
            hexPattern = BytePattern.fromHexString(query);
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        long fileSize = accessor.getFileSize();
        long readOffset = 0;
        int overlap = 1024; // to handle matches crossing buffer boundary
        byte[] tail = new byte[0];

        while (readOffset < fileSize) {
            if (isCancelled()) throw new CancellationException();

            int toRead = (int) Math.min(bufferSize, fileSize - readOffset);
            byte[] chunk = accessor.readBytes(readOffset, toRead);

            // combine tail + chunk
            byte[] window = new byte[tail.length + chunk.length];
            System.arraycopy(tail, 0, window, 0, tail.length);
            System.arraycopy(chunk, 0, window, tail.length, chunk.length);

            if (mode == Mode.HEX) {
                // search byte pattern with wildcards
                List<Long> hits = hexPattern.searchAll(window);
                for (long hit : hits) {
                    long absoluteOffset = readOffset - tail.length + hit;
                    if (absoluteOffset >= 0 && absoluteOffset < fileSize) {
                        String preview = buildHexPreview(absoluteOffset, hexPattern.getLength());
                        SearchResult r = new SearchResult(absoluteOffset, hexPattern.getLength(), preview);
                        results.add(r);
                        publish(r);
                    }
                }
            } else {
                // TEXT or REGEX: convert window to string
                String s = new String(window, charset);
                Matcher m = textPattern.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    long absoluteOffset = readOffset - tail.length + start;
                    int length = end - start;
                    String preview = s.substring(Math.max(0, start - 20), Math.min(s.length(), end + 20))
                                     .replaceAll("\\p{C}", "."); // remove control chars
                    SearchResult r = new SearchResult(absoluteOffset, length, preview);
                    results.add(r);
                    publish(r);
                }
            }

            // progress
            int percent = (int) ((readOffset + toRead) * 100 / Math.max(1, fileSize));
            setProgress(Math.min(100, percent));

            // prepare tail: keep last 'overlap' bytes from window
            int keep = Math.min(overlap, window.length);
            tail = new byte[keep];
            System.arraycopy(window, window.length - keep, tail, 0, keep);

            readOffset += toRead;
        }

        setProgress(100);
        return null;
    }

    private String buildHexPreview(long offset, int length) {
        try {
            int previewLen = Math.min(32, length);
            byte[] data = accessor.readBytes(offset, previewLen);
            StringBuilder sb = new StringBuilder();
            for (byte b : data) sb.append(String.format("%02X ", b));
            return sb.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    protected void process(List<SearchResult> chunks) {
        // published results will be forwarded to listeners via get() after completion,
        // but we also publish so UI can add as they arrive (see SearchPanel).
    }

    public List<SearchResult> getResults() {
        return results;
    }
}
