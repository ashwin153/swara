package com.swara.music;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.swara.music.elements.Song;

/**
 * A music writer. A music writer is responsible for writing a {@link Song} from to given
 * {@link OutputStream}, and it, along with the {@link MusicReader}, allow musical information to
 * be serialized and deserialized to and from a variety of sources. The reader and writer interfaces
 * are separated on purpose, because they ought to be independent operations.
 */
@FunctionalInterface
public interface MusicWriter {

    void write(OutputStream out, Song song) throws Exception;

    default void write(File file, Song song) throws Exception {
        try (OutputStream out = new FileOutputStream(file)) {
            write(out, song);
        }
    }
}
