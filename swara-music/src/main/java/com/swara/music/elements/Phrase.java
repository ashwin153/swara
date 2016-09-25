package com.swara.music.elements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.swara.music.MusicElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A set of voices played simultaneously. Phrases encode musical information for a particular
 * instrument. Phrases are combined together to form a {@link Fragment}. Phrases are built with
 * {@link Phrase.Builder}. Phrases are immutable, and, therefore, thread-safe. The default phrase is
 * an empty set of voices for a grand piano.
 */
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = Phrase.Builder.class)
public class Phrase implements MusicElement {

    public static final int GRAND_PIANO     = 0;
    public static final int KEYBOARD        = 4;
    public static final int ACOUSTIC_GUITAR = 25;
    public static final int ELECTRIC_GUITAR = 27;
    public static final int VIOLIN          = 40;
    public static final int VIOLA           = 41;
    public static final int CELLO           = 42;
    public static final int TRUMPET         = 56;
    public static final int TROMBONE        = 57;
    public static final int TUBA            = 58;
    public static final int FRENCH_HORN     = 60;
    public static final int ALTO_SAX        = 65;
    public static final int TENOR_SAX       = 66;
    public static final int OBOE            = 68;
    public static final int CLARINET        = 71;
    public static final int FLUTE           = 73;
    public static final int APPLAUSE        = 126;

    private final int program;
    private final Set<Voice> voices;

    private Phrase(Builder builder) {
        this.program = builder.program;
        this.voices = builder.voices;
    }

    /**
     * Returns the Midi program (instrument identifier). A Midi controller maps program numbers to
     * instruments using its configured {@link javax.sound.midi.Soundbank}. For example, program
     * numbers 0 is typically mapped to a grand piano. The program number must be on the interval
     * [0, 128).
     */
    @JsonGetter
    public int program() {
        return this.program;
    }

    /**
     * Returns the set of voices that comprise this phrase. Monophonic phrases consist of a single
     * voice, while polyphonic phrases consist of multiple voices sounded simultaneously.
     */
    @JsonGetter
    public Set<Voice> voices() {
        return this.voices;
    }

    public static final class Builder implements MusicElement.Builder<Phrase> {

        private int program;
        private Set<Voice> voices;

        public Builder() {
            this.program = 0;
            this.voices = new HashSet<>();
        }

        public Builder withProgram(int program) {
            // The program must be on the interval [0, 128).
            Preconditions.checkArgument(program >= 0 && program < 128);
            this.program = program;
            return this;
        }

        public Builder withVoice(Voice voice) {
            // The voice must not be null.
            Preconditions.checkNotNull(voice);
            this.voices.add(voice);
            return this;
        }

        public Builder withVoice(Collection<? extends Voice> voices) {
            // The chords may not be null.
            Preconditions.checkNotNull(voices);
            this.voices.addAll(voices);
            return this;
        }

        public Phrase build() {
            return new Phrase(this);
        }
    }

}
