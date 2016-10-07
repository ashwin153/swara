package com.swara.music.readers;

import java.io.InputStream;
import java.io.ObjectInputStream;

import com.swara.music.MusicReader;
import com.swara.music.elements.Song;

/**
 * Reads a {@link Song} from a stream encoded using the standard Java Serialization API. This reader
 * is included as a way of benchmarking the performance of alternative techniques.
 */
public final class JavaReader implements MusicReader {

    @Override
    @SuppressWarnings({ "unchecked" })
    public Song read(InputStream input) throws Exception {
        final ObjectInputStream reader = new ObjectInputStream(input);
        return (Song) reader.readObject();
    }

}
