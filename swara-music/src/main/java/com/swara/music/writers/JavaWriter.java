package com.swara.music.writers;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.swara.music.MusicWriter;
import com.swara.music.elements.Song;

/**
 * Writes a {@link Song} to a stream encoded using the standard Java Serialization API. This writer
 * is included as a way of benchmarking the performance of alternative techniques.
 */
public final class JavaWriter implements MusicWriter {

    @Override
    public void write(OutputStream out, Song song) throws Exception {
        final ObjectOutputStream writer = new ObjectOutputStream(out);
        writer.writeObject(song);
    }

}
