package com.swara.midi.io;

import java.io.File;

import com.swara.music.data.Song;
import com.swara.music.io.JsonSongReader;
import com.swara.music.io.JsonSongWriter;
import com.swara.music.io.SongReader;
import com.swara.music.io.SongWriter;

import org.junit.Test;

public class JsonReadWriteTest {

    @Test
    public void testReadWrite() throws Exception {
        final SongReader reader = new JsonSongReader();
        final SongWriter writer = new JsonSongWriter();

        // Read the test file and write it to the result file.
        final Song song = reader.read(new File("./src/test/resources/test.json"));
        writer.write(new File("./src/test/resources/test-result.json"), song);
    }

}
