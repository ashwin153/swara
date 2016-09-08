package com.swara.music.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.swara.music.struct.Song;

public interface SongReader {

    /**]
     * Reads a {@link Song} from an {@link InputStream}.
     */
    Song read(InputStream in) throws Exception;

    default Song read(File file) throws Exception {
        try (final InputStream in = new FileInputStream(file)) {
            return read(in);
        }
    }

}
