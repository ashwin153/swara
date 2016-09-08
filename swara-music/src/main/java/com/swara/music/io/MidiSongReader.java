package com.swara.music.io;

import java.io.InputStream;

import com.swara.music.struct.Song;

/**
 * Reads a {@link Song} from a MIDI {@link javax.sound.midi.Sequence}.
 */
public class MidiSongReader implements SongReader {

    @Override
    public Song read(InputStream in) throws Exception {
        throw new UnsupportedOperationException();
    }

}
