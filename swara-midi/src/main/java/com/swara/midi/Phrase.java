package com.swara.midi;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * A set of chords played sequentially for a particular instrument. Phrases are combined together to
 * form a {@link com.swara.midi.Fragment} and are built with {@link com.swara.midi.Phrase.Builder}.
 * Phrases are immutable, and, therefore, thread-safe.
 */
public class Phrase {

    private final int program;
    private final List<Chord> chords;

    private Phrase(Builder builder) {
        this.program = builder.program;
        this.chords = builder.chords;
    }

    /**
     * Returns the MIDI program. A MIDI controller maps program numbers to instruments using its
     * configured soundbank. For example, program numbers 0-8 are mapped to piano instruments. The
     * program number must be on the interval [0, 128).
     */
    public int program() {
        return this.program;
    }

    /**
     * Returns the a mutable copy of the list of chords that comprise this phrase. Chords are stored
     * sequentially in playback order.
     */
    public List<Chord> chords() {
        return this.chords;
    }

    /**
     * Constructs a {@link com.swara.midi.Chord} using a Fluent-style builder pattern. By default,
     * the builder will construct an empty piano (program 0) phrase.
     */
    public static final class Builder {

        private int program;
        private List<Chord> chords;

        public Builder() {
            this.program = 0;
            this.chords = new ArrayList<>();
        }

        public Builder withProgram(int program) {
            // The program must be on the interval [0, 128).
            Preconditions.checkArgument(program >= 0 && program < 128);
            this.program = program;
            return this;
        }

        public Builder withChord(Chord chord) {
            // The chord must not be null.
            Preconditions.checkNotNull(chord);
            this.chords.add(chord);
            return this;
        }

        public Phrase build() {
            return new Phrase(this);
        }
    }

}
