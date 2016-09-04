package com.swara.midi;

import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.math.Fraction;

/**
 * A set of phrases played simultaneously in a particular {@link com.swara.midi.Key} and
 * {@link com.swara.midi.Tempo}. Fragments are combined together to form a song. Fragments are built
 * using a {@link com.swara.midi.Fragment.Builder} and are immutable and, therefore, thread-safe.
 */
public class Fragment {

    private final Key key;
    private final Tempo tempo;
    private final Map<Integer, Phrase> phrases;

    private Fragment(Builder builder) {
        this.key = builder.key;
        this.tempo = builder.tempo;
        this.phrases = builder.phrases;
    }

    /**
     * Returns the key in which all the phrases in the fragment are written.
     */
    public Key key() {
        return this.key;
    }

    /**
     * Returns the tempo in which all the phrases in the fragment are written.
     */
    public Tempo tempo() {
        return this.tempo;
    }

    /**
     * Returns the mapping of phrases to channels. A MIDI sequence is composed of 16 channels over
     * which information may be passed. Each phrase in the fragment is assigned to a unique channel.
     * Channel 10 is reserved for percussion.
     */
    public Map<Integer, Phrase> phrases() {
        return this.phrases;
    }

    /**
     * Writes the song fragment to the MIDI sequence. Writing conforms with the Midi Format 0 spec;
     * all channels are written to the same track alongside key/tempo information. Currently
     * requires that the sequence use the PPQ division type. This link is God's Gift to Midi Files:
     * http://www.somascape.org/midi/tech/mfile.html.
     */
    public void write(Sequence seq) throws InvalidMidiDataException {
        Preconditions.checkArgument(seq.getDivisionType() == Sequence.PPQ);

        // Create a track if it doesn't already exist.
        final long start = seq.getTickLength();
        final Track track = (seq.getTracks().length == 0) ? seq.createTrack() : seq.getTracks()[0];

        // Write the tempo in microseconds-per-quarter-note.
        final int mspqn = 60_000_000 / this.tempo.bpm();
        track.add(new MidiEvent(new MetaMessage(0x51, new byte[]{
            (byte) ((mspqn >> 16) & 0xFF),
            (byte) ((mspqn >> 8) & 0xFF),
            (byte) (mspqn & 0xFF),
        }, 3), start));

        // Write the key signature.
        track.add(new MidiEvent(new MetaMessage(0x59, new byte[] {
            (byte) this.key.signature(),
            (byte) this.key.type()
        }, 2), start));

        // Write the time signature.
        track.add(new MidiEvent(new MetaMessage(0x58, new byte[]{
            (byte) (this.tempo.signature().getNumerator()),
            (byte) (Math.log(this.tempo.signature().getDenominator()) / Math.log(2)),
            (byte) 24,
            (byte) 8
        }, 4), start));

        // Write each phrase on a separate channel on a separate track.
        final Fraction ticksPerWholeNote = Fraction.getFraction(4 * seq.getResolution());
        for (Map.Entry<Integer, Phrase> entry : this.phrases.entrySet()) {
            final int channel = entry.getKey();
            final Phrase phrase = entry.getValue();
            long tick = start;

            // Write the program change event.
            track.add(new MidiEvent(new ShortMessage(
                ShortMessage.PROGRAM_CHANGE, channel, phrase.program(), 0
            ), tick));

            for (Chord chord : phrase.chords()) {
                final long next = tick + chord.duration().multiplyBy(ticksPerWholeNote).longValue();
                for (Note note : chord.notes()) {
                    // Write the note on event.
                    track.add(new MidiEvent(new ShortMessage(
                        ShortMessage.NOTE_ON, channel, note.pitch(), note.volume()
                    ), tick));

                    // Write the note off event.
                    track.add(new MidiEvent(new ShortMessage(
                        ShortMessage.NOTE_ON, channel, note.pitch(), 0
                    ), next));
                }
                tick = next;
            }
        }
    }

    /**
     * Constructs a {@link com.swara.midi.Fragment} using a Fluent-style builder pattern. By
     * default, the builder will construct an empty fragment with the default key and tempo.
     */
    public static final class Builder {

        private Key key;
        private Tempo tempo;
        private Map<Integer, Phrase> phrases;

        public Builder() {
            this.key = new Key.Builder().build();
            this.tempo = new Tempo.Builder().build();
            this.phrases = new HashMap<>();
        }

        public Builder withKey(Key key) {
            //
            Preconditions.checkNotNull(key);
            this.key = key;
            return this;
        }

        public Builder withTempo(Tempo tempo) {
            //
            Preconditions.checkNotNull(tempo);
            this.tempo = tempo;
            return this;
        }

        public Builder withPhrase(int channel, Phrase phrase) {
            //
            Preconditions.checkArgument(channel >= 0 && channel < 16);
            Preconditions.checkNotNull(phrase);
            this.phrases.put(channel, phrase);
            return this;
        }

        public Fragment build() {
            return new Fragment(this);
        }
    }

}
