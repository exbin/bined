package org.exbin.framework.bined.search.service.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Integrate code
 * <p>
 * Represents a byte pattern with optional wildcard bytes.
 * Wildcard byte is represented with null in the pattern array.
 */
public class BytePattern {
    private final Byte[] pattern; // null -> wildcard

    private BytePattern(Byte[] pattern) {
        this.pattern = pattern;
    }

    public static BytePattern fromHexString(String hex) {
        // normalize: remove spaces
        String s = hex.replaceAll("\\s+", "");
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i + 1 < s.length(); i += 2) {
            String pair = s.substring(i, i + 2);
            if (pair.equals("??") || pair.equals("..") || pair.contains("?")) {
                list.add(null);
            } else {
                int v = Integer.parseInt(pair, 16);
                list.add((byte) v);
            }
        }
        Byte[] arr = new Byte[list.size()];
        arr = list.toArray(arr);
        return new BytePattern(arr);
    }

    public int getLength() { return pattern.length; }

    /**
     * Search all matches inside haystack (byte array) and return list of start indexes.
     */
    public List<Long> searchAll(byte[] haystack) {
        List<Long> hits = new ArrayList<>();
        if (pattern.length == 0 || haystack.length < pattern.length) return hits;
        outer:
        for (int i = 0; i <= haystack.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                Byte p = pattern[j];
                if (p != null && p != haystack[i + j]) {
                    continue outer;
                }
            }
            hits.add((long) i);
        }
        return hits;
    }
}
