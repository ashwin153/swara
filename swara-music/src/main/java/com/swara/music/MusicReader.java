package com.swara.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.swara.music.elements.Song;

/**
 * A music reader. A music reader is responsible for parsing a {@link Song} from a given
 * {@link InputStream}, and it, along with the {@link MusicWriter}, allow musical information to
 * be serialized and deserialized to and from a variety of sources. The reader and writer interfaces
 * are separated on purpose, because they ought to be independent operations.
 */
@FunctionalInterface
public interface MusicReader {

    Song read(InputStream in) throws Exception;

    default Song read(File file) throws Exception {
        try (final InputStream in = new FileInputStream(file)) {
            return read(in);
        }
    }

}
