package com.swara.midi;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.math.Fraction;

/**
 * A set of notes played simultaneously for a period of time. Chords are combined together to form a
 * {@link com.swara.midi.Phrase}. Chords are built using a {@link com.swara.midi.Chord.Builder}
 * immutable, and, therefore, thread-safe.
 */
public class Chord {

    private final Set<Note> notes;
    private final Fraction duration;

    public Chord(Builder builder) {
        this.notes = builder.notes;
        this.duration = builder.duration;
    }

    /**
     * Returns the notes that comprise the chord. An empty set indicates a musical rest.
     */
    public Set<Note> notes() {
        return this.notes;
    }

    /**
     * Returns the duration of the chord. Like a time signature, the duration is a fraction in
     * which the denominator is a type of note and the numerator is the number of that type of
     * note that the chord takes up. For example, a duration of 3/8 indicates that the chord takes
     * up the time of 3 eighth-notes.
     */
    public Fraction duration() {
        return this.duration;
    }

    /**
     * Constructs a {@link com.swara.midi.Chord} using a Fluent-style builder pattern. By default,
     * the builder will construct a quarter-rest.
     */
    public static final class Builder {

        private Set<Note> notes;
        private Fraction duration;

        public Builder() {
            this.notes = new HashSet<>();
            this.duration = Fraction.ONE_QUARTER;
        }

        public Builder withNote(Note note) {
            // The note must not be null.
            Preconditions.checkNotNull(note);
            this.notes.add(note);
            return this;
        }

        public Builder withDuration(Fraction duration) {
            // The duration must be positive and non-null.
            Preconditions.checkNotNull(duration);
            Preconditions.checkArgument(duration.compareTo(Fraction.ZERO) > 0);
            this.duration = duration;
            return this;
        }

        public Chord build() {
            return new Chord(this);
        }
    }

}