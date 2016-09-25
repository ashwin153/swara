package com.swara.music.writers;

import java.io.OutputStream;

import com.swara.music.MusicWriter;
import com.swara.music.elements.Song;

/**
 *
 */
public class StringWriter implements MusicWriter {

    @Override
    public void write(OutputStream out, Song song) throws Exception {
        out.write(song.toString().getBytes());
    }

}
