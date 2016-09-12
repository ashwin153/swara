package com.swara.music.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.swara.music.data.Song;

public interface SongWriter {

    /**
     * Writes a {@link Song} to the specified {@link OutputStream}.
     */
    void write(OutputStream out, Song song) throws Exception;

    default void write(File file, Song song) throws Exception {
        try (OutputStream out = new FileOutputStream(file)) {
            write(out, song);
        }
    }
}
