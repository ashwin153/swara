package com.swara.music.io;

import java.io.OutputStream;
import java.util.Map;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.swara.music.data.Chord;
import com.swara.music.data.Fragment;
import com.swara.music.data.Note;
import com.swara.music.data.Phrase;
import com.swara.music.data.Song;

/**
 * Writes {@link Song} to a MIDI {@link Sequence}. Writing conforms to the MIDI Format 0 spec;
 * therefore, the result is a single-track, 16-channel MIDI file. For more information about the
 * MIDI file spec check out: http://www.somascape.org/midi/tech/mfile.html.
 */
public class MidiSongWriter implements SongWriter {

    private static final int TICK_RESOLUTION = 480;

    @Override
    public void write(OutputStream out, Song song) throws Exception {
        // Create MIDI sequence using the PPQ (pulses-per-quarter-note) division type.
        final Sequence sequence = new Sequence(Sequence.PPQ, 480);
        final Track track = sequence.createTrack();

        for (Fragment fragment : song.fragments()) {
            // Create a track if it doesn't already exist.
            final long start = sequence.getTickLength();

            // Write the bpm to the sequence. (mspqn = milliseconds/quarter-note)
            final int mspqn = 60_000_000 / fragment.tempo().bpm();
            track.add(new MidiEvent(new MetaMessage(0x51, new byte[] {
                (byte) ((mspqn >> 16) & 0xFF),
                (byte) ((mspqn >>  8) & 0xFF),
                (byte) ((mspqn) & 0xFF)
            }, 3), start));

            // Write the time signature.
            track.add(new MidiEvent(new MetaMessage(0x58, new byte[]{
                (byte) (fragment.tempo().signature().getNumerator()),
                (byte) (Math.log(fragment.tempo().signature().getDenominator()) / Math.log(2)),
                (byte) 24,
                (byte) 8
            }, 4), start));

            // Write the key signature.
            track.add(new MidiEvent(new MetaMessage(0x59, new byte[] {
                (byte) fragment.key().signature(),
                (byte) fragment.key().type()
            }, 2), start));

            // Write each phrase on a separate channel on a separate track.
            for (Map.Entry<Integer, Phrase> entry : fragment.phrases().entrySet()) {
                final Phrase phrase = entry.getValue();
                final int channel = entry.getKey();
                long tick = start;

                // Write the program change event.
                track.add(new MidiEvent(new ShortMessage(
                    ShortMessage.PROGRAM_CHANGE, channel, phrase.program(), 0
                ), start));

                for (Chord chord : phrase.chords()) {
                    // Calculate the duration of the chord. (ppb = pulses per beat)
                    final int ppb = 4 * TICK_RESOLUTION;
                    final long next = tick + chord.duration().multiply(ppb).longValue();

                    for (Note note : chord.notes()) {
                        // Translate the note into a MIDI key.
                        final int key = note.octave() * 12 + note.pitch();

                        // Write the note on event.
                        track.add(new MidiEvent(new ShortMessage(
                            ShortMessage.NOTE_ON, channel, key, chord.volume()
                        ), tick));

                        // Write the note off event.
                        track.add(new MidiEvent(new ShortMessage(
                            ShortMessage.NOTE_ON, channel, key, 0
                        ), next));
                    }

                    // Update the tick to the beginning of the next chord.
                    tick = next;
                }
            }
        }

        // Write the sequence to file.
        MidiSystem.write(sequence, 0, out);
    }

}
