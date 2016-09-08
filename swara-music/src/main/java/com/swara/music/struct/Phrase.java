package com.swara.music.struct;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

/**
 * A set of chords played sequentially for a particular instrument. Phrases are combined together to
 * form a {@link Fragment} and are built with {@link Phrase.Builder}. Phrases are immutable, and,
 * therefore, thread-safe.
 */
@JsonDeserialize(builder = Phrase.Builder.class)
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
    @JsonGetter
    public int program() {
        return this.program;
    }

    /**
     * Returns the a mutable copy of the list of chords that comprise this phrase. Chords are stored
     * sequentially in playback order.
     */
    @JsonGetter
    public List<Chord> chords() {
        return this.chords;
    }

    /**
     * Constructs a {@link Chord} using a Fluent-style builder pattern. By default, the builder will
     * construct an empty piano (program 0) phrase.
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

        public Builder withChords(List<Chord> chords) {
            // The chords may not be null.
            Preconditions.checkNotNull(chords);
            this.chords = chords;
            return this;
        }

        public Phrase build() {
            return new Phrase(this);
        }
    }

}
