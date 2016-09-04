package com.swara.music.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;

import org.apache.commons.math3.fraction.Fraction;

/**
 * A set of notes played simultaneously for a period of time. Chords are combined together to form a
 * {@link Phrase}. Chords are built using a {@link Chord.Builder} immutable, and, therefore,
 * thread-safe.
 */
@JsonDeserialize(builder = Chord.Builder.class)
public class Chord {

    private final Set<Note> notes;
    private final Fraction duration;

    private Chord(Builder builder) {
        this.notes = builder.notes;
        this.duration = builder.duration;
    }

    /**
     * Returns the notes that comprise the chord. An empty set indicates a musical rest.
     */
    @JsonGetter
    public Set<Note> notes() {
        return this.notes;
    }

    /**
     * Returns the duration of the chord. Like a time signature, the duration is a fraction in which
     * the denominator is a type of note and the numerator is the number of that type of note that
     * the chord takes up. For example, a duration of 3/8 indicates that the chord takes up the time
     * of 3 eighth-notes.
     */
    @JsonGetter
    public Fraction duration() {
        return this.duration;
    }

    /**
     * Constructs a {@link Chord} using a Fluent-style builder pattern. By default, the builder will
     * construct a quarter-rest.
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

        public Builder withNotes(Set<Note> notes) {
            // The notes may not be null.
            Preconditions.checkNotNull(notes);
            this.notes = notes;
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