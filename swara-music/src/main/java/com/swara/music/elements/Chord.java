package com.swara.music.elements;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import org.apache.commons.math3.fraction.Fraction;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A set of notes sounded simultaneously at a particular volume for a particular duration. Chords,
 * therefore, encode the rhythmic, dynamic, and harmonic properties of a sequence of notes. Chords
 * are combined together to form a {@link Voice}. A sequence of chords forms a chord progression or
 * {@link com.swara.music.features.Harmony}. Chords are built using a {@link Chord.Builder} and are
 * immutable, and, therefore thread-safe. The default chord is a quarter-rest.
 */
@ToString
@EqualsAndHashCode(exclude = { "volume" })
@JsonDeserialize(builder = Chord.Builder.class)
public class Chord implements MusicElement, Serializable {

    private static final long serialVersionUID = 4074771317712567295L;

    private final Set<Note> notes;
    private final Fraction duration;
    private final int volume;

    private Chord(Builder builder) {
        this.notes = builder.notes;
        this.duration = builder.duration;
        this.volume = builder.volume;
    }

    /**
     * Returns the duration of the chord. A duration is represented as a fraction in which the
     * denominator is a type of note and the numerator is the number of that type of note that the
     * chord takes up. For example, a duration of 3/8 indicates that the chord takes up the time of
     * 3 eighth-notes.
     */
    @JsonGetter
    public Fraction duration() {
        return this.duration;
    }

    /**
     * Returns the volume of the chord. Volume is synonymous with MIDI velocity. The volume is
     * encoded as a number on the interval [0, 128), in which 0 represents silence and 127
     * represents maximum volume.
     */
    @JsonGetter
    public int volume() {
        return this.volume;
    }

    /**
     * Returns the notes that comprise the chord. A chord may contain any number of notes, or no
     * notes at all; in fact, a musical rest is simply a chord with no notes.
     */
    @JsonGetter
    public Set<Note> notes() {
        return this.notes;
    }

    /**
     * Returns the type of the chord. Because chord theory is so vast and complex, a simple but
     * expressive alternative is necessary. Chords are partitioned into equivalence classes or types
     * by the ordered set of the pitches of the notes that compose it. For example, all C Major
     * Chords belong to the family {0, 4, 7}. Because these equivalence classes are ordered by
     * construction, they can be compared lexicographically. These types are expressive enough to
     * capture differences in number of notes, added tones, and harmonic content; however, they fail
     * to discriminate between the various inversions of a chord. Partitioning chords into types
     * dramatically reduces the state space and makes a variety of machine learning methods more
     * tractable.
     */
    @Transient
    public Set<Integer> type() {
        return this.notes.stream()
            .map(Note::pitch)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    public static final class Builder implements MusicElement.Builder<Chord> {

        private Set<Note> notes;
        private Fraction duration;
        private int volume;

        public Builder() {
            this.notes = new TreeSet<>();
            this.duration = Fraction.ONE_QUARTER;
            this.volume = 64;
        }

        public Builder withNote(Note note) {
            // The note must not be null.
            Preconditions.checkNotNull(note);
            this.notes.add(note);
            return this;
        }

        public Builder withNotes(Collection<? extends Note> notes) {
            // The notes may not be null.
            Preconditions.checkNotNull(notes);
            this.notes.addAll(notes);
            return this;
        }

        public Builder withDuration(Fraction duration) {
            // The duration must be positive and non-null.
            Preconditions.checkNotNull(duration);
            Preconditions.checkArgument(duration.compareTo(Fraction.ZERO) > 0);
            this.duration = duration;
            return this;
        }

        public Builder withDuration(int beats, int type) {
            // Beats and type must both be positive and type must be a power of 2.
            Preconditions.checkArgument(beats > 0 && type > 0);
            Preconditions.checkArgument((type & -type) == type);
            this.duration = new Fraction(beats, type);
            return this;
        }

        public Builder withVolume(int volume) {
            // The volume must be on the interval [0, 128).
            Preconditions.checkArgument(volume >= 0 && volume < 128);
            this.volume = volume;
            return this;
        }

        public Chord build() {
            return new Chord(this);
        }
    }

}