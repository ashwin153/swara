package com.swara.midi.io;

import java.io.File;

import com.swara.music.data.Song;
import com.swara.music.io.MidiSongReader;
import com.swara.music.io.MidiSongWriter;
import com.swara.music.io.SongReader;
import com.swara.music.io.SongWriter;

import org.junit.Test;

public class MidiReadWriteTest {

    @Test
    public void testReadWrite() throws Exception {
        final SongReader reader = new MidiSongReader();
        final SongWriter writer = new MidiSongWriter();

        // Read the test file and write it to the result file.
        final Song song = reader.read(new File("./src/test/resources/test.mid"));
        writer.write(new File("./src/test/resources/test-result.mid"), song);
    }

}
