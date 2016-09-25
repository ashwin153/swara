package com.swara.music.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A set of chords played sequentially. Voices are combined to form a {@link Phrase}. Voices are
 * built using a {@link Voice.Builder} and are immutable, and, therefore, thread-safe.
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Voice.Builder.class)
public class Voice implements MusicElement {

    private final List<Chord> chords;

    private Voice(Builder builder) {
        this.chords = builder.chords;
    }

    /**
     * Returns a list of the chords that comprise this voice. Chords are stored sequentially in
     * playback order.
     */
    @JsonGetter
    public List<Chord> chords() {
        return this.chords;
    }

    public static final class Builder implements MusicElement.Builder<Voice> {

        private List<Chord> chords;

        public Builder() {
            this.chords = new ArrayList<>();
        }

        public Builder withChord(Chord chord) {
            // The chord must not be null.
            Preconditions.checkNotNull(chord);
            this.chords.add(chord);
            return this;
        }

        public Builder withChords(Collection<? extends Chord> chords) {
            // The chords may not be null.
            Preconditions.checkNotNull(chords);
            this.chords.addAll(chords);
            return this;
        }

        public Voice build() {
            return new Voice(this);
        }
    }

}
