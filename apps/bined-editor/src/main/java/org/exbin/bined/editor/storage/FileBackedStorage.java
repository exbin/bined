package org.exbin.bined.swap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * FEATURE 1: File-backed storage
 *
 * Minimal wrapper providing low-level file access that higher-level
 * components can use. Keep this small so we can add it as a standalone PR.
 */
public class FileBackedStorage implements AutoCloseable {
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final File file;

    public FileBackedStorage(File file) throws IOException {
        this.file = file;
        this.raf = new RandomAccessFile(file, "rw");
        this.channel = raf.getChannel();
    }

    /** Read into the provided ByteBuffer starting from the given file position. */
    public int read(java.nio.ByteBuffer dst, long position) throws IOException {
        return channel.read(dst, position);
    }

    /** Write from the provided ByteBuffer starting from the given file position. */
    public int write(java.nio.ByteBuffer src, long position) throws IOException {
        return channel.write(src, position);
    }

    /** Get current file length. */
    public long length() throws IOException {
        return channel.size();
    }

    /** Truncate file to given size. */
    public void truncate(long size) throws IOException {
        channel.truncate(size);
    }

    /** Force writing to device (optional for this PR). */
    public void force(boolean metaData) throws IOException {
        channel.force(metaData);
    }

    public File getFile() {
        return file;
    }

    @Override
    public void close() throws IOException {
        channel.close();
        raf.close();
    }
}