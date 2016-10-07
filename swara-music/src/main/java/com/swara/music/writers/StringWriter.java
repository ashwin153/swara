package com.swara.music.writers;

import java.io.OutputStream;

import com.swara.music.MusicWriter;
import com.swara.music.elements.Song;

/**
 * Writes a {@link Song} to a standard Java String. This writer is included primarily for debugging
 * and logging purposes as well as the gold-standard for performance; it doesn't get faster than
 * dumping an object to string!
 */
public final class StringWriter implements MusicWriter {

    @Override
    public void write(OutputStream out, Song song) throws Exception {
        out.write(song.toString().getBytes());
    }

}
