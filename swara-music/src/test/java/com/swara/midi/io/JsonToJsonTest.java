package com.swara.midi.io;

import java.io.File;

import com.swara.music.MusicReader;
import com.swara.music.MusicWriter;
import com.swara.music.elements.Song;
import com.swara.music.readers.JsonReader;
import com.swara.music.writers.JsonWriter;

import org.junit.Test;

public class JsonToJsonTest {

    @Test
    public void testReadWrite() throws Exception {
        final MusicReader reader = new JsonReader();
        final MusicWriter writer = new JsonWriter();

        // Read the test file and write it to the result file.
        final Song song = reader.read(new File("./src/test/resources/test.json"));
        writer.write(new File("./src/test/resources/test-result.json"), song);
    }

}
