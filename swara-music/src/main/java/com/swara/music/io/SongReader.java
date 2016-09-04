package com.swara.music.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.swara.music.model.Song;

public interface SongReader {

    /**
     *
     */
    Song read(InputStream in) throws Exception;

    default Song read(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            return read(in);
        }
    }

}
